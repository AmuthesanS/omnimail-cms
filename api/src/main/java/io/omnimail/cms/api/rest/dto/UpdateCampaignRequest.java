package io.omnimail.cms.api.rest.dto;

import jakarta.validation.constraints.Email;
import java.util.Map;

public record UpdateCampaignRequest(String name, String subject, @Email String fromEmail, Map<String, Object> variables) {}
