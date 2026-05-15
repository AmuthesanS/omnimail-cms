package io.omnimail.cms.infrastructure.mongo.document;

import io.omnimail.cms.domain.model.WorkflowStatus;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "workflow_events")
public class WorkflowEventDocument {

    @Id
    private String id;
    private String tenantId;
    private String entityType;
    private String entityId;
    private WorkflowStatus fromStatus;
    private WorkflowStatus toStatus;
    private String actor;
    private String comment;
    private Instant occurredAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public WorkflowStatus getFromStatus() {
        return fromStatus;
    }

    public void setFromStatus(WorkflowStatus fromStatus) {
        this.fromStatus = fromStatus;
    }

    public WorkflowStatus getToStatus() {
        return toStatus;
    }

    public void setToStatus(WorkflowStatus toStatus) {
        this.toStatus = toStatus;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }
}
