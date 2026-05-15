package io.omnimail.cms.infrastructure.mongo;

import io.omnimail.cms.application.port.TemplateRepository;
import io.omnimail.cms.domain.model.Template;
import io.omnimail.cms.infrastructure.mongo.repository.TemplateMongoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class SpringTemplateRepository implements TemplateRepository {

    private final TemplateMongoRepository mongo;

    SpringTemplateRepository(TemplateMongoRepository mongo) {
        this.mongo = mongo;
    }

    @Override
    public Template save(Template template) {
        return MongoMapper.toDomain(mongo.save(MongoMapper.toDocument(template)));
    }

    @Override
    public Optional<Template> findById(String tenantId, String id) {
        return mongo.findById(id).filter(d -> tenantId.equals(d.getTenantId())).map(MongoMapper::toDomain);
    }

    @Override
    public List<Template> findAllByTenant(String tenantId) {
        return mongo.findByTenantId(tenantId).stream().map(MongoMapper::toDomain).toList();
    }

    @Override
    public void delete(String tenantId, String id) {
        mongo.findById(id).filter(d -> tenantId.equals(d.getTenantId())).ifPresent(mongo::delete);
    }
}
