package io.omnimail.cms.infrastructure.mongo.repository;

import io.omnimail.cms.infrastructure.mongo.document.WorkflowEventDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WorkflowEventMongoRepository extends MongoRepository<WorkflowEventDocument, String> {}
