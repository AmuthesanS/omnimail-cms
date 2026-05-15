package io.omnimail.cms.api.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;

public record CreateCampaignRequest(
        @NotBlank String name,
        @NotBlank String templateVersionId,
        @NotBlank String subject,
        @NotBlank @Email String fromEmail,
        Map<String, Object> variables) {}
