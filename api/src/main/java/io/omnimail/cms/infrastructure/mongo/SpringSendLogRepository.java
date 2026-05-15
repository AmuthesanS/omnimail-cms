package io.omnimail.cms.infrastructure.mongo;

import io.omnimail.cms.application.port.SendLogRepository;
import io.omnimail.cms.domain.model.SendLog;
import io.omnimail.cms.infrastructure.mongo.repository.SendLogMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public class SpringSendLogRepository implements SendLogRepository {

    private final SendLogMongoRepository mongo;

    SpringSendLogRepository(SendLogMongoRepository mongo) {
        this.mongo = mongo;
    }

    @Override
    public SendLog save(SendLog sendLog) {
        return MongoMapper.toDomain(mongo.save(MongoMapper.toDocument(sendLog)));
    }
}
