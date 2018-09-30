/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.email;

import wbh.bookworm.platform.ddd.model.DomainAggregate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public final class EmailTemplate extends DomainAggregate<EmailTemplate, EmailTemplateId> {

    private static final long serialVersionUID = -1L;

    private final EmailTemplateId emailTemplateId;

    private final String text;

    @JsonCreator
    public EmailTemplate(final @JsonProperty("domainId") EmailTemplateId emailTemplateId,
                         final @JsonProperty String text) {
        this.emailTemplateId = emailTemplateId;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public int hashCode() {
        return Objects.hash(emailTemplateId, text);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final EmailTemplate that = (EmailTemplate) other;
        return Objects.equals(text, that.text);
    }

    @Override
    public int compareTo(final EmailTemplate other) {
        return other.text.compareTo(this.text);
    }

    @Override
    public String toString() {
        return String.format("EmailTemplate{emailTemplateId=%s, text='%s'}",
                emailTemplateId, text);
    }

}
