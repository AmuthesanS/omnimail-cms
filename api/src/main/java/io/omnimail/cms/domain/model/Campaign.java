package io.omnimail.cms.domain.model;

import java.time.Instant;
import java.util.Map;

public record Campaign(
        String id,
        String tenantId,
        String name,
        String templateVersionId,
        String subject,
        String fromEmail,
        Map<String, Object> variables,
        WorkflowStatus status,
        Instant createdAt,
        Instant updatedAt) {
}
