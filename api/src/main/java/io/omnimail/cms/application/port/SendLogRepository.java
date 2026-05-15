package io.omnimail.cms.application.port;

import io.omnimail.cms.domain.model.SendLog;

public interface SendLogRepository {

    SendLog save(SendLog sendLog);
}
