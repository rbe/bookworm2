/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.platform.ddd.repository.search;

import wbh.bookworm.platform.ddd.repository.DomainFactoryComponent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.io.IOException;
import java.nio.file.Path;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@DomainFactoryComponent
public class LuceneIndexFactory {

    private final LuceneIndexConfig luceneIndexConfig;

    @Autowired
    public LuceneIndexFactory(final LuceneIndexConfig luceneIndexConfig) {
        this.luceneIndexConfig = luceneIndexConfig;
    }

    @Bean
    @Scope(SCOPE_PROTOTYPE)
    public LuceneIndex luceneIndex(final String name) throws IOException {
        final Path path = luceneIndexConfig.getIndex().getDirectory().resolve(name);
        return new LuceneIndex(path);
    }

}
