package com.anyservice.dto.user;

import com.anyservice.core.enums.LegalStatus;
import com.anyservice.dto.api.Detailed;
import com.anyservice.dto.file.FileDetailed;
import com.anyservice.entity.user.Contacts;
import com.anyservice.entity.user.CountryEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Detailed
public class UserDetailed extends UserBrief {
    private OffsetDateTime passwordUpdateDate;
    private String description;
    private Contacts contacts;
    private LegalStatus legalStatus;
    private boolean isVerified;
    private boolean isLegalStatusVerified;
    private String password;
    private Map<String, String> addresses;
    private CountryEntity defaultCountry;
    private FileDetailed profilePhoto;
    private List<FileDetailed> documents;
    private List<FileDetailed> portfolio;
    private List<CountryEntity> listOfCountriesWhereServicesProvided;
}
