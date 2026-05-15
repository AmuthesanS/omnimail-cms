package io.omnimail.cms.api.rest.dto;

import java.util.Map;

public record UpdateTemplateRequest(String mjmlSource, Map<String, Object> variablesSchema) {}
