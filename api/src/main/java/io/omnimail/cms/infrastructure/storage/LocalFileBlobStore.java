package io.omnimail.cms.infrastructure.storage;

import io.omnimail.cms.application.port.BlobStore;
import io.omnimail.cms.infrastructure.config.OmnimailProperties;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "omnimail.storage.type", havingValue = "local", matchIfMissing = true)
public class LocalFileBlobStore implements BlobStore {

    private final Path root;

    public LocalFileBlobStore(OmnimailProperties properties) throws IOException {
        this.root = Path.of(properties.getStorage().getLocalPath());
        Files.createDirectories(root);
    }

    @Override
    public void put(String objectKey, InputStream content, String contentType, long sizeBytes) {
        try {
            Path target = root.resolve(objectKey);
            Files.createDirectories(target.getParent());
            Files.write(target, content.readAllBytes());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write local blob: " + objectKey, e);
        }
    }

    @Override
    public Optional<byte[]> get(String objectKey) {
        Path file = root.resolve(objectKey);
        if (!Files.isRegularFile(file)) {
            return Optional.empty();
        }
        try {
            return Optional.of(Files.readAllBytes(file));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read local blob: " + objectKey, e);
        }
    }

    @Override
    public boolean exists(String objectKey) {
        return Files.isRegularFile(root.resolve(objectKey));
    }

    @Override
    public void delete(String objectKey) {
        try {
            Files.deleteIfExists(root.resolve(objectKey));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to delete local blob: " + objectKey, e);
        }
    }
}
