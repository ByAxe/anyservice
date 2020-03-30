package com.anyservice.core.enums;

/**
 * Possible user roles
 */
public enum UserRole {
    ROLE_USER("Simply user"),
    ROLE_ADMIN("Administrator of service, that verifies the user"),
    ROLE_SUPER_ADMIN("Backend development role");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }
}
