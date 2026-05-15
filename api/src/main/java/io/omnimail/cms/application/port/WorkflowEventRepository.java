package io.omnimail.cms.application.port;

import io.omnimail.cms.domain.model.WorkflowEvent;

public interface WorkflowEventRepository {

    WorkflowEvent save(WorkflowEvent event);
}
