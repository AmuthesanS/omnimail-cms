package io.omnimail.cms.domain.model;

import java.time.Instant;

public record Asset(
        String contentHash,
        String tenantId,
        String objectKey,
        String mimeType,
        long sizeBytes,
        Instant createdAt) {
}
