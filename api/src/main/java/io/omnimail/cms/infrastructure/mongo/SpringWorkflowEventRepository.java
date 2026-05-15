package io.omnimail.cms.infrastructure.mongo;

import io.omnimail.cms.application.port.WorkflowEventRepository;
import io.omnimail.cms.domain.model.WorkflowEvent;
import io.omnimail.cms.infrastructure.mongo.repository.WorkflowEventMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public class SpringWorkflowEventRepository implements WorkflowEventRepository {

    private final WorkflowEventMongoRepository mongo;

    SpringWorkflowEventRepository(WorkflowEventMongoRepository mongo) {
        this.mongo = mongo;
    }

    @Override
    public WorkflowEvent save(WorkflowEvent event) {
        return MongoMapper.toDomain(mongo.save(MongoMapper.toDocument(event)));
    }
}
