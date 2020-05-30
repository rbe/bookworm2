package wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class ShardObject implements Serializable {

    private static final long serialVersionUID = -1698948107854015839L;

    private final String titelnummer;

    private final long size;

    private final String hashValue;

    private ShardNumber shardNumber;

    @JsonCreator
    public ShardObject(@JsonProperty("shardNumber") final ShardNumber shardNumber,
                       @JsonProperty("titelnummer") final String titelnummer,
                       @JsonProperty("size") final long size,
                       @JsonProperty("hashValue") final String hashValue) {
        this.shardNumber = shardNumber;
        this.titelnummer = titelnummer;
        this.size = size;
        this.hashValue = hashValue;
    }

    public ShardObject(final ShardObject shardObject, final ShardNumber shardNumber) {
        this.shardNumber = shardNumber;
        titelnummer = shardObject.getTitelnummer();
        size = shardObject.getSize();
        hashValue = shardObject.getHashValue();
    }

    public ShardNumber getShardNumber() {
        return shardNumber;
    }

    public void reshard(final ShardNumber shardNumber) {
        this.shardNumber = shardNumber;
    }

    public String getTitelnummer() {
        return titelnummer;
    }

    public long getSize() {
        return size;
    }

    public String getHashValue() {
        return hashValue;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ShardObject that = (ShardObject) o;
        return shardNumber == that.shardNumber &&
                size == that.size &&
                titelnummer.equals(that.titelnummer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shardNumber, titelnummer, size);
    }

    @Override
    public String toString() {
        return String.format("ShardEntry{shardNumer='%s', titelnummer='%s'}", shardNumber, titelnummer);
    }

}
