/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.ddd.repository;

import aoc.ddd.model.DomainAggregate;
import aoc.ddd.model.DomainId;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings({"squid:S00119"})
public abstract class JsonDomainRepository
        <AGG extends DomainAggregate<AGG, ID>, ID extends DomainId<String>>
        implements DomainRepository<AGG, ID> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final Class<AGG> aggClass;

    private final Class<ID> idClass;

    private final Path idSequenceFilename;

    private final Path aggregateStoragePath;

    private final ObjectMapper objectMapper;

    /**
     * Lock operations on ID sequence (file)
     */
    private final ReentrantLock idSequenceLock = new ReentrantLock();

    /* TODO Fine granular locking
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = readWriteLock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = readWriteLock.writeLock();
    */

    public JsonDomainRepository(final Class<AGG> aggClass, final Class<ID> idClass,
                                final Path storagePath) {
        Objects.requireNonNull(aggClass);
        this.aggClass = aggClass;
        Objects.requireNonNull(idClass);
        this.idClass = idClass;
        idSequenceFilename = Path.of("idsequence.json");
        Objects.requireNonNull(storagePath);
        logger.trace("Initizialing with default storage path '{}'", storagePath.toAbsolutePath());
        aggregateStoragePath = storagePath
                .resolve(String.format("repository/%s", aggClass.getSimpleName()));
        try {
            Files.createDirectories(aggregateStoragePath);
        } catch (IOException e) {
            throw new DomainRepositoryException("Cannot create storage directory " + aggregateStoragePath, e);
        }
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        /* TODO objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);*/
        logger.debug("Initialized with aggregate storage path '{}'", aggregateStoragePath.toAbsolutePath());
    }

    /**
     * Just create storage filename (w/o aggregate storage path)
     */
    private Path makeStorageFilenameFrom(final ID domainId) {
        final String sane = domainId.getValue()
                .replaceAll("[^a-zA-Z0-9., ]+", "_");
        return Path.of(String.format("%s.json", sane));
    }

    private ID makeDomainIdFrom(final Path path) {
        final String[] sane = path.getFileName().toString().split("[.]");
        try {
            return idClass.getDeclaredConstructor(String.class)
                    .newInstance(sane[0]);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new DomainRepositoryException("", e);
        }
    }

    private Path fqAggregatePathFrom(final ID domainId) {
        return aggregateStoragePath.resolve(makeStorageFilenameFrom(domainId));
    }

    private Stream<Path> aggregatePathsAsStream() throws IOException {
        return Files.list(aggregateStoragePath)
                .filter(p -> p.getFileName().toString().endsWith(".json")
                        && !p.getFileName().equals(idSequenceFilename));
    }

    @Override
    public ID nextIdentity() {
        return nextIdentity("");
    }

    @Override
    public ID nextIdentity(final String prefix) {
        Objects.requireNonNull(prefix);
        logger.trace("Generating next id for aggregate {}", aggClass);
        final Path idSequencePath = aggregateStoragePath.resolve(idSequenceFilename);
        ID id;
        try {
            if (idSequenceLock.tryLock()
                    || idSequenceLock.tryLock(5, TimeUnit.SECONDS)) {
                final DomainIdSequence domainIdSequence;
                if (!Files.exists(idSequencePath)) {
                    // TODO Strategy domainIdSequence = new DomainIdSequence(0);
                    domainIdSequence = initializeDomainIdCountingExistingAggregates(idSequencePath);
                } else {
                    domainIdSequence = loadExistingIdSequence(idSequencePath);
                }
                id = incrementAndStoreIdSequence(prefix, idSequencePath, domainIdSequence);
            } else {
                throw new DomainRepositoryException("Could not lock " + idSequenceLock
                        + " while trying to get next ID for prefix=" + prefix
                        + " idSequencePath=" + idSequencePath);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DomainRepositoryException(e);
        } finally {
            if (idSequenceLock.isLocked()) {
                idSequenceLock.unlock();
            }
        }
        return id;
    }

    private ID incrementAndStoreIdSequence(final String prefix, final Path idSequencePath,
                                           final DomainIdSequence domainIdSequence) {
        try {
            final Constructor<ID> declaredConstructor = idClass.
                    getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
            final String trim = prefix.trim();
            final String prefixAndId = trim.length() > 1
                    ? String.format("%s_%s", trim, domainIdSequence.incrementAndGetAsHex())
                    : domainIdSequence.incrementAndGetAsHex();
            final ID id = declaredConstructor.newInstance(prefixAndId);
            objectMapper.writeValue(idSequencePath.toFile(), domainIdSequence);
            idSequenceLock.unlock();
            logger.debug("Generated next id '{}'", id);
            return id;
        } catch (InstantiationException | IllegalAccessException
                | InvocationTargetException | NoSuchMethodException e) {
            throw new DomainRepositoryException(e);
        } catch (IOException e) {
            throw new DomainRepositoryException("Could not store next id", e);
        }
    }

    private DomainIdSequence loadExistingIdSequence(final Path idSequencePath) {
        Objects.requireNonNull(idSequencePath);
        final DomainIdSequence domainIdSequence;
        logger.trace("Loading existing IdSequence for {}", aggClass);
        try {
            domainIdSequence = objectMapper.readValue(
                    idSequencePath.toFile(), DomainIdSequence.class);
        } catch (IOException e) {
            throw new DomainRepositoryException("Could not initialize idSequence file " +
                    idSequencePath, e);
        }
        return domainIdSequence;
    }

    private DomainIdSequence initializeDomainIdCountingExistingAggregates(final Path idSequencePath) {
        Objects.requireNonNull(idSequencePath);
        logger.trace("Initializing IdSequence at '{}' with count of existing aggregates",
                idSequencePath);
        try {
//            idSequenceLock.tryLock(1, TimeUnit.SECONDS);
            Files.createFile(idSequencePath);
            final DomainIdSequence domainIdSequence = new DomainIdSequence(countAll());
            objectMapper.writeValue(idSequencePath.toFile(), domainIdSequence);
//            idSequenceLock.unlock();
            logger.debug("Initialized IdSequence at '{}' counting existing aggregates, {}",
                    idSequencePath, domainIdSequence);
            return domainIdSequence;
        } catch (IOException e) {
//            idSequenceLock.unlock();
            throw new DomainRepositoryException("Could not initialize IdSequence at " +
                    idSequencePath, e);
        } /*catch (InterruptedException e) {
            idSequenceLock.unlock();
            throw new DomainRepositoryException(e);
        }*/
    }

    @Override
    public AGG create() {
        try {
            return aggClass.getDeclaredConstructor(idClass)
                    .newInstance(nextIdentity());
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new DomainRepositoryException(e);
        }
    }

    private AGG create(final ID domainId) {
        Objects.requireNonNull(domainId);
        if (load(domainId).isPresent()) {
            throw new DomainRepositoryException("ID " + domainId + " already exists");
        } else {
            try {
                return aggClass.getDeclaredConstructor(idClass).newInstance(domainId);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                throw new DomainRepositoryException(e);
            }
        }
    }

    @Override
    public AGG save(final AGG aggregate) {
        Objects.requireNonNull(aggregate);
        try {
            final long version = aggregate.incVersion();
            final Path path = fqAggregatePathFrom(aggregate.getDomainId());
            logger.trace("Saving aggregate {} with id '{}@{}' at '{}'",
                    aggClass, aggregate.getDomainId(), version, path);
            final byte[] bytes = objectMapper.writeValueAsBytes(aggregate);
            Files.write(path, bytes,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            logger.debug("Saved aggregate {} with id '{}@{}' and data: {} at '{}'",
                    aggClass, aggregate.getDomainId(), version, aggregate, path);
            return aggregate;
        } catch (IOException e) {
            throw new DomainRepositoryException(e);
        }
    }

    @Override
    public Set<AGG> saveAll(final Set<AGG> aggregates) {
        Objects.requireNonNull(aggregates);
        logger.trace("Saving {} aggregates", aggregates.size());
        final Set<AGG> ids = aggregates.stream()
                .map(this::save)
                .collect(Collectors.toCollection(TreeSet::new));
        logger.debug("Saved {} aggregates", aggregates.size());
        return ids;
    }

    @Override
    public Optional<AGG> load(final ID domainId) {
        Objects.requireNonNull(domainId);
        logger.trace("Loading aggregate of type {} with id '{}'", aggClass, domainId);
        final Path storagePath = fqAggregatePathFrom(domainId);
        try {
            final byte[] bytes = Files.readAllBytes(storagePath);
            final AGG aggregate = objectMapper.readValue(bytes, aggClass);
            logger.debug("Loaded aggregate of type {} with id '{}@{}' toString={}",
                    aggClass, aggregate.getDomainId(), aggregate.getVersion(), aggregate);
            return Optional.of(aggregate);
        } catch (NoSuchFileException e) {
            logger.warn("Domain id '{}' for {} not found", domainId, aggClass);
            return Optional.empty();
        } catch (IOException e) {
            throw new DomainRepositoryException(e);
        }
    }

    private Optional<AGG> load(final Path path) {
        Objects.requireNonNull(path);
        return load(makeDomainIdFrom(path));
    }

    //@Override
    /* TODO public AGG loadOrCreate(final ID domainId) {
        return load(domainId).orElseGet(() -> create(domainId));
    }*/

    @Override
    public <SUBT extends AGG> Optional<SUBT> load(final ID domainId, final Class<SUBT> subklass) {
        Objects.requireNonNull(domainId);
        Objects.requireNonNull(subklass);
        logger.trace("Loading {} with domain id '{}'", subklass, domainId);
        final Path storagePath = fqAggregatePathFrom(domainId);
        if (Files.exists(storagePath)) {
            try {
                final byte[] bytes = Files.readAllBytes(storagePath);
                final SUBT aggregate = objectMapper.readValue(bytes, subklass);
                logger.debug("Loaded aggregate {} with domain id '{}', data: {}",
                        aggClass, aggregate.getDomainId(), aggregate);
                return Optional.of(aggregate);
            } catch (IOException e) {
                throw new DomainRepositoryException(e);
            }
        } else {
            logger.debug("Domain id '{}' for {} not found", domainId, aggClass);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Set<AGG>> loadAll() {
        try (final Stream<Path> stream = aggregatePathsAsStream()) {
            return getAggs(stream);
        } catch (IOException e) {
            throw new DomainRepositoryException("Cannot load all aggregates of type " + aggClass, e);
        }
    }

    @Override
    public Optional<Set<AGG>> loadAllWithPrefix(final String domainIdPrefix) {
        try (final Stream<Path> stream = aggregatePathsAsStream()
                .filter(path -> path.startsWith(domainIdPrefix))) {
            return getAggs(stream);
        } catch (IOException e) {
            throw new DomainRepositoryException("Cannot load all aggregates of type " + aggClass, e);
        }
    }

    private Optional<Set<AGG>> getAggs(final Stream<Path> stream) {
        return Optional.of(stream.map(this::load)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet()));
    }

    /**
     * @deprecated Use #loadAllWithPrefix(String), NOTE: uses .startsWith(String)
     */
    @Deprecated
    @Override
    @SuppressWarnings({"unchecked"})
    public final Optional<Set<AGG>> findByPrefix(final String domainIdPrefix) {
        Objects.requireNonNull(domainIdPrefix);
        return loadAllWithPrefix(domainIdPrefix);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public final Optional<Set<AGG>> find(final QueryPredicate... queryPredicates) {
        Objects.requireNonNull(queryPredicates);
        return filterAggs(queryPredicates, loadAll().orElseThrow());
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public final Optional<Set<AGG>> findByPrefixAndPredicate(final String domainIdPrefix,
                                                             final QueryPredicate... queryPredicates) {
        Objects.requireNonNull(domainIdPrefix);
        Objects.requireNonNull(queryPredicates);
        return filterAggs(queryPredicates, loadAllWithPrefix(domainIdPrefix).orElseThrow());
    }

    private Optional<Set<AGG>> filterAggs(final QueryPredicate[] queryPredicates,
                                          final Set<AGG> aggs) {
        return Optional.of(aggs.stream()
                .filter(agg -> Arrays.stream(queryPredicates)
                        .anyMatch(predicate -> {
                            try {
                                final String fieldName = predicate.getField();
                                final String getter = String.format("get%s%s",
                                        fieldName.substring(0, 1).toUpperCase(), fieldName.substring(1));
                                final Method method = agg.getClass().getMethod(getter);
                                final Object value = method.invoke(agg);
                                logger.trace("{}.equals({})", value, predicate.getValue());
                                return predicate.isSatisfied(value.toString());
                            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                                logger.error("", e);
                                return false;
                            }
                        }))
                .collect(Collectors.toSet()));
    }

    @Override
    public long countAll() {
        logger.trace("Counting all instances of aggregate type {}", aggClass);
        try (final Stream<Path> stream = aggregatePathsAsStream()) {
            final long count = stream.count();
            logger.debug("Found {} instances of aggregate type {}", count, aggClass);
            return count;
        } catch (IOException e) {
            throw new DomainRepositoryException("Cannot count all instances of aggregate " +
                    aggClass, e);
        }
    }

    @Override
    public void delete(final ID id) {
        logger.trace("Trying to delete domain id '{}'", id);
        try {
            Files.deleteIfExists(fqAggregatePathFrom(id));
        } catch (IOException e) {
            throw new DomainRepositoryException(String.format(
                    "Cannot delete aggregate with domain id '%s'", id),
                    e);
        }
    }

    @Override
    public void delete(final Set<AGG> aggregates) {
        Objects.requireNonNull(aggregates);
        logger.trace("Deleting {} aggregates", aggregates.size());
        aggregates.forEach(this::delete);
        logger.debug("Deleted {} aggregates", aggregates.size());
    }

}
