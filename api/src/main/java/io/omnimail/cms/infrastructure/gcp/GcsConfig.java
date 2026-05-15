package io.omnimail.cms.infrastructure.gcp;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import io.omnimail.cms.infrastructure.config.OmnimailProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "omnimail.storage.type", havingValue = "gcs")
public class GcsConfig {

    @Bean
    Storage storage(OmnimailProperties properties) {
        return StorageOptions.newBuilder()
                .setProjectId(properties.getGcs().getProjectId())
                .build()
                .getService();
    }
}
