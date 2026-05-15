package io.omnimail.cms.application.port;

import io.omnimail.cms.domain.model.TemplateVersion;
import java.util.List;
import java.util.Optional;

public interface TemplateVersionRepository {

    TemplateVersion save(TemplateVersion version);

    Optional<TemplateVersion> findByVersionId(String tenantId, String templateVersionId);

    Optional<TemplateVersion> findByTemplateIdAndVersion(String tenantId, String templateId, int version);

    List<TemplateVersion> findByTemplateId(String tenantId, String templateId);

    int nextVersionNumber(String tenantId, String templateId);
}
