package wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

import aoc.mikrokosmos.ddd.spring.Singleton;

@Singleton
public final class HostName implements Serializable {

    private static final long serialVersionUID = 4845478634298179177L;

    private final String hostname;

    public HostName() {
        try {
            hostname = String.format("%s", InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public HostName(String hostname) {
        this.hostname = hostname;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final HostName hostName = (HostName) o;
        return hostname.equals(hostName.hostname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostname);
    }

    @Override
    public String toString() {
        return hostname;
    }

}
