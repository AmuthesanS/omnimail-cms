package io.omnimail.cms.application.service;

import io.omnimail.cms.application.TenantContext;
import io.omnimail.cms.application.port.CampaignRepository;
import io.omnimail.cms.application.port.TemplateVersionRepository;
import io.omnimail.cms.domain.exception.NotFoundException;
import io.omnimail.cms.domain.exception.WorkflowException;
import io.omnimail.cms.domain.model.Campaign;
import io.omnimail.cms.domain.model.WorkflowStatus;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final TemplateVersionRepository versionRepository;

    public CampaignService(CampaignRepository campaignRepository, TemplateVersionRepository versionRepository) {
        this.campaignRepository = campaignRepository;
        this.versionRepository = versionRepository;
    }

    public Campaign create(
            String name,
            String templateVersionId,
            String subject,
            String fromEmail,
            Map<String, Object> variables) {
        if (!TenantContext.hasRole("editor")) {
            throw new WorkflowException("Editor role required");
        }
        String tenantId = TenantContext.requireTenantId();
        versionRepository
                .findByVersionId(tenantId, templateVersionId)
                .orElseThrow(() -> new NotFoundException("Template version not found: " + templateVersionId));
        Instant now = Instant.now();
        Campaign campaign = new Campaign(
                UUID.randomUUID().toString(),
                tenantId,
                name,
                templateVersionId,
                subject,
                fromEmail,
                variables,
                WorkflowStatus.DRAFT,
                now,
                now);
        return campaignRepository.save(campaign);
    }

    public Campaign get(String id) {
        return campaignRepository
                .findById(TenantContext.requireTenantId(), id)
                .orElseThrow(() -> new NotFoundException("Campaign not found: " + id));
    }

    public List<Campaign> list() {
        return campaignRepository.findAllByTenant(TenantContext.requireTenantId());
    }

    public Campaign update(String id, String name, String subject, String fromEmail, Map<String, Object> variables) {
        if (!TenantContext.hasRole("editor")) {
            throw new WorkflowException("Editor role required");
        }
        Campaign existing = get(id);
        if (existing.status() != WorkflowStatus.DRAFT) {
            throw new WorkflowException("Only draft campaigns can be updated");
        }
        Campaign updated = new Campaign(
                existing.id(),
                existing.tenantId(),
                name,
                existing.templateVersionId(),
                subject,
                fromEmail,
                variables,
                existing.status(),
                existing.createdAt(),
                Instant.now());
        return campaignRepository.save(updated);
    }
}
