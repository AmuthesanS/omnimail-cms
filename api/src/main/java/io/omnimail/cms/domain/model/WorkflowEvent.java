package io.omnimail.cms.domain.model;

import java.time.Instant;

public record WorkflowEvent(
        String id,
        String tenantId,
        String entityType,
        String entityId,
        WorkflowStatus fromStatus,
        WorkflowStatus toStatus,
        String actor,
        String comment,
        Instant occurredAt) {
}
