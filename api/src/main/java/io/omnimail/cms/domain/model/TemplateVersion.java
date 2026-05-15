package io.omnimail.cms.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record TemplateVersion(
        String templateVersionId,
        String tenantId,
        String templateId,
        int version,
        WorkflowStatus status,
        String mjmlSource,
        String htmlObjectKey,
        Map<String, Object> variablesSchema,
        List<String> assetRefs,
        String gitRef,
        String sourcePath,
        Instant publishedAt,
        String publishedBy,
        Instant createdAt) {
}
