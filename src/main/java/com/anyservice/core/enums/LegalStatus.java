package com.anyservice.core.enums;

/**
 * User possible legal statuses
 */
public enum LegalStatus {
    LLC(""),
    JSC("");

    private final String description;

    LegalStatus(String description) {
        this.description = description;
    }
}
