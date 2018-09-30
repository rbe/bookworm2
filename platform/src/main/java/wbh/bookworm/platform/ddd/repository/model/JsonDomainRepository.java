/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.platform.ddd.repository.model;

import wbh.bookworm.platform.ddd.model.DomainAggregate;
import wbh.bookworm.platform.ddd.model.DomainId;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

@SuppressWarnings({"squid:S00119"})
public abstract class JsonDomainRepository
        <T extends DomainAggregate<T, ID>, ID extends DomainId<String>>
        implements DomainRepository<T, ID> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonDomainRepository.class);

    private final Class<T> klass;

    private final Class<ID> idKlass;

    private final Path idSequenceFilename;

    private final Path aggregateStoragePath;

    private final ObjectMapper objectMapper;

    public JsonDomainRepository(final Class<T> klass, final Class<ID> idKlass) {
        this(klass, idKlass, Paths.get("."));
    }

    public JsonDomainRepository(final Class<T> klass, final Class<ID> idKlass,
                                final Path defaultStoragePath) {
        Objects.requireNonNull(klass);
        this.klass = klass;
        Objects.requireNonNull(idKlass);
        this.idKlass = idKlass;
        idSequenceFilename = Paths.get("idsequence.json");
        Objects.requireNonNull(defaultStoragePath);
        LOGGER.trace("Initizialing with default storage path {}", defaultStoragePath.toAbsolutePath());
        aggregateStoragePath = defaultStoragePath
                .resolve(this.getClass().getSimpleName())
                .resolve(Paths.get(String.format("%s/%s", klass.getPackageName(), klass.getSimpleName())));
        try {
            Files.createDirectories(aggregateStoragePath);
        } catch (IOException e) {
            throw new DomainRepositoryException("Cannot create storage directory " + aggregateStoragePath, e);
        }
        this.objectMapper = new ObjectMapper();
        /* TODO objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);*/
        LOGGER.info("Initialized with aggregate storage path {}", aggregateStoragePath.toAbsolutePath());
    }

    private Path makeStorageFilename(final ID domainId) {
        final String sane = domainId.getValue()
                .replaceAll("[^a-zA-Z0-9., ]+", "_");
        return Paths.get(String.format("%s.json", sane));
    }

    private Path fullyQualifiedAggregatePath(final ID domainId) {
        return aggregateStoragePath.resolve(makeStorageFilename(domainId));
    }

    @Override
    public ID nextId() {
        final Path idSequencePath = aggregateStoragePath.resolve(idSequenceFilename);
        synchronized (klass) {
            final IdSequence idSequence;
            if (!Files.exists(idSequencePath)) {
                // idSequence = new IdSequence(0);
                idSequence = initializeIdSequenceCountExistingAggregates(idSequencePath);
            } else {
                LOGGER.trace("Loading existing IdSequence for {}", klass);
                try {
                    idSequence = objectMapper.readValue(idSequencePath.toFile(), IdSequence.class);
                } catch (IOException e) {
                    throw new DomainRepositoryException("Could not initialize idSequence file " +
                            idSequencePath, e);
                }
            }
            try {
                final Constructor<ID> declaredConstructor = idKlass.getDeclaredConstructor(String.class);
                declaredConstructor.setAccessible(true);
                final ID id = declaredConstructor.newInstance(idSequence.incrementAndGetAsHex());
                objectMapper.writeValue(idSequencePath.toFile(), idSequence);
                return id;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new DomainRepositoryException(e);
            } catch (IOException e) {
                throw new DomainRepositoryException("Could not store next id", e);
            }
        }
    }

    private IdSequence initializeIdSequenceCountExistingAggregates(final Path idSequencePath) {
        LOGGER.trace("Initializing IdSequence with count of existing aggregates");
        try {
            Files.createFile(idSequencePath);
            final IdSequence idSequence = new IdSequence(countAll());
            objectMapper.writeValue(idSequencePath.toFile(), idSequence);
            LOGGER.warn("Initialized IdSequence counting existing aggregates, {}", idSequence);
            return idSequence;
        } catch (IOException e) {
            throw new DomainRepositoryException("Could not store next id", e);
        }
    }

    @Override
    public ID save(final T aggregate) {
        Objects.requireNonNull(aggregate);
        LOGGER.trace("Saving aggregate {} with domain id '{}'", aggregate, aggregate.getDomainId());
        try {
            final byte[] bytes = objectMapper.writeValueAsBytes(aggregate);
            final Path path = fullyQualifiedAggregatePath(aggregate.getDomainId());
            Files.write(path, bytes,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            LOGGER.debug("Saved {} with domain id '{}'", aggregate, aggregate.getDomainId());
            return aggregate.getDomainId();
        } catch (IOException e) {
            throw new DomainRepositoryException(e);
        }
    }

    @Override
    public Set<ID> saveAll(final Set<T> aggregates) {
        Objects.requireNonNull(aggregates);
        LOGGER.trace("Saving {} aggregates", aggregates.size());
        final Set<ID> ids = new TreeSet<>();
        for (final T aggregate : aggregates) {
            ids.add(save(aggregate));
        }
        LOGGER.debug("Saved {} aggregates", aggregates.size());
        return ids;
    }

    @Override
    public Optional<T> load(final ID domainId) {
        Objects.requireNonNull(domainId);
        LOGGER.trace("Loading {} with domain id '{}'", klass, domainId);
        final Path path = fullyQualifiedAggregatePath(domainId);
        try {
            final byte[] bytes = Files.readAllBytes(path);
            final T readValue = objectMapper.readValue(bytes, klass);
            LOGGER.debug("Loaded {} with domain id '{}'", readValue, readValue.getDomainId());
            return Optional.of(readValue);
        } catch (NoSuchFileException e) {
            LOGGER.warn("Domain id '{}' for {} not found", domainId, klass);
            return Optional.empty();
        } catch (IOException e) {
            throw new DomainRepositoryException(e);
        }
    }

    @Override
    public <SUBT extends T> Optional<SUBT> load(final ID domainId, final Class<SUBT> subklass) {
        Objects.requireNonNull(domainId);
        Objects.requireNonNull(subklass);
        LOGGER.trace("Loading {} with domain id '{}'", subklass, domainId);
        final Path path = fullyQualifiedAggregatePath(domainId);
        try {
            final byte[] bytes = Files.readAllBytes(path);
            final SUBT readValue = objectMapper.readValue(bytes, subklass);
            LOGGER.debug("Loaded {} with domain id '{}'", readValue, readValue.getDomainId());
            return Optional.of(readValue);
        } catch (NoSuchFileException e) {
            LOGGER.warn("Domain id '{}' for {} not found", domainId, klass);
            return Optional.empty();
        } catch (IOException e) {
            throw new DomainRepositoryException(e);
        }
    }

    @Override
    public Optional<Set<T>> loadAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long countAll() {
        try (final Stream<Path> stream = Files.list(aggregateStoragePath)) {
            return stream
                    .filter(p -> !p.getFileName().equals(idSequenceFilename))
                    .count();
        } catch (IOException e) {
            throw new DomainRepositoryException("Cannot count all aggregates", e);
        }
    }

}
