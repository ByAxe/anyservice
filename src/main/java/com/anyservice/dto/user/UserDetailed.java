package com.anyservice.dto.user;

import com.anyservice.core.enums.LegalStatus;
import com.anyservice.entity.user.Contacts;
import com.anyservice.entity.user.Country;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserDetailed extends UserBrief {
    private OffsetDateTime passwordUpdateDate;
    private String description;
    private Contacts contacts;
    private LegalStatus legalStatus;
    private boolean isVerified;
    private boolean isLegalStatusVerified;
    private String password;
    private String address;
    private Country country;
}
