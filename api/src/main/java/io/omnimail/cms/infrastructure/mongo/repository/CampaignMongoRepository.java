package io.omnimail.cms.infrastructure.mongo.repository;

import io.omnimail.cms.infrastructure.mongo.document.CampaignDocument;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CampaignMongoRepository extends MongoRepository<CampaignDocument, String> {
    List<CampaignDocument> findByTenantId(String tenantId);
}
