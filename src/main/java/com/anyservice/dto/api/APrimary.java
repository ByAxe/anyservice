package com.anyservice.dto.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class APrimary {
    private UUID uuid;
    private OffsetDateTime dtCreate;
    private OffsetDateTime dtUpdate;
}
