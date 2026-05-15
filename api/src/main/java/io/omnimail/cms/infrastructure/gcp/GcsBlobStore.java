package io.omnimail.cms.infrastructure.gcp;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import io.omnimail.cms.application.port.BlobStore;
import io.omnimail.cms.infrastructure.config.OmnimailProperties;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "omnimail.storage.type", havingValue = "gcs")
public class GcsBlobStore implements BlobStore {

    private final Storage storage;
    private final OmnimailProperties properties;

    public GcsBlobStore(Storage storage, OmnimailProperties properties) {
        this.storage = storage;
        this.properties = properties;
    }

    @Override
    public void put(String objectKey, InputStream content, String contentType, long sizeBytes) {
        BlobId blobId = BlobId.of(properties.getGcs().getBucket(), objectKey);
        BlobInfo info = BlobInfo.newBuilder(blobId).setContentType(contentType).build();
        try {
            byte[] bytes = content.readAllBytes();
            storage.create(info, bytes);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to upload to GCS: " + objectKey, e);
        }
    }

    @Override
    public Optional<byte[]> get(String objectKey) {
        var blob = storage.get(BlobId.of(properties.getGcs().getBucket(), objectKey));
        if (blob == null || !blob.exists()) {
            return Optional.empty();
        }
        return Optional.of(blob.getContent());
    }

    @Override
    public boolean exists(String objectKey) {
        var blob = storage.get(BlobId.of(properties.getGcs().getBucket(), objectKey));
        return blob != null && blob.exists();
    }

    @Override
    public void delete(String objectKey) {
        storage.delete(BlobId.of(properties.getGcs().getBucket(), objectKey));
    }
}
