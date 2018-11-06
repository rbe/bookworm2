/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.ddd.repository;

import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {RepositoryTestAppConfig.class})
@ExtendWith(SpringExtension.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class JsonDomainRepositoryTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonDomainRepositoryTest.class);

    private final AnAggregateRepository anAggregateRepository;

    @Autowired
    JsonDomainRepositoryTest(final AnAggregateRepository anAggregateRepository) {
        this.anAggregateRepository = anAggregateRepository;
    }

    @Test
    void testA_persistAndLoadAggregateWithGeneratedDomainId() {
        final AnAggregateId anAggregateId = anAggregateRepository.nextIdentity();

        final AnEntityId anEntityId = new AnEntityId("Nancy");
        final AnEntity anEntity = new AnEntity(anEntityId, "Huhu Swietbert " + anAggregateId);

        final AnAggregate anAggregate = new AnAggregate(anAggregateId, anEntity);
        anAggregateRepository.save(anAggregate);

        final Optional<AnAggregate> loadedAggregate = anAggregateRepository.load(anAggregateId);
        assertTrue(loadedAggregate.isPresent());
        Assertions.assertEquals(anAggregateId, loadedAggregate.get().getDomainId());
        assertEquals(loadedAggregate.get().getAnEntity().getBla(), "Huhu Swietbert " + anAggregateId);
    }

    @Test
    void testB_persistDomainAggregate() {
        final AnEntityId domainId = new AnEntityId("Nancy");
        final AnEntity anEntity = new AnEntity(domainId, "Huhu Swietbert");

        final AnAggregate anAggregate = new AnAggregate(new AnAggregateId("Ralf"), anEntity);
        anAggregateRepository.save(anAggregate);
    }

    @Test
    void testC_loadDomainAggregate() {
        final AnAggregateId domainId = new AnAggregateId("Ralf");
        final Optional<AnAggregate> anAggregate = anAggregateRepository.load(domainId);
        assertTrue(anAggregate.isPresent());
        Assertions.assertEquals(domainId, anAggregate.get().getDomainId());
        assertEquals(anAggregate.get().getAnEntity().getBla(), "Huhu Swietbert");
    }

    @Test
    void testD_countAllAggregates() {
        assertThat(anAggregateRepository.countAll(), is(greaterThanOrEqualTo(1L)));
    }

    @Test
    void testE_loadAllAggregates() {
        final Optional<Set<AnAggregate>> aggregates = anAggregateRepository.loadAll();
        assertTrue(aggregates.isPresent());
        aggregates.get().forEach(a -> LOGGER.info("{}", a));
        //assertEquals(2, aggregates.get().size());
    }

    @Test
    void testF_loadAllAggregates() {
        final Optional<Set<AnAggregate>> aggregates =
                anAggregateRepository.find(QueryPredicate.Equals.of("domainId", "Ralf"));
        assertTrue(aggregates.isPresent());
        aggregates.get().forEach(a -> LOGGER.info("{}", a));
        //assertEquals(2, aggregates.get().size());
    }

}
