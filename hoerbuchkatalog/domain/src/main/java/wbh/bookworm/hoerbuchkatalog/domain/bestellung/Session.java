package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.MandantenId;

import aoc.mikrokosmos.ddd.model.DomainAggregate;

public final class Session extends DomainAggregate<Session, BestellungId> {

    private static final long serialVersionUID = -1L;

    @JsonProperty
    private final MandantenId mandantenId;

    @JsonProperty
    private final Hoerernummer hoerernummer;

    public Session(final BestellungId domainId, final MandantenId mandantenId, final Hoerernummer hoerernummer) {
        super(domainId);
        this.mandantenId = mandantenId;
        this.hoerernummer = hoerernummer;
    }

    public BestellungId getBestellungId() {
        return domainId;
    }

    public MandantenId getMandantenId() {
        return mandantenId;
    }

    public Hoerernummer getHoerernummer() {
        return hoerernummer;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final Session session = (Session) o;
        return domainId.equals(session.domainId)
                && mandantenId.equals(session.mandantenId)
                && hoerernummer.equals(session.hoerernummer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), domainId, mandantenId, hoerernummer);
    }

    @Override
    public String toString() {
        return String.format("Session{domainId=%s, mandantenId=%s, hoerernummer=%s}",
                domainId, mandantenId, hoerernummer);
    }

}
