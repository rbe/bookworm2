package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import java.util.Map;

public class AntwortDTO<T> {

    private final Map<String, Object> meta;

    private final T data;

    protected AntwortDTO(final Map<String, Object> meta, final T data) {
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
