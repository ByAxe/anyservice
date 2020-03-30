package com.anyservice.dto.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Root class for all DTO in application
 * Contains necessary fields for all DTOs
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class APrimary {
    private UUID uuid;
    private OffsetDateTime dtCreate;
    private OffsetDateTime dtUpdate;
}
