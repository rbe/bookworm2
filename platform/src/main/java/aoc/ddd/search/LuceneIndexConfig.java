/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.ddd.search;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.nio.file.Path;

@Configuration
@PropertySource(value = "classpath:/conf/lucene.properties")
public class LuceneIndexConfig {

    @Value("${lucene.index.directory}")
    private Path directory;

    public Path getDirectory() {
        return directory;
    }

}
