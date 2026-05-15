package io.omnimail.cms.application.port;

import io.omnimail.cms.domain.model.Campaign;
import java.util.List;
import java.util.Optional;

public interface CampaignRepository {

    Campaign save(Campaign campaign);

    Optional<Campaign> findById(String tenantId, String id);

    List<Campaign> findAllByTenant(String tenantId);
}
