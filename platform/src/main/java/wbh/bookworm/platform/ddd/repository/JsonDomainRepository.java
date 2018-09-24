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
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public abstract class JsonDomainRepository<T extends DomainAggregate<?>> implements DomainRepository<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonDomainRepository.class);

    private final Class<T> klass;

    private final Path defaultStoragePath;

    private final ObjectMapper objectMapper;

    public JsonDomainRepository(final Class<T> klass) {
        this(klass, Paths.get("."));
    }

    public JsonDomainRepository(final Class<T> klass, final Path defaultStoragePath) {
        Objects.requireNonNull(klass);
        this.klass = klass;
        Objects.requireNonNull(defaultStoragePath);
        LOGGER.trace("Initizialing with default storage path {}", defaultStoragePath.toAbsolutePath());
        this.defaultStoragePath = defaultStoragePath;
        this.objectMapper = new ObjectMapper();
        LOGGER.info("Initialized with default storage path {}", defaultStoragePath.toAbsolutePath());
    }

    private String makeFilename(final Class<?> klass, final DomainId<?> domainId) {
        return String.format("%s-%s.json", klass.getName(), domainId.getValue());
    }

    @Override
    public void save(final T aggregate) {
        Objects.requireNonNull(aggregate);
        LOGGER.trace("Saving aggregate {} with domain id '{}'", aggregate, aggregate.getDomainId());
        try {
            final byte[] bytes = objectMapper.writeValueAsBytes(aggregate);
            final Path path = defaultStoragePath.resolve(
                    makeFilename(aggregate.getClass(), aggregate.getDomainId()));
            Files.write(path, bytes,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            LOGGER.debug("Saved {} with domain id '{}'", aggregate, aggregate.getDomainId());
        } catch (IOException e) {
            throw new DomainRepositoryException(e);
        }
    }

    @Override
    public void saveAll(final Set<T> aggregates) {
        Objects.requireNonNull(aggregates);
        LOGGER.trace("Saving {} aggregates", aggregates.size());
        for (final T aggregate : aggregates) {
            save(aggregate);
        }
        LOGGER.debug("Saved {} aggregates", aggregates.size());
    }

    @Override
    public Optional<T> load(final DomainId<?> domainId) {
        Objects.requireNonNull(domainId);
        LOGGER.trace("Loading {} with domain id '{}'", klass, domainId);
        final Path path = defaultStoragePath.resolve(makeFilename(klass, domainId));
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
    public <R extends T> Optional<R> load(final DomainId<?> domainId, final Class<R> subklass) {
        Objects.requireNonNull(domainId);
        Objects.requireNonNull(subklass);
        LOGGER.trace("Loading {} with domain id '{}'", subklass, domainId);
        final Path path = defaultStoragePath.resolve(makeFilename(subklass, domainId));
        try {
            final byte[] bytes = Files.readAllBytes(path);
            final R readValue = objectMapper.readValue(bytes, subklass);
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

}
