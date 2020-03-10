package com.anyservice.dto.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class AEssence {
    private UUID uuid;
    private OffsetDateTime dtCreate;
    private OffsetDateTime dtUpdate;
}
