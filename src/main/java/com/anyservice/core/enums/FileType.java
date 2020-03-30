package com.anyservice.core.enums;

/**
 * Describes, to what Domain (business area/operation)
 * the file belongs to
 */
public enum FileType {
    PROFILE_PHOTO("User profile photo"),
    PORTFOLIO("Portfolio of an individual or a company"),
    DOCUMENT("Document for approval of user profile");

    private final String description;

    FileType(String description) {
        this.description = description;
    }
}
