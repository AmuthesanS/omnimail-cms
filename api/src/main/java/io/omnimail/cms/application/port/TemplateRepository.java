package io.omnimail.cms.application.port;

import io.omnimail.cms.domain.model.Template;
import java.util.List;
import java.util.Optional;

public interface TemplateRepository {

    Template save(Template template);

    Optional<Template> findById(String tenantId, String id);

    List<Template> findAllByTenant(String tenantId);

    void delete(String tenantId, String id);
}
