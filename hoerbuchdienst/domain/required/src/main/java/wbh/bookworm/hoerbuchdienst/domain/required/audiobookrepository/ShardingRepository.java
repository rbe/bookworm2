/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import aoc.mikrokosmos.ddd.model.DomainId;

public interface ShardingRepository {

    Optional<ShardName> lookupShard(String objectId);

    void redistribute(int heartbeatHighWatermark, List<? extends DomainId<String>> localDomainIds);

    void receiveObject(String objectId, InputStream inputStream);

}
