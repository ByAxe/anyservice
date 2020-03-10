package com.anyservice.dto;

import com.anyservice.dto.enums.LegalStatus;
import com.anyservice.entity.Contacts;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private UUID uuid;
    private OffsetDateTime dtCreate;
    private OffsetDateTime dtUpdate;
    private String description;
    private Contacts contacts;
    private LegalStatus legalStatus;
    private Boolean isVerified;
    private Boolean isLegalStatusVerified;
}
