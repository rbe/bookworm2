/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.email;

import wbh.bookworm.platform.ddd.repository.JsonDomainRepository;

import java.nio.file.Paths;

public class EmailTemplateRepository extends JsonDomainRepository<EmailTemplate, EmailTemplateId> {

    public EmailTemplateRepository() {
        super(EmailTemplate.class, EmailTemplateId.class, Paths.get("."));
    }

    public EmailTemplate erstellen(final String text) {
        return new EmailTemplate(nextId(), text);
    }

}
