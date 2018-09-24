/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.email;

import wbh.bookworm.platform.ddd.model.DomainEntity;

import java.util.Objects;

public final class Email extends DomainEntity<Email> {

    private static final long serialVersionUID = -1L;

    private String from;

    private String to;

    private String subject;

    private String content;

    public Email() {
    }

    public Email(String from, String to, String subject, String content) {
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
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Email email = (Email) o;
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
        /* TODO Comparable */return o.from.compareTo(this.from);
    }

    @Override
    public String toString() {
        return "Email{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

}
