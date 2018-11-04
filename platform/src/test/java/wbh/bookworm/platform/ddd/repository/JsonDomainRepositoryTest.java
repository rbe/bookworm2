/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.platform.ddd.repository;

import wbh.bookworm.platform.ddd.model.DomainAggregate;
import wbh.bookworm.platform.ddd.model.DomainEntity;
import wbh.bookworm.platform.ddd.model.DomainId;
import wbh.bookworm.platform.ddd.model.search.SearchTestAppConfig;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {SearchTestAppConfig.class})
@ExtendWith(SpringExtension.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class JsonDomainRepositoryTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonDomainRepositoryTest.class);

    @Test
    void testA_persistAndLoadAggregateWithGeneratedDomainId() {
        final AnAggregateRepository anAggregateRepository = new AnAggregateRepository();
        final AnAggregateDomainId anAggregateDomainId = anAggregateRepository.nextIdentity();

        final AnEntityDomainId anEntityDomainId = new AnEntityDomainId("Nancy");
        final AnEntity anEntity = new AnEntity(anEntityDomainId, "Huhu Swietbert " + anAggregateDomainId);

        final AnAggregate anAggregate = new AnAggregate(anAggregateDomainId, anEntity);
        anAggregateRepository.save(anAggregate);

        final Optional<AnAggregate> loadedAggregate = anAggregateRepository.load(anAggregateDomainId);
        assertTrue(loadedAggregate.isPresent());
        assertEquals(anAggregateDomainId, loadedAggregate.get().getDomainId());
        assertEquals(loadedAggregate.get().getAnEntity().getBla(), "Huhu Swietbert " + anAggregateDomainId);
    }

    @Test
    void testB_persistDomainAggregate() {
        final AnAggregateRepository anAggregateRepository = new AnAggregateRepository();

        final AnEntityDomainId domainId = new AnEntityDomainId("Nancy");
        final AnEntity anEntity = new AnEntity(domainId, "Huhu Swietbert");

        final AnAggregate anAggregate = new AnAggregate(new AnAggregateDomainId("Ralf"), anEntity);
        anAggregateRepository.save(anAggregate);
    }

    @Test
    void testC_loadDomainAggregate() {
        final AnAggregateRepository anAggregateRepository = new AnAggregateRepository();

        final AnAggregateDomainId domainId = new AnAggregateDomainId("Ralf");
        final Optional<AnAggregate> anAggregate = anAggregateRepository.load(domainId);
        assertTrue(anAggregate.isPresent());
        assertEquals(domainId, anAggregate.get().getDomainId());
        assertEquals(anAggregate.get().getAnEntity().getBla(), "Huhu Swietbert");
    }

    @Test
    void testD_countAllAggregates() {
        final AnAggregateRepository anAggregateRepository = new AnAggregateRepository();
        assertThat(anAggregateRepository.countAll(), is(greaterThanOrEqualTo(1L)));
    }

    @Test
    void testE_loadAllAggregates() {
        final AnAggregateRepository anAggregateRepository = new AnAggregateRepository();
        final Optional<Set<AnAggregate>> aggregates = anAggregateRepository.loadAll();
        assertTrue(aggregates.isPresent());
        aggregates.get().forEach(a -> LOGGER.info("{}", a));
        //assertEquals(2, aggregates.get().size());
    }

    @Test
    void testF_loadAllAggregates() {
        final AnAggregateRepository anAggregateRepository = new AnAggregateRepository();
        final Optional<Set<AnAggregate>> aggregates =
                anAggregateRepository.find(Predicate.Equals.of("domainId", "Ralf"));
        assertTrue(aggregates.isPresent());
        aggregates.get().forEach(a -> LOGGER.info("{}", a));
        //assertEquals(2, aggregates.get().size());
    }

    private static class AnAggregateRepository extends JsonDomainRepository<AnAggregate, AnAggregateDomainId> {

        AnAggregateRepository() {
            super(AnAggregate.class, AnAggregateDomainId.class, Path.of("target"));
        }

    }

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
