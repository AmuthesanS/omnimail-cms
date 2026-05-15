package io.omnimail.cms.api.rest;

import io.omnimail.cms.api.rest.dto.CreateCampaignRequest;
import io.omnimail.cms.api.rest.dto.SendCampaignRequest;
import io.omnimail.cms.api.rest.dto.UpdateCampaignRequest;
import io.omnimail.cms.application.service.CampaignService;
import io.omnimail.cms.application.service.SendService;
import io.omnimail.cms.domain.model.Campaign;
import io.omnimail.cms.domain.model.SendLog;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping("/api/v1/campaigns")
public class CampaignController {

    private final CampaignService campaignService;
    private final SendService sendService;

    public CampaignController(CampaignService campaignService, SendService sendService) {
        this.campaignService = campaignService;
        this.sendService = sendService;
    }

    @PostMapping
    public ResponseEntity<Campaign> create(@Valid @RequestBody CreateCampaignRequest request) {
        Campaign created = campaignService.create(
                request.name(),
                request.templateVersionId(),
                request.subject(),
                request.fromEmail(),
                request.variables());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public List<Campaign> list() {
        return campaignService.list();
    }

    @GetMapping("/{id}")
    public Campaign get(@PathVariable String id) {
        return campaignService.get(id);
    }

    @PutMapping("/{id}")
    public Campaign update(@PathVariable String id, @RequestBody UpdateCampaignRequest request) {
        Campaign existing = campaignService.get(id);
        return campaignService.update(
                id,
                request.name() != null ? request.name() : existing.name(),
                request.subject() != null ? request.subject() : existing.subject(),
                request.fromEmail() != null ? request.fromEmail() : existing.fromEmail(),
                request.variables() != null ? request.variables() : existing.variables());
    }

    @PostMapping("/{id}/send")
    public SendLog send(@PathVariable String id, @Valid @RequestBody SendCampaignRequest request) {
        String mode = request.mode() == null ? "test" : request.mode();
        return sendService.send(id, request.recipients(), request.variables(), mode);
    }
}
