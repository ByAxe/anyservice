package com.anyservice.core.enums;

/**
 * Possible state of a user account
 */
public enum UserState {
    ACTIVE("Active", true, true),
    WAITING("Waiting for verification", false, true),
    BLOCKED("Blocked", true, false);

    private boolean enabled;
    private boolean nonLocked;
    private String description;

    UserState(String description, boolean isEnabled, boolean isNonLocked) {
        this.description = description;
        this.enabled = isEnabled;
        this.nonLocked = isNonLocked;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isNonLocked() {
        return nonLocked;
    }

    public String getDescription() {
        return description;
    }
}
