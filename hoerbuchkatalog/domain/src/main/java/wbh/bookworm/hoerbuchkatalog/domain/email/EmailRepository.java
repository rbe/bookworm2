/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.email;

import wbh.bookworm.platform.ddd.repository.JsonDomainRepository;

import java.nio.file.Path;
import java.nio.file.Paths;

public class EmailRepository extends JsonDomainRepository<Email, EmailId> {

    public EmailRepository() {
        super(Email.class, EmailId.class, Path.of("."));
    }

    public EmailRepository(final Path storagePath) {
        super(Email.class, EmailId.class, storagePath);
    }

    public Email erstellen(final String from, final String to, final String subject, final String content) {
        return new Email(nextIdentity(), from, to, subject, content);
    }

}
