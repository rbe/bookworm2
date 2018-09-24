/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.email;

import wbh.bookworm.hoerbuchkatalog.domain.email.EmailTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public final class TemplateBuilder {

    private final TemplateEngine templateEngine;

    @Autowired
    public TemplateBuilder(final TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String buildSimple(final String message) {
        final Context context = new Context();
        context.setVariable("message", message);
        return templateEngine.process("mailTemplate", context);
    }

    public String build(final EmailTemplate emailTemplate, final String message) {
        final Context context = new Context();
        context.setVariable("message", message);
        return templateEngine.process("mailTemplate", context);
    }

}
