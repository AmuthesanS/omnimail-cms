package io.omnimail.cms.domain.model;

import java.time.Instant;

public record SendLog(
        String id,
        String tenantId,
        String campaignId,
        String templateVersionId,
        String mode,
        int recipientCount,
        String status,
        Instant createdAt) {
}
