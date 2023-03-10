package wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;

import aoc.mikrokosmos.crypto.messagedigest.MessageDigester;

@Introspected
public final class ShardAudiobook implements Comparable<ShardAudiobook>, Serializable {

    private final String objectId;

    private final List<ShardObject> shardObjects;

    private final ShardName shardName;

    private final String hashValue;

    @JsonCreator
    public ShardAudiobook(@JsonProperty("objectId") final String objectId,
                          @JsonProperty("shardObjects") final List<ShardObject> shardObjects,
                          @JsonProperty("shardName") final ShardName shardName) {
        this.objectId = objectId;
        this.shardObjects = null != shardObjects && !shardObjects.isEmpty()
                ? Collections.unmodifiableList(shardObjects)
                : Collections.emptyList();
        hashValue = computeHashValue();
        this.shardName = shardName;
    }

    public static ShardAudiobook local(final String objectId,
                                       final List<ShardObject> shardObjects) {
        return new ShardAudiobook(objectId, shardObjects,
                new ShardName());
    }

    public static ShardAudiobook of(final ShardAudiobook shardAudiobook) {
        return new ShardAudiobook(shardAudiobook.objectId,
                shardAudiobook.shardObjects,
                shardAudiobook.shardName);
    }

    public static ShardAudiobook of(final ShardAudiobook shardAudiobook, final ShardName shardName) {
        return new ShardAudiobook(shardAudiobook.objectId,
                shardAudiobook.shardObjects,
                shardName);
    }

    public String getObjectId() {
        return objectId;
    }

    public List<ShardObject> getShardObjects() {
        return shardObjects;
    }

    public ShardName getShardName() {
        return shardName;
    }

    public String getHashValue() {
        return hashValue;
    }

    public long size() {
        return shardObjects.stream()
                .map(ShardObject::getSize)
                .reduce(0L, Long::sum);
    }

    public boolean isTitelnummer(final String titelnummer) {
        return titelnummer.equals(objectId);
    }

    private String computeHashValue() {
        final List<String> allObjectsHashValues = shardObjects.stream()
                .map(ShardObject::getHashValue)
                .collect(Collectors.toUnmodifiableList());
        return MessageDigester.sha256OfUTF8(allObjectsHashValues);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ShardAudiobook that = (ShardAudiobook) o;
        return shardName.equals(that.shardName) &&
                shardObjects.equals(that.shardObjects);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shardName, shardObjects);
    }

    @Override
    public int compareTo(final ShardAudiobook o) {
        return Comparator.comparing(ShardAudiobook::getObjectId)
                .compare(this, o);
    }

    @Override
    public String toString() {
        return String.format("ShardAudiobook{objectId='%s', shardName='%s', shardObjects=%d, hashValue='%s'}",
                objectId, shardName, shardObjects.size(), hashValue);
    }

}
