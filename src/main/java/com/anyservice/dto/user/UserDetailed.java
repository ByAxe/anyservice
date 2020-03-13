package com.anyservice.dto.user;

import com.anyservice.dto.enums.LegalStatus;
import com.anyservice.entity.Contacts;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailed extends UserBrief {
    private String description;
    private Contacts contacts;
    private LegalStatus legalStatus;
    private Boolean isVerified;
    private Boolean isLegalStatusVerified;
    private String password;
}
