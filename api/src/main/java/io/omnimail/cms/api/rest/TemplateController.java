package io.omnimail.cms.api.rest;

import io.omnimail.cms.api.rest.dto.CreateTemplateRequest;
import io.omnimail.cms.api.rest.dto.RejectRequest;
import io.omnimail.cms.api.rest.dto.UpdateTemplateRequest;
import io.omnimail.cms.application.service.TemplateService;
import io.omnimail.cms.domain.model.Template;
import io.omnimail.cms.domain.model.TemplateVersion;
import io.omnimail.cms.infrastructure.config.OmnimailProperties;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/templates")
public class TemplateController {

    private final TemplateService templateService;
    private final OmnimailProperties properties;

    public TemplateController(TemplateService templateService, OmnimailProperties properties) {
        this.templateService = templateService;
        this.properties = properties;
    }

    @PostMapping
    public ResponseEntity<Template> create(@Valid @RequestBody CreateTemplateRequest request) {
        Template created = templateService.create(
                request.name(),
                request.tags() == null ? List.of() : request.tags(),
                request.mjmlSource(),
                request.variablesSchema());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public List<Template> list() {
        return templateService.list();
    }

    @GetMapping("/{id}")
    public Template get(@PathVariable String id) {
        return templateService.get(id);
    }

    @PutMapping("/{id}")
    public TemplateVersion update(@PathVariable String id, @RequestBody UpdateTemplateRequest request) {
        return templateService.updateDraft(id, request.mjmlSource(), request.variablesSchema());
    }

    @GetMapping("/{id}/versions")
    public List<TemplateVersion> versions(@PathVariable String id) {
        return templateService.listVersions(id);
    }

    @GetMapping("/{id}/versions/{version}")
    public TemplateVersion version(@PathVariable String id, @PathVariable int version) {
        return templateService.getVersion(id, version);
    }

    @PostMapping("/{id}/submit")
    public TemplateVersion submit(@PathVariable String id) {
        return templateService.submit(id);
    }

    @PostMapping("/{id}/approve")
    public TemplateVersion approve(@PathVariable String id) {
        return templateService.approve(id);
    }

    @PostMapping("/{id}/reject")
    public TemplateVersion reject(@PathVariable String id, @RequestBody(required = false) RejectRequest request) {
        String comment = request == null ? null : request.comment();
        return templateService.reject(id, comment);
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<Map<String, Object>> publish(@PathVariable String id) {
        TemplateVersion publishing = templateService.markPublishing(id);
        String gitAuthor = properties.getSystem().getGit().getAuthor().getEmail();
        return ResponseEntity.accepted()
                .body(Map.of(
                        "templateVersionId", publishing.templateVersionId(),
                        "status", publishing.status().name(),
                        "message", "Publish accepted; complete via Cloud Build",
                        "gitAuthorEmail", gitAuthor));
    }
}
