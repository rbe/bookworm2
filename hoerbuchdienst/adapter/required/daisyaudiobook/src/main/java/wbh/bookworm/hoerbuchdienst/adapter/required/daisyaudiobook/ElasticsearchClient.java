/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.daisyaudiobook;

import java.util.List;

import org.elasticsearch.search.SearchHit;

public interface ElasticsearchClient {

    boolean indexExists();

    boolean createIndex();

    boolean index(String json);

    boolean bulkIndex(List<String> jsonStrings);

    SearchHit[] findAll(String[] keyword);

}
