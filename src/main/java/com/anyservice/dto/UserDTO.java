package com.anyservice.dto;

import com.anyservice.entity.Contacts;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class UserDTO {
    private UUID uuid;
    private OffsetDateTime dtCreate;
    private OffsetDateTime dtUpdate;
    private String description;
    private Contacts contacts;
    private String legalStatus;
    private Boolean isVerified;
    private Boolean isLegalStatusVerified;
}
