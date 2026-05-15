package io.omnimail.cms.infrastructure.mongo;

import io.omnimail.cms.domain.model.Asset;
import io.omnimail.cms.domain.model.Campaign;
import io.omnimail.cms.domain.model.SendLog;
import io.omnimail.cms.domain.model.Template;
import io.omnimail.cms.domain.model.TemplateVersion;
import io.omnimail.cms.domain.model.WorkflowEvent;
import io.omnimail.cms.infrastructure.mongo.document.AssetDocument;
import io.omnimail.cms.infrastructure.mongo.document.CampaignDocument;
import io.omnimail.cms.infrastructure.mongo.document.SendLogDocument;
import io.omnimail.cms.infrastructure.mongo.document.TemplateDocument;
import io.omnimail.cms.infrastructure.mongo.document.TemplateVersionDocument;
import io.omnimail.cms.infrastructure.mongo.document.WorkflowEventDocument;

public final class MongoMapper {

    private MongoMapper() {
    }

    public static Template toDomain(TemplateDocument doc) {
        return new Template(doc.getId(), doc.getTenantId(), doc.getName(), doc.getTags(), doc.getCreatedAt(), doc.getUpdatedAt());
    }

    public static TemplateDocument toDocument(Template t) {
        TemplateDocument doc = new TemplateDocument();
        doc.setId(t.id());
        doc.setTenantId(t.tenantId());
        doc.setName(t.name());
        doc.setTags(t.tags());
        doc.setCreatedAt(t.createdAt());
        doc.setUpdatedAt(t.updatedAt());
        return doc;
    }

    public static TemplateVersion toDomain(TemplateVersionDocument doc) {
        return new TemplateVersion(
                doc.getTemplateVersionId(),
                doc.getTenantId(),
                doc.getTemplateId(),
                doc.getVersion(),
                doc.getStatus(),
                doc.getMjmlSource(),
                doc.getHtmlObjectKey(),
                doc.getVariablesSchema(),
                doc.getAssetRefs(),
                doc.getGitRef(),
                doc.getSourcePath(),
                doc.getPublishedAt(),
                doc.getPublishedBy(),
                doc.getCreatedAt());
    }

    public static TemplateVersionDocument toDocument(TemplateVersion v) {
        TemplateVersionDocument doc = new TemplateVersionDocument();
        doc.setTemplateVersionId(v.templateVersionId());
        doc.setTenantId(v.tenantId());
        doc.setTemplateId(v.templateId());
        doc.setVersion(v.version());
        doc.setStatus(v.status());
        doc.setMjmlSource(v.mjmlSource());
        doc.setHtmlObjectKey(v.htmlObjectKey());
        doc.setVariablesSchema(v.variablesSchema());
        doc.setAssetRefs(v.assetRefs());
        doc.setGitRef(v.gitRef());
        doc.setSourcePath(v.sourcePath());
        doc.setPublishedAt(v.publishedAt());
        doc.setPublishedBy(v.publishedBy());
        doc.setCreatedAt(v.createdAt());
        return doc;
    }

    public static Campaign toDomain(CampaignDocument doc) {
        return new Campaign(
                doc.getId(),
                doc.getTenantId(),
                doc.getName(),
                doc.getTemplateVersionId(),
                doc.getSubject(),
                doc.getFromEmail(),
                doc.getVariables(),
                doc.getStatus(),
                doc.getCreatedAt(),
                doc.getUpdatedAt());
    }

    public static CampaignDocument toDocument(Campaign c) {
        CampaignDocument doc = new CampaignDocument();
        doc.setId(c.id());
        doc.setTenantId(c.tenantId());
        doc.setName(c.name());
        doc.setTemplateVersionId(c.templateVersionId());
        doc.setSubject(c.subject());
        doc.setFromEmail(c.fromEmail());
        doc.setVariables(c.variables());
        doc.setStatus(c.status());
        doc.setCreatedAt(c.createdAt());
        doc.setUpdatedAt(c.updatedAt());
        return doc;
    }

    public static Asset toDomain(AssetDocument doc) {
        return new Asset(doc.getContentHash(), doc.getTenantId(), doc.getObjectKey(), doc.getMimeType(), doc.getSizeBytes(), doc.getCreatedAt());
    }

    public static AssetDocument toDocument(Asset a) {
        AssetDocument doc = new AssetDocument();
        doc.setContentHash(a.contentHash());
        doc.setTenantId(a.tenantId());
        doc.setObjectKey(a.objectKey());
        doc.setMimeType(a.mimeType());
        doc.setSizeBytes(a.sizeBytes());
        doc.setCreatedAt(a.createdAt());
        return doc;
    }

    public static WorkflowEvent toDomain(WorkflowEventDocument doc) {
        return new WorkflowEvent(
                doc.getId(),
                doc.getTenantId(),
                doc.getEntityType(),
                doc.getEntityId(),
                doc.getFromStatus(),
                doc.getToStatus(),
                doc.getActor(),
                doc.getComment(),
                doc.getOccurredAt());
    }

    public static WorkflowEventDocument toDocument(WorkflowEvent e) {
        WorkflowEventDocument doc = new WorkflowEventDocument();
        doc.setId(e.id());
        doc.setTenantId(e.tenantId());
        doc.setEntityType(e.entityType());
        doc.setEntityId(e.entityId());
        doc.setFromStatus(e.fromStatus());
        doc.setToStatus(e.toStatus());
        doc.setActor(e.actor());
        doc.setComment(e.comment());
        doc.setOccurredAt(e.occurredAt());
        return doc;
    }

    public static SendLog toDomain(SendLogDocument doc) {
        return new SendLog(
                doc.getId(),
                doc.getTenantId(),
                doc.getCampaignId(),
                doc.getTemplateVersionId(),
                doc.getMode(),
                doc.getRecipientCount(),
                doc.getStatus(),
                doc.getCreatedAt());
    }

    public static SendLogDocument toDocument(SendLog log) {
        SendLogDocument doc = new SendLogDocument();
        doc.setId(log.id());
        doc.setTenantId(log.tenantId());
        doc.setCampaignId(log.campaignId());
        doc.setTemplateVersionId(log.templateVersionId());
        doc.setMode(log.mode());
        doc.setRecipientCount(log.recipientCount());
        doc.setStatus(log.status());
        doc.setCreatedAt(log.createdAt());
        return doc;
    }
}
