package io.omnimail.cms.domain.model;

import java.time.Instant;
import java.util.List;

public record Template(
        String id,
        String tenantId,
        String name,
        List<String> tags,
        Instant createdAt,
        Instant updatedAt) {
}
