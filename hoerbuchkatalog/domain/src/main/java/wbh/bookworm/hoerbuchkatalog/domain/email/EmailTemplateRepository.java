/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.email;

import wbh.bookworm.platform.ddd.repository.JsonDomainRepository;

import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class EmailTemplateRepository extends JsonDomainRepository<HtmlEmailTemplate, EmailTemplateId> {

    public EmailTemplateRepository() {
        super(HtmlEmailTemplate.class, EmailTemplateId.class, Path.of("."));
    }

    public HtmlEmailTemplate erstellen(final String html) {
        return save(new HtmlEmailTemplate(nextIdentity(), html));
    }

}
