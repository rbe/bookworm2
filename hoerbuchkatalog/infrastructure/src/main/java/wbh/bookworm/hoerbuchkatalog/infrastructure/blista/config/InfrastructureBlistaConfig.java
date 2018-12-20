/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.config;

import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.bestellung.DlsBestellungAppConfig;
import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.lieferung.DlsLieferungAppConfig;
import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.restdlskatalog.RestServiceClient;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        DlsBestellungAppConfig.class,
        DlsLieferungAppConfig.class
})
@ComponentScan(basePackageClasses = {
        RestServiceClient.class
})
public class InfrastructureBlistaConfig {
}
