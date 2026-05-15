package io.omnimail.cms.application.service;

import io.omnimail.cms.application.TenantContext;
import io.omnimail.cms.application.port.AssetRepository;
import io.omnimail.cms.application.port.BlobStore;
import io.omnimail.cms.domain.model.Asset;
import io.omnimail.cms.infrastructure.config.OmnimailProperties;
import java.io.InputStream;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import org.springframework.stereotype.Service;

@Service
public class AssetService {

    private final BlobStore blobStore;
    private final AssetRepository assetRepository;
    private final OmnimailProperties properties;

    public AssetService(BlobStore blobStore, AssetRepository assetRepository, OmnimailProperties properties) {
        this.blobStore = blobStore;
        this.assetRepository = assetRepository;
        this.properties = properties;
    }

    public Asset upload(InputStream content, String mimeType, long sizeBytes) throws Exception {
        if (!TenantContext.hasRole("editor")) {
            throw new io.omnimail.cms.domain.exception.WorkflowException("Editor role required");
        }
        byte[] bytes = content.readAllBytes();
        String hash = sha256(bytes);
        String tenantId = TenantContext.requireTenantId();
        return assetRepository
                .findByHash(tenantId, hash)
                .orElseGet(() -> {
                    String objectKey = tenantId + "/assets/sha256/" + hash;
                    blobStore.put(objectKey, new java.io.ByteArrayInputStream(bytes), mimeType, sizeBytes);
                    Asset asset = new Asset(hash, tenantId, objectKey, mimeType, sizeBytes, Instant.now());
                    return assetRepository.save(asset);
                });
    }

    public Asset get(String hash) {
        return assetRepository
                .findByHash(TenantContext.requireTenantId(), hash)
                .orElseThrow(() -> new io.omnimail.cms.domain.exception.NotFoundException("Asset not found: " + hash));
    }

    public String cdnUrl(Asset asset) {
        String base = properties.getStorage().getCdnBaseUrl();
        if (base.endsWith("/")) {
            return base + asset.objectKey();
        }
        return base + "/" + asset.objectKey();
    }

    private static String sha256(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(bytes));
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
