package wbh.bookworm.hoerbuchdienst.domain.required.mandantrepository;

import java.io.Serializable;

// TODO ValueObject -> shared.domain
public final class StorageCredentials implements Serializable {

    private static final long serialVersionUID = 5088986321877616394L;

    private final String storageUrl;

    private final String accessKey;

    private final String secretKey;

    public StorageCredentials(final String storageUrl, final String accessKey, final String secretKey) {
        this.storageUrl = storageUrl;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String getStorageUrl() {
        return storageUrl;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

}
