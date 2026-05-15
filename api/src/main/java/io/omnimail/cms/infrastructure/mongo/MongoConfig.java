package io.omnimail.cms.infrastructure.mongo;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "io.omnimail.cms.infrastructure.mongo.repository")
public class MongoConfig {}
