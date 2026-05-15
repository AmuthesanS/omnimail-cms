package io.omnimail.cms.infrastructure.mongo.repository;

import io.omnimail.cms.infrastructure.mongo.document.SendLogDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SendLogMongoRepository extends MongoRepository<SendLogDocument, String> {}
