package io.omnimail.cms.infrastructure.mongo;

import io.omnimail.cms.application.port.TemplateVersionRepository;
import io.omnimail.cms.domain.model.TemplateVersion;
import io.omnimail.cms.infrastructure.mongo.repository.TemplateVersionMongoRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class SpringTemplateVersionRepository implements TemplateVersionRepository {

    private final TemplateVersionMongoRepository mongo;

    SpringTemplateVersionRepository(TemplateVersionMongoRepository mongo) {
        this.mongo = mongo;
    }

    @Override
    public TemplateVersion save(TemplateVersion version) {
        return MongoMapper.toDomain(mongo.save(MongoMapper.toDocument(version)));
    }

    @Override
    public Optional<TemplateVersion> findByVersionId(String tenantId, String templateVersionId) {
        return mongo.findById(templateVersionId)
                .filter(d -> tenantId.equals(d.getTenantId()))
                .map(MongoMapper::toDomain);
    }

    @Override
    public Optional<TemplateVersion> findByTemplateIdAndVersion(String tenantId, String templateId, int version) {
        return mongo.findByTenantIdAndTemplateIdAndVersion(tenantId, templateId, version).map(MongoMapper::toDomain);
    }

    @Override
    public List<TemplateVersion> findByTemplateId(String tenantId, String templateId) {
        return mongo.findByTenantIdAndTemplateIdOrderByVersionAsc(tenantId, templateId).stream()
                .map(MongoMapper::toDomain)
                .toList();
    }

    @Override
    public int nextVersionNumber(String tenantId, String templateId) {
        return findByTemplateId(tenantId, templateId).stream()
                .map(TemplateVersion::version)
                .max(Comparator.naturalOrder())
                .orElse(0) + 1;
    }
}
