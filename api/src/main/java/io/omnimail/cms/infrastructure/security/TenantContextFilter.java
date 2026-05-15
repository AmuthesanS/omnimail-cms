package io.omnimail.cms.infrastructure.security;

import io.omnimail.cms.application.TenantContext;
import io.omnimail.cms.infrastructure.config.OmnimailProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TenantContextFilter extends OncePerRequestFilter {

    private static final String TENANT_HEADER = "X-Tenant-Id";
    private static final String ACTOR_HEADER = "X-User-Id";
    private static final String ROLES_HEADER = "X-User-Roles";

    private final OmnimailProperties properties;

    public TenantContextFilter(OmnimailProperties properties) {
        this.properties = properties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            String tenant = headerOrDefault(request, TENANT_HEADER, properties.getTenant().getDefaultId());
            String actor = headerOrDefault(request, ACTOR_HEADER, "dev-user");
            Set<String> roles = parseRoles(request.getHeader(ROLES_HEADER));
            if (roles.isEmpty()) {
                roles = Set.of("editor", "reviewer", "publisher", "admin");
            }
            TenantContext.set(tenant, actor, roles);
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private static String headerOrDefault(HttpServletRequest request, String header, String defaultValue) {
        String value = request.getHeader(header);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private static Set<String> parseRoles(String header) {
        if (header == null || header.isBlank()) {
            return Set.of();
        }
        return Arrays.stream(header.split(",")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toSet());
    }
}
