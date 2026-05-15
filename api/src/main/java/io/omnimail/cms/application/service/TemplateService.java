package io.omnimail.cms.application.service;

import io.omnimail.cms.application.TenantContext;
import io.omnimail.cms.application.port.TemplateRepository;
import io.omnimail.cms.application.port.TemplateVersionRepository;
import io.omnimail.cms.application.port.WorkflowEventRepository;
import io.omnimail.cms.application.workflow.WorkflowTransitions;
import io.omnimail.cms.domain.exception.NotFoundException;
import io.omnimail.cms.domain.exception.WorkflowException;
import io.omnimail.cms.domain.model.Template;
import io.omnimail.cms.domain.model.TemplateVersion;
import io.omnimail.cms.domain.model.WorkflowEvent;
import io.omnimail.cms.domain.model.WorkflowStatus;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final TemplateVersionRepository versionRepository;
    private final WorkflowEventRepository workflowEventRepository;

    public TemplateService(
            TemplateRepository templateRepository,
            TemplateVersionRepository versionRepository,
            WorkflowEventRepository workflowEventRepository) {
        this.templateRepository = templateRepository;
        this.versionRepository = versionRepository;
        this.workflowEventRepository = workflowEventRepository;
    }

    public Template create(String name, List<String> tags, String mjmlSource, Map<String, Object> variablesSchema) {
        requireEditor();
        String tenantId = TenantContext.requireTenantId();
        Instant now = Instant.now();
        String templateId = UUID.randomUUID().toString();
        Template template = new Template(templateId, tenantId, name, tags, now, now);
        templateRepository.save(template);

        int version = 1;
        TemplateVersion draft = new TemplateVersion(
                versionId(templateId, version),
                tenantId,
                templateId,
                version,
                WorkflowStatus.DRAFT,
                mjmlSource,
                null,
                variablesSchema,
                List.of(),
                null,
                null,
                null,
                null,
                now);
        versionRepository.save(draft);
        return template;
    }

    public Template get(String templateId) {
        return templateRepository
                .findById(TenantContext.requireTenantId(), templateId)
                .orElseThrow(() -> new NotFoundException("Template not found: " + templateId));
    }

    public List<Template> list() {
        return templateRepository.findAllByTenant(TenantContext.requireTenantId());
    }

    public TemplateVersion updateDraft(String templateId, String mjmlSource, Map<String, Object> variablesSchema) {
        requireEditor();
        String tenantId = TenantContext.requireTenantId();
        get(templateId);
        TemplateVersion current = latestDraftOrEditable(tenantId, templateId);
        if (current.status() != WorkflowStatus.DRAFT && current.status() != WorkflowStatus.REJECTED) {
            throw new WorkflowException("Only draft or rejected templates can be edited");
        }
        TemplateVersion updated = new TemplateVersion(
                current.templateVersionId(),
                tenantId,
                templateId,
                current.version(),
                current.status(),
                mjmlSource,
                current.htmlObjectKey(),
                variablesSchema,
                current.assetRefs(),
                current.gitRef(),
                current.sourcePath(),
                current.publishedAt(),
                current.publishedBy(),
                current.createdAt());
        return versionRepository.save(updated);
    }

    public List<TemplateVersion> listVersions(String templateId) {
        get(templateId);
        return versionRepository.findByTemplateId(TenantContext.requireTenantId(), templateId);
    }

    public TemplateVersion getVersion(String templateId, int version) {
        get(templateId);
        return versionRepository
                .findByTemplateIdAndVersion(TenantContext.requireTenantId(), templateId, version)
                .orElseThrow(() -> new NotFoundException("Version not found: " + version));
    }

    public TemplateVersion submit(String templateId) {
        return transitionLatest(templateId, WorkflowStatus.IN_REVIEW, "editor");
    }

    public TemplateVersion approve(String templateId) {
        return transitionLatest(templateId, WorkflowStatus.APPROVED, "reviewer");
    }

    public TemplateVersion reject(String templateId, String comment) {
        TemplateVersion version = transitionLatest(templateId, WorkflowStatus.REJECTED, "reviewer");
        recordEvent(version, comment);
        return version;
    }

    public TemplateVersion markPublishing(String templateId) {
        return transitionLatest(templateId, WorkflowStatus.PUBLISHING, "publisher");
    }

    public TemplateVersion markPublished(String templateId, String htmlObjectKey, String publishedBy) {
        requirePublisher();
        String tenantId = TenantContext.requireTenantId();
        TemplateVersion current = latestVersion(tenantId, templateId);
        WorkflowTransitions.assertTransition(current.status(), WorkflowStatus.PUBLISHED);
        TemplateVersion published = new TemplateVersion(
                current.templateVersionId(),
                tenantId,
                templateId,
                current.version(),
                WorkflowStatus.PUBLISHED,
                current.mjmlSource(),
                htmlObjectKey,
                current.variablesSchema(),
                current.assetRefs(),
                current.gitRef(),
                current.sourcePath(),
                Instant.now(),
                publishedBy,
                current.createdAt());
        versionRepository.save(published);
        recordEvent(published, "Published");
        return published;
    }

    private TemplateVersion transitionLatest(String templateId, WorkflowStatus target, String requiredRole) {
        if ("editor".equals(requiredRole)) {
            requireEditor();
        } else if ("reviewer".equals(requiredRole)) {
            requireReviewer();
        } else if ("publisher".equals(requiredRole)) {
            requirePublisher();
        }
        String tenantId = TenantContext.requireTenantId();
        get(templateId);
        TemplateVersion current = latestVersion(tenantId, templateId);
        WorkflowTransitions.assertTransition(current.status(), target);
        TemplateVersion updated = new TemplateVersion(
                current.templateVersionId(),
                tenantId,
                templateId,
                current.version(),
                target,
                current.mjmlSource(),
                current.htmlObjectKey(),
                current.variablesSchema(),
                current.assetRefs(),
                current.gitRef(),
                current.sourcePath(),
                current.publishedAt(),
                current.publishedBy(),
                current.createdAt());
        versionRepository.save(updated);
        recordEvent(updated, null);
        return updated;
    }

    private TemplateVersion latestVersion(String tenantId, String templateId) {
        List<TemplateVersion> versions = versionRepository.findByTemplateId(tenantId, templateId);
        if (versions.isEmpty()) {
            throw new NotFoundException("No versions for template: " + templateId);
        }
        return versions.get(versions.size() - 1);
    }

    private TemplateVersion latestDraftOrEditable(String tenantId, String templateId) {
        return latestVersion(tenantId, templateId);
    }

    private void recordEvent(TemplateVersion version, String comment) {
        workflowEventRepository.save(new WorkflowEvent(
                UUID.randomUUID().toString(),
                version.tenantId(),
                "template_version",
                version.templateVersionId(),
                null,
                version.status(),
                TenantContext.requireActor(),
                comment,
                Instant.now()));
    }

    private static String versionId(String templateId, int version) {
        return "tpl_" + templateId + "_v" + version;
    }

    private static void requireEditor() {
        if (!TenantContext.hasRole("editor")) {
            throw new WorkflowException("Editor role required");
        }
    }

    private static void requireReviewer() {
        if (!TenantContext.hasRole("reviewer")) {
            throw new WorkflowException("Reviewer role required");
        }
    }

    private static void requirePublisher() {
        if (!TenantContext.hasRole("publisher")) {
            throw new WorkflowException("Publisher role required");
        }
    }
}
