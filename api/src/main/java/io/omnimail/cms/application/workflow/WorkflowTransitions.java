package io.omnimail.cms.application.workflow;

import io.omnimail.cms.domain.exception.WorkflowException;
import io.omnimail.cms.domain.model.WorkflowStatus;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public final class WorkflowTransitions {

    private static final Map<WorkflowStatus, Set<WorkflowStatus>> ALLOWED = Map.of(
            WorkflowStatus.DRAFT, EnumSet.of(WorkflowStatus.IN_REVIEW),
            WorkflowStatus.IN_REVIEW, EnumSet.of(WorkflowStatus.APPROVED, WorkflowStatus.REJECTED),
            WorkflowStatus.APPROVED, EnumSet.of(WorkflowStatus.PUBLISHING, WorkflowStatus.REJECTED),
            WorkflowStatus.PUBLISHING, EnumSet.of(WorkflowStatus.PUBLISHED, WorkflowStatus.FAILED),
            WorkflowStatus.FAILED, EnumSet.of(WorkflowStatus.PUBLISHING),
            WorkflowStatus.REJECTED, EnumSet.of(WorkflowStatus.DRAFT));

    private WorkflowTransitions() {
    }

    public static void assertTransition(WorkflowStatus from, WorkflowStatus to) {
        if (from == to) {
            return;
        }
        Set<WorkflowStatus> targets = ALLOWED.getOrDefault(from, Set.of());
        if (!targets.contains(to)) {
            throw new WorkflowException("Invalid workflow transition from " + from + " to " + to);
        }
    }
}
