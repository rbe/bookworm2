/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.nutzerdaten;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.nio.file.Path;

@Configuration
@PropertySource("classpath:/conf/nutzerdaten.properties")
@ConfigurationProperties(prefix = "nutzerdaten")
public class HoererRepositoryConfig {

    private Path directory;

    public void setDirectory(final Path directory) {
        this.directory = directory;
    }

    Path getDirectory() {
        return directory;
    }

}
