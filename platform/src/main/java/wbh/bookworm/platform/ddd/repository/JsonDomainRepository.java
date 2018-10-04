/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.platform.ddd.repository;

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
        <AGG extends DomainAggregate<AGG, ID>, ID extends DomainId<String>>
        implements DomainRepository<AGG, ID> {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final Class<AGG> klass;

    private final Class<ID> idKlass;

    private final Path idSequenceFilename;

    private final Path aggregateStoragePath;

    private final ObjectMapper objectMapper;

    public JsonDomainRepository(final Class<AGG> klass, final Class<ID> idKlass) {
        this(klass, idKlass, Paths.get("."));
    }

    public JsonDomainRepository(final Class<AGG> klass, final Class<ID> idKlass,
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
        return nextId("");
    }

    @Override
    public ID nextId(final String prefix) {
        Objects.requireNonNull(prefix);
        LOGGER.trace("Generating next id for aggregate {}", klass);
        final Path idSequencePath = aggregateStoragePath.resolve(idSequenceFilename);
        synchronized (klass) {
            final IdSequence idSequence;
            if (!Files.exists(idSequencePath)) {
                // TODO Strategy idSequence = new IdSequence(0);
                idSequence = initializeIdSequenceCountingExistingAggregates(idSequencePath);
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
                final ID id = declaredConstructor.newInstance(
                        String.format("%s_%s", prefix, idSequence.incrementAndGetAsHex()));
                objectMapper.writeValue(idSequencePath.toFile(), idSequence);
                LOGGER.debug("Generated next id '{}'", id);
                return id;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new DomainRepositoryException(e);
            } catch (IOException e) {
                throw new DomainRepositoryException("Could not store next id", e);
            }
        }
    }

    private IdSequence initializeIdSequenceCountingExistingAggregates(final Path idSequencePath) {
        LOGGER.trace("Initializing IdSequence at '{}' with count of existing aggregates",
                idSequencePath);
        try {
            Files.createFile(idSequencePath);
            final IdSequence idSequence = new IdSequence(countAll());
            objectMapper.writeValue(idSequencePath.toFile(), idSequence);
            LOGGER.debug("Initialized IdSequence at '{}' counting existing aggregates, {}",
                    idSequencePath, idSequence);
            return idSequence;
        } catch (IOException e) {
            throw new DomainRepositoryException("Could not initialize IdSequence at " +
                    idSequencePath, e);
        }
    }

    @Override
    public ID save(final AGG aggregate) {
        Objects.requireNonNull(aggregate);
        LOGGER.trace("Saving aggregate {} with domain id '{}'", aggregate, aggregate.getDomainId());
        try {
            final byte[] bytes = objectMapper.writeValueAsBytes(aggregate);
            final Path path = fullyQualifiedAggregatePath(aggregate.getDomainId());
            Files.write(path, bytes,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            LOGGER.debug("Saved aggregate with domain id '{}' and data: {}", aggregate.getDomainId(), aggregate);
            return aggregate.getDomainId();
        } catch (IOException e) {
            throw new DomainRepositoryException(e);
        }
    }

    @Override
    public Set<ID> saveAll(final Set<AGG> aggregates) {
        Objects.requireNonNull(aggregates);
        LOGGER.trace("Saving {} aggregates", aggregates.size());
        final Set<ID> ids = new TreeSet<>();
        for (final AGG aggregate : aggregates) {
            ids.add(save(aggregate));
        }
        LOGGER.debug("Saved {} aggregates", aggregates.size());
        return ids;
    }

    @Override
    public Optional<AGG> load(final ID domainId) {
        Objects.requireNonNull(domainId);
        LOGGER.trace("Loading aggregate {} with domain id '{}'", klass, domainId);
        final Path path = fullyQualifiedAggregatePath(domainId);
        try {
            final byte[] bytes = Files.readAllBytes(path);
            final AGG aggregate = objectMapper.readValue(bytes, klass);
            LOGGER.debug("Loaded aggregate {} with domain id '{}' and data: {}",
                    klass, aggregate.getDomainId(), aggregate);
            return Optional.of(aggregate);
        } catch (NoSuchFileException e) {
            LOGGER.warn("Domain id '{}' for {} not found", domainId, klass);
            return Optional.empty();
        } catch (IOException e) {
            throw new DomainRepositoryException(e);
        }
    }

    @Override
    public <SUBT extends AGG> Optional<SUBT> load(final ID domainId, final Class<SUBT> subklass) {
        Objects.requireNonNull(domainId);
        Objects.requireNonNull(subklass);
        LOGGER.trace("Loading {} with domain id '{}'", subklass, domainId);
        final Path path = fullyQualifiedAggregatePath(domainId);
        try {
            final byte[] bytes = Files.readAllBytes(path);
            final SUBT aggregate = objectMapper.readValue(bytes, subklass);
            LOGGER.debug("Loaded aggregate {} with domain id '{}', data: {}",
                    klass, aggregate.getDomainId(), aggregate);
            return Optional.of(aggregate);
        } catch (NoSuchFileException e) {
            LOGGER.warn("Domain id '{}' for {} not found", domainId, klass);
            return Optional.empty();
        } catch (IOException e) {
            throw new DomainRepositoryException(e);
        }
    }

    @Override
    public Optional<Set<AGG>> loadAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long countAll() {
        try (final Stream<Path> stream = Files.list(aggregateStoragePath)) {
            return stream
                    .filter(p -> !p.getFileName().equals(idSequenceFilename))
                    .count();
        } catch (IOException e) {
            throw new DomainRepositoryException("Cannot count all instances of aggregate " +
                    klass, e);
        }
    }

}
