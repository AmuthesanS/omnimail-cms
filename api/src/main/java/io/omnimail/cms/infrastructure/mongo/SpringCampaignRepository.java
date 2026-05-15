package io.omnimail.cms.infrastructure.mongo;

import io.omnimail.cms.application.port.CampaignRepository;
import io.omnimail.cms.domain.model.Campaign;
import io.omnimail.cms.infrastructure.mongo.repository.CampaignMongoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class SpringCampaignRepository implements CampaignRepository {

    private final CampaignMongoRepository mongo;

    SpringCampaignRepository(CampaignMongoRepository mongo) {
        this.mongo = mongo;
    }

    @Override
    public Campaign save(Campaign campaign) {
        return MongoMapper.toDomain(mongo.save(MongoMapper.toDocument(campaign)));
    }

    @Override
    public Optional<Campaign> findById(String tenantId, String id) {
        return mongo.findById(id).filter(d -> tenantId.equals(d.getTenantId())).map(MongoMapper::toDomain);
    }

    @Override
    public List<Campaign> findAllByTenant(String tenantId) {
        return mongo.findByTenantId(tenantId).stream().map(MongoMapper::toDomain).toList();
    }
}
