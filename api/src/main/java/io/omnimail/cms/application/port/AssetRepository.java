package io.omnimail.cms.application.port;

import io.omnimail.cms.domain.model.Asset;
import java.util.Optional;

public interface AssetRepository {

    Asset save(Asset asset);

    Optional<Asset> findByHash(String tenantId, String contentHash);
}
