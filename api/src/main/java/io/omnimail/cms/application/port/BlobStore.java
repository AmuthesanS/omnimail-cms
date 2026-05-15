package io.omnimail.cms.application.port;

import java.io.InputStream;
import java.util.Optional;

public interface BlobStore {

    void put(String objectKey, InputStream content, String contentType, long sizeBytes);

    Optional<byte[]> get(String objectKey);

    boolean exists(String objectKey);

    void delete(String objectKey);
}
