package wbh.bookworm.hoerbuchdienst.adapter.required.hoerbuchkatalog;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("hoerbuchkatalog")
public class HoerbuchkatalogClientConfiguration {

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

}
