package io.omnimail.cms.application;

import java.util.Collections;
import java.util.Set;

public final class TenantContext {

    private static final ThreadLocal<String> TENANT = new ThreadLocal<>();
    private static final ThreadLocal<String> ACTOR = new ThreadLocal<>();
    private static final ThreadLocal<Set<String>> ROLES = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void set(String tenantId, String actor, Set<String> roles) {
        TENANT.set(tenantId);
        ACTOR.set(actor);
        ROLES.set(roles == null ? Set.of() : Set.copyOf(roles));
    }

    public static void clear() {
        TENANT.remove();
        ACTOR.remove();
        ROLES.remove();
    }

    public static String requireTenantId() {
        String tenant = TENANT.get();
        if (tenant == null || tenant.isBlank()) {
            throw new IllegalStateException("Tenant context is not set");
        }
        return tenant;
    }

    public static String requireActor() {
        String actor = ACTOR.get();
        return actor == null || actor.isBlank() ? "system" : actor;
    }

    public static Set<String> roles() {
        Set<String> roles = ROLES.get();
        return roles == null ? Collections.emptySet() : roles;
    }

    public static boolean hasRole(String role) {
        return roles().contains(role) || roles().contains("admin");
    }
}
