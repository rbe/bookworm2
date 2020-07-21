package wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

// TODO @Introspected does not work with Json fromStringCreator
public final class ShardName implements Serializable {

    private static final long serialVersionUID = -1351069385413170972L;

    private final String hostName;

    public ShardName() {
        this.hostName = new HostName().toString();
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public ShardName(final String hostName) {
        this.hostName = hostName;
    }

    @JsonIgnore
    public String getShardName() {
        return hostName;
    }

    @JsonValue
    public String getHostName() {
        return hostName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ShardName shardName = (ShardName) o;
        return hostName.equals(shardName.hostName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostName);
    }

    @Override
    public String toString() {
        return getShardName();
    }

}
