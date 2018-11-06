/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.email;

import aoc.ddd.repository.DomainRespositoryComponent;
import aoc.ddd.repository.JsonDomainRepository;

import java.nio.file.Path;

@DomainRespositoryComponent
public class EmailTemplateRepository extends JsonDomainRepository<HtmlEmailTemplate, EmailTemplateId> {

    public EmailTemplateRepository(final Path storagePath) {
        super(HtmlEmailTemplate.class, EmailTemplateId.class, storagePath);
    }

    public HtmlEmailTemplate erstellen(final String html) {
        return save(new HtmlEmailTemplate(nextIdentity(), html));
    }

}
