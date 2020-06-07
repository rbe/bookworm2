package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import java.net.InetAddress;
import java.net.UnknownHostException;

import io.micronaut.context.annotation.Property;

import aoc.mikrokosmos.ddd.spring.Singleton;

@Singleton
final class MyHostname {

    @Property(name = RepositoryConfigurationKeys.HOERBUCHDIENST_SHARD_NUMBER)
    private Integer MY_SHARD_NUMBER;

    String myHostname() {
        final String hostName;
        try {
            return String.format("%s#%d", InetAddress.getLocalHost().getHostName(), MY_SHARD_NUMBER);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    boolean equals(String hostname) {
        return myHostname().equals(hostname);
    }

}
