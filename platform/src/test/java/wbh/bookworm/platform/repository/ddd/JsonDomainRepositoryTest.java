/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.platform.repository.ddd;

import wbh.bookworm.platform.ddd.model.DomainAggregate;
import wbh.bookworm.platform.ddd.model.DomainEntity;
import wbh.bookworm.platform.ddd.model.DomainId;
import wbh.bookworm.platform.ddd.repository.JsonDomainRepository;
import wbh.bookworm.platform.ddd.repository.search.TestAppConfig;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {TestAppConfig.class})
@ExtendWith(SpringExtension.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class JsonDomainRepositoryTest {

    @Test
    void should1PersistDomainAggregate() {
        final AnEntity anEntity = new AnEntity(new ADomainId("Nancy"), "Huhu Swietbert");
        final AnAggregate anAggregate = new AnAggregate(new ADomainId("Ralf"), anEntity);
        new ADomainRepository().save(anAggregate);
    }

    @Test
    void should2LoadDomainAggregate() {
        final DomainId<String> domainId = new DomainId<>("Ralf");
        final Optional<AnAggregate> anAggregate = new ADomainRepository().load(domainId);
        assertTrue(anAggregate.isPresent());
        assertEquals(domainId, anAggregate.get().getDomainId());
        assertEquals(anAggregate.get().getAnEntity().getBla(), "Huhu Swietbert");
    }

    private static class ADomainRepository extends JsonDomainRepository<AnAggregate> {

        ADomainRepository() {
            super(AnAggregate.class, Paths.get("target"));
        }

    }

    private static class ADomainId extends DomainId<ADomainId> {

        ADomainId(final @JsonProperty("value") String value) {
            super(value);
        }

    }

    private static class AnAggregate extends DomainAggregate<AnAggregate> {

        private static final long serialVersionUID = -1L;

        @JsonProperty
        AnEntity anEntity;

        @JsonCreator
        AnAggregate(final @JsonProperty("domainId") ADomainId domainId,
                    final @JsonProperty("anEntity") AnEntity anEntity) {
            super(new DomainId<>(domainId.getValue()));
            this.anEntity = anEntity;
        }

        AnEntity getAnEntity() {
            return anEntity;
        }

        @Override
        public int compareTo(final AnAggregate o) {
            /* TODO Comparable */return 0;
        }

    }

    private static class AnEntity extends DomainEntity<AnEntity> {

        private static final long serialVersionUID = -1L;

        @JsonProperty
        private String bla;

        @JsonCreator
        AnEntity(final @JsonProperty("domainId") ADomainId domainId,
                 final @JsonProperty("bla") String bla) {
            super(new DomainId<>(domainId.getValue()));
            this.bla = bla;
        }

        String getBla() {
            return bla;
        }

        @Override
        public int compareTo(final AnEntity o) {
            /* TODO Comparable */return 0;
        }

    }

}
