package com.anyservice.service.converters.user.entity_dto;

import com.anyservice.core.enums.LegalStatus;
import com.anyservice.core.enums.UserRole;
import com.anyservice.core.enums.UserState;
import com.anyservice.dto.user.UserDetailed;
import com.anyservice.entity.user.UserEntity;
import com.anyservice.service.converters.file.entity_dto.FileEntityToDetailedConverter;
import org.springframework.core.convert.converter.Converter;

public class UserEntityToDetailedConverter implements Converter<UserEntity, UserDetailed> {

    private final FileEntityToDetailedConverter fileConverter;

    public UserEntityToDetailedConverter(FileEntityToDetailedConverter fileConverter) {
        this.fileConverter = fileConverter;
    }

    @Override
    public UserDetailed convert(UserEntity source) {
        return UserDetailed.builder()
                .uuid(source.getUuid())
                .dtUpdate(source.getDtUpdate())
                .dtCreate(source.getDtCreate())
                .passwordUpdateDate(source.getPasswordUpdateDate())
                .userName(source.getUserName())
                .contacts(source.getContacts())
                .description(source.getDescription())
                .isLegalStatusVerified(source.getIsLegalStatusVerified())
                .isVerified(source.getIsVerified())
                .legalStatus(source.getLegalStatus() != null ? LegalStatus.valueOf(source.getLegalStatus()) : null)
                .initials(source.getInitials())
                .role(source.getRole() != null ? UserRole.valueOf(source.getRole()) : null)
                .state(source.getState() != null ? UserState.valueOf(source.getState()) : null)
                .address(source.getAddress())
                .password(source.getPassword())
                .country(source.getCountry())
                .profilePhoto(source.getPhoto() != null ? fileConverter.convert(source.getPhoto()) : null)
                .build();
    }
}
