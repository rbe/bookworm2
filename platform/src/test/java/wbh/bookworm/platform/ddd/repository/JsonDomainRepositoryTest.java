/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.platform.ddd.repository;

import wbh.bookworm.platform.ddd.model.DomainAggregate;
import wbh.bookworm.platform.ddd.model.DomainEntity;
import wbh.bookworm.platform.ddd.model.DomainId;
import wbh.bookworm.platform.ddd.repository.model.JsonDomainRepository;
import wbh.bookworm.platform.ddd.repository.search.SearchTestAppConfig;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {SearchTestAppConfig.class})
@ExtendWith(SpringExtension.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class JsonDomainRepositoryTest {

    @Test
    void should0PersistAndLoadAggregateWithGeneratedDomainId() {
        final AnAggregateRepository anAggregateRepository = new AnAggregateRepository();

        final AnEntity anEntity = new AnEntity(new AnEntityDomainId("Nancy"), "Huhu Swietbert");
        final AnAggregateDomainId domainId = anAggregateRepository.nextId();
        final AnAggregate anAggregate = new AnAggregate(domainId, anEntity);
        anAggregateRepository.save(anAggregate);

        final Optional<AnAggregate> loadedAggregate = anAggregateRepository.load(domainId);
        assertTrue(loadedAggregate.isPresent());
        assertEquals(domainId, loadedAggregate.get().getDomainId());
        assertEquals(loadedAggregate.get().getAnEntity().getBla(), "Huhu Swietbert");
    }

    @Test
    void should1PersistDomainAggregate() {
        final AnAggregateRepository anAggregateRepository = new AnAggregateRepository();
        final AnEntity anEntity = new AnEntity(new AnEntityDomainId("Nancy"), "Huhu Swietbert");
        final AnAggregate anAggregate = new AnAggregate(new AnAggregateDomainId("Ralf"), anEntity);
        anAggregateRepository.save(anAggregate);
    }

    @Test
    void should2LoadDomainAggregate() {
        final AnAggregateDomainId domainId = new AnAggregateDomainId("Ralf");
        final Optional<AnAggregate> anAggregate = new AnAggregateRepository().load(domainId);
        assertTrue(anAggregate.isPresent());
        assertEquals(domainId, anAggregate.get().getDomainId());
        assertEquals(anAggregate.get().getAnEntity().getBla(), "Huhu Swietbert");
    }

    @Test
    void should3CountAllAggregates() {
        final AnAggregateRepository anAggregateRepository = new AnAggregateRepository();
        assertEquals(1, anAggregateRepository.countAll());
    }

    private static class AnAggregateRepository extends JsonDomainRepository<AnAggregate, AnAggregateDomainId> {

        AnAggregateRepository() {
            super(AnAggregate.class, AnAggregateDomainId.class, Paths.get("target"));
        }

    }

    //@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
    //@JsonTypeName("ADomainId")
    private static class AnAggregateDomainId extends DomainId<String> {

        @JsonCreator
        AnAggregateDomainId(final @JsonProperty("value") String value) {
            super(value);
        }

    }

    private static class AnAggregate extends DomainAggregate<AnAggregate, AnAggregateDomainId> {

        private static final long serialVersionUID = -1L;

        @JsonProperty
        AnEntity anEntity;

        @JsonCreator
        AnAggregate(final @JsonProperty("domainId") AnAggregateDomainId domainId,
                    final @JsonProperty("anEntity") AnEntity anEntity) {
            super(domainId);
            this.anEntity = anEntity;
        }

        AnEntity getAnEntity() {
            return anEntity;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final AnAggregate that = (AnAggregate) o;
            return Objects.equals(anEntity, that.anEntity);
        }

        @Override
        public int hashCode() {
            return Objects.hash(domainId, anEntity);
        }

        @Override
        public int compareTo(final AnAggregate o) {
            /* TODO Comparable */
            return 0;
        }

        @Override
        public String toString() {
            return String.format("AnAggregate{domainId=%s, anEntity=%s}", domainId, anEntity);
        }

    }

    private static class AnEntityDomainId extends DomainId<String> {

        @JsonCreator
        AnEntityDomainId(final @JsonProperty("value") String value) {
            super(value);
        }

    }

    private static class AnEntity extends DomainEntity<AnEntity, AnEntityDomainId> {

        private static final long serialVersionUID = -1L;

        @JsonProperty
        private String bla;

        @JsonCreator
        AnEntity(final @JsonProperty("domainId") AnEntityDomainId domainId,
                 final @JsonProperty("bla") String bla) {
            super(domainId);
            this.bla = bla;
        }

        String getBla() {
            return bla;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final AnEntity anEntity = (AnEntity) o;
            return Objects.equals(bla, anEntity.bla);
        }

        @Override
        public int hashCode() {
            return Objects.hash(bla);
        }

        @Override
        public int compareTo(final AnEntity o) {
            /* TODO Comparable */
            return 0;
        }

        @Override
        public String toString() {
            return "AnEntity{" +
                    "bla='" + bla + '\'' +
                    ", domainId=" + domainId +
                    '}';
        }

    }

}
