package io.omnimail.cms.infrastructure.mongo.repository;

import io.omnimail.cms.infrastructure.mongo.document.TemplateVersionDocument;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TemplateVersionMongoRepository extends MongoRepository<TemplateVersionDocument, String> {
    List<TemplateVersionDocument> findByTenantIdAndTemplateIdOrderByVersionAsc(String tenantId, String templateId);

    Optional<TemplateVersionDocument> findByTenantIdAndTemplateIdAndVersion(
            String tenantId, String templateId, int version);
}
