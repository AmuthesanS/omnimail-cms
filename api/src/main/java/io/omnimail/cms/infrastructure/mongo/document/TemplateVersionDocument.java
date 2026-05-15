package io.omnimail.cms.infrastructure.mongo.document;

import io.omnimail.cms.domain.model.WorkflowStatus;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "template_versions")
@CompoundIndex(name = "tenant_template_version", def = "{'tenantId': 1, 'templateId': 1, 'version': 1}", unique = true)
public class TemplateVersionDocument {

    @Id
    private String templateVersionId;
    private String tenantId;
    private String templateId;
    private int version;
    private WorkflowStatus status;
    private String mjmlSource;
    private String htmlObjectKey;
    private Map<String, Object> variablesSchema;
    private List<String> assetRefs;
    private String gitRef;
    private String sourcePath;
    private Instant publishedAt;
    private String publishedBy;
    private Instant createdAt;

    public String getTemplateVersionId() {
        return templateVersionId;
    }

    public void setTemplateVersionId(String templateVersionId) {
        this.templateVersionId = templateVersionId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public WorkflowStatus getStatus() {
        return status;
    }

    public void setStatus(WorkflowStatus status) {
        this.status = status;
    }

    public String getMjmlSource() {
        return mjmlSource;
    }

    public void setMjmlSource(String mjmlSource) {
        this.mjmlSource = mjmlSource;
    }

    public String getHtmlObjectKey() {
        return htmlObjectKey;
    }

    public void setHtmlObjectKey(String htmlObjectKey) {
        this.htmlObjectKey = htmlObjectKey;
    }

    public Map<String, Object> getVariablesSchema() {
        return variablesSchema;
    }

    public void setVariablesSchema(Map<String, Object> variablesSchema) {
        this.variablesSchema = variablesSchema;
    }

    public List<String> getAssetRefs() {
        return assetRefs;
    }

    public void setAssetRefs(List<String> assetRefs) {
        this.assetRefs = assetRefs;
    }

    public String getGitRef() {
        return gitRef;
    }

    public void setGitRef(String gitRef) {
        this.gitRef = gitRef;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getPublishedBy() {
        return publishedBy;
    }

    public void setPublishedBy(String publishedBy) {
        this.publishedBy = publishedBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
