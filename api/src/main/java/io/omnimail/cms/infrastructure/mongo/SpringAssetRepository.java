package io.omnimail.cms.infrastructure.mongo;

import io.omnimail.cms.application.port.AssetRepository;
import io.omnimail.cms.domain.model.Asset;
import io.omnimail.cms.infrastructure.mongo.repository.AssetMongoRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class SpringAssetRepository implements AssetRepository {

    private final AssetMongoRepository mongo;

    SpringAssetRepository(AssetMongoRepository mongo) {
        this.mongo = mongo;
    }

    @Override
    public Asset save(Asset asset) {
        return MongoMapper.toDomain(mongo.save(MongoMapper.toDocument(asset)));
    }

    @Override
    public Optional<Asset> findByHash(String tenantId, String contentHash) {
        return mongo.findByTenantIdAndContentHash(tenantId, contentHash).map(MongoMapper::toDomain);
    }
}
