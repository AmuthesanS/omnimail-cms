package io.omnimail.cms.application.workflow;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.omnimail.cms.domain.exception.WorkflowException;
import io.omnimail.cms.domain.model.WorkflowStatus;
import org.junit.jupiter.api.Test;

class WorkflowTransitionsTest {

    @Test
    void allowsDraftToInReview() {
        assertDoesNotThrow(() -> WorkflowTransitions.assertTransition(WorkflowStatus.DRAFT, WorkflowStatus.IN_REVIEW));
    }

    @Test
    void rejectsDraftToPublished() {
        assertThrows(
                WorkflowException.class,
                () -> WorkflowTransitions.assertTransition(WorkflowStatus.DRAFT, WorkflowStatus.PUBLISHED));
    }
}
