/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.email;

import aoc.mikrokosmos.ddd.model.DomainAggregate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public final class HtmlEmailTemplate extends DomainAggregate<HtmlEmailTemplate, EmailTemplateId> {

    private static final long serialVersionUID = -1L;

    private final String html;

    @JsonCreator
    public HtmlEmailTemplate(final @JsonProperty("domainId") EmailTemplateId emailTemplateId,
                             final @JsonProperty("html") String html) {
        super(emailTemplateId);
        this.html = html;
    }

    public String getHtml() {
        return html;
    }

    @Override
    public int hashCode() {
        return Objects.hash(domainId, html);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final HtmlEmailTemplate that = (HtmlEmailTemplate) other;
        return Objects.equals(html, that.html);
    }

    @Override
    public int compareTo(final HtmlEmailTemplate other) {
        return other.html.compareTo(this.html);
    }

    @Override
    public String toString() {
        return String.format("HtmlEmailTemplate{domainId=%s, text='%s'}",
                domainId, html);
    }

}
