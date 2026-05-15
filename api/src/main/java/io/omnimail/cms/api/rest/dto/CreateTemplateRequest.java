package io.omnimail.cms.api.rest.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

public record CreateTemplateRequest(
        @NotBlank String name,
        List<String> tags,
        String mjmlSource,
        Map<String, Object> variablesSchema) {}
