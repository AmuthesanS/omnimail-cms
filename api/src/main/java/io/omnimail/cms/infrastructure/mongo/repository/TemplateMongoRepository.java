package io.omnimail.cms.infrastructure.mongo.repository;

import io.omnimail.cms.infrastructure.mongo.document.TemplateDocument;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TemplateMongoRepository extends MongoRepository<TemplateDocument, String> {
    List<TemplateDocument> findByTenantId(String tenantId);
}
