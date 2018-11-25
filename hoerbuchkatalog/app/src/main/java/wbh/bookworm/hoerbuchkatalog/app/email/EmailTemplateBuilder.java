/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.email;

import wbh.bookworm.hoerbuchkatalog.domain.email.EmailTemplateId;
import wbh.bookworm.hoerbuchkatalog.repository.email.EmailTemplateRepository;
import wbh.bookworm.hoerbuchkatalog.domain.email.HtmlEmailTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;

@Component
public final class EmailTemplateBuilder {

    private final EmailTemplateRepository emailTemplateRepository;

    private final TemplateEngine templateEngine;

    @Autowired
    public EmailTemplateBuilder(final EmailTemplateRepository emailTemplateRepository,
                                final TemplateEngine templateEngine) {
        this.emailTemplateRepository = emailTemplateRepository;
        this.templateEngine = templateEngine;
    }

    public String buildSimple(final String message) {
        final Context context = new Context();
        context.setVariable("message", message);
        return templateEngine.process("mailTemplate", context);
    }

    public String build(final EmailTemplateId emailTemplateId, final String message) {
        final HtmlEmailTemplate htmlEmailTemplate =
                emailTemplateRepository.load(emailTemplateId)
                .orElseThrow(EmailServiceException::new);
        final Context context = new Context(Locale.GERMANY);
        context.setVariable("message", message);
        return templateEngine.process(htmlEmailTemplate.getHtml(), context);
    }

}
