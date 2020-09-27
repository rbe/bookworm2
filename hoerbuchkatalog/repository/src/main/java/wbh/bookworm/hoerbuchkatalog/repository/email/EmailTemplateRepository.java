/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.email;

import java.nio.file.Path;

import wbh.bookworm.hoerbuchkatalog.domain.email.EmailTemplateId;
import wbh.bookworm.hoerbuchkatalog.domain.email.HtmlEmailTemplate;

import aoc.mikrokosmos.ddd.repository.JsonDomainRepository;

// TODO spring.main.allow-bean-definition-overriding=true @DomainRepositoryComponent
public class EmailTemplateRepository extends JsonDomainRepository<HtmlEmailTemplate, EmailTemplateId> {

    public EmailTemplateRepository(final Path storagePath) {
        super(HtmlEmailTemplate.class, EmailTemplateId.class, storagePath);
    }

    public HtmlEmailTemplate erstellen(final String html) {
        return save(new HtmlEmailTemplate(nextIdentity(), html));
    }

}
