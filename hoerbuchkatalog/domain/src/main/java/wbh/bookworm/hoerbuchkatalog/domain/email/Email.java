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

public final class Email extends DomainAggregate<Email, EmailId> {

    private static final long serialVersionUID = -1L;

    private String from;

    private String to;

    private String subject;

    private String content;

    public Email(final EmailId emailId) {
        super(emailId);
    }

    @JsonCreator
    public Email(final @JsonProperty("domainId") EmailId emailId,
                 final @JsonProperty String from,
                 final @JsonProperty String to,
                 final @JsonProperty String subject,
                 final @JsonProperty String content) {
        super(emailId);
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.content = content;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final Email email = (Email) other;
        return Objects.equals(from, email.from) &&
                Objects.equals(to, email.to) &&
                Objects.equals(subject, email.subject) &&
                Objects.equals(content, email.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, subject, content);
    }

    @Override
    public int compareTo(final Email o) {
        return o.domainId.compareTo(this.domainId.getValue());
    }

    @Override
    public String toString() {
        return String.format("Email{domainId='%s', from='%s', to='%s', subject='%s', content='%s'}",
                domainId, from, to, subject, content);
    }

}
