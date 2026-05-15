package io.omnimail.cms.infrastructure.mongo.repository;

import io.omnimail.cms.infrastructure.mongo.document.AssetDocument;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AssetMongoRepository extends MongoRepository<AssetDocument, String> {
    Optional<AssetDocument> findByTenantIdAndContentHash(String tenantId, String contentHash);
}
