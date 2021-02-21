package wbh.bookworm.hoerbuchkatalog.webservice.api;

import java.util.Map;

public final class Antwort<T> {

    private final Map<String, Object> meta;

    private final T data;

    public Antwort(final Map<String, Object> meta, final T data) {
        this.meta = meta;
        this.data = data;
    }

    public Map<String, Object> getMeta() {
        return meta;
    }

    public T getData() {
        return data;
    }

}
