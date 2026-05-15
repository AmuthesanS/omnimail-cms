package io.omnimail.cms.api.rest.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

public record SendCampaignRequest(
        @NotEmpty List<String> recipients, Map<String, Object> variables, String mode) {}
