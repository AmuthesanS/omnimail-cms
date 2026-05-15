package io.omnimail.cms.application.service;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import io.omnimail.cms.application.TenantContext;
import io.omnimail.cms.application.port.BlobStore;
import io.omnimail.cms.application.port.EmailSender;
import io.omnimail.cms.application.port.SendLogRepository;
import io.omnimail.cms.application.port.TemplateVersionRepository;
import io.omnimail.cms.domain.exception.NotFoundException;
import io.omnimail.cms.domain.exception.WorkflowException;
import io.omnimail.cms.domain.model.Campaign;
import io.omnimail.cms.domain.model.SendLog;
import io.omnimail.cms.domain.model.TemplateVersion;
import io.omnimail.cms.domain.model.WorkflowStatus;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class SendService {

    private final CampaignService campaignService;
    private final TemplateVersionRepository versionRepository;
    private final BlobStore blobStore;
    private final EmailSender emailSender;
    private final SendLogRepository sendLogRepository;
    private final MustacheFactory mustacheFactory = new DefaultMustacheFactory();

    public SendService(
            CampaignService campaignService,
            TemplateVersionRepository versionRepository,
            BlobStore blobStore,
            EmailSender emailSender,
            SendLogRepository sendLogRepository) {
        this.campaignService = campaignService;
        this.versionRepository = versionRepository;
        this.blobStore = blobStore;
        this.emailSender = emailSender;
        this.sendLogRepository = sendLogRepository;
    }

    public SendLog send(
            String campaignId, List<String> recipients, Map<String, Object> variables, String mode) {
        if (!TenantContext.hasRole("editor") && !TenantContext.hasRole("publisher")) {
            throw new WorkflowException("Editor or publisher role required");
        }
        Campaign campaign = campaignService.get(campaignId);
        String tenantId = TenantContext.requireTenantId();
        TemplateVersion version = versionRepository
                .findByVersionId(tenantId, campaign.templateVersionId())
                .orElseThrow(() -> new NotFoundException("Template version not found"));
        if (version.status() != WorkflowStatus.PUBLISHED && !"test".equalsIgnoreCase(mode)) {
            throw new WorkflowException("Campaign template version must be published for production send");
        }
        String html = resolveHtml(version, campaign);
        Map<String, Object> merged = mergeVariables(campaign.variables(), variables);
        String rendered = renderMustache(html, merged);
        emailSender.send(campaign.fromEmail(), recipients, campaign.subject(), rendered);
        SendLog log = new SendLog(
                UUID.randomUUID().toString(),
                tenantId,
                campaignId,
                version.templateVersionId(),
                mode,
                recipients.size(),
                "sent",
                Instant.now());
        return sendLogRepository.save(log);
    }

    private String resolveHtml(TemplateVersion version, Campaign campaign) {
        if (version.htmlObjectKey() != null && !version.htmlObjectKey().isBlank()) {
            return blobStore
                    .get(version.htmlObjectKey())
                    .map(bytes -> new String(bytes, StandardCharsets.UTF_8))
                    .orElseThrow(() -> new NotFoundException("Rendered HTML not found in object store"));
        }
        if (version.mjmlSource() != null && !version.mjmlSource().isBlank()) {
            return version.mjmlSource();
        }
        throw new NotFoundException("No HTML content for template version");
    }

    private static Map<String, Object> mergeVariables(Map<String, Object> base, Map<String, Object> override) {
        java.util.HashMap<String, Object> merged = new java.util.HashMap<>();
        if (base != null) {
            merged.putAll(base);
        }
        if (override != null) {
            merged.putAll(override);
        }
        return merged;
    }

    private String renderMustache(String html, Map<String, Object> variables) {
        try {
            Mustache mustache = mustacheFactory.compile(new StringReader(html), "email");
            StringWriter writer = new StringWriter();
            mustache.execute(writer, variables).flush();
            return writer.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Template rendering failed", e);
        }
    }
}
