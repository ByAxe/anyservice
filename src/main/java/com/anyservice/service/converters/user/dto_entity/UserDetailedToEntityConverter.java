package com.anyservice.service.converters.user.dto_entity;

import com.anyservice.dto.user.UserDetailed;
import com.anyservice.entity.user.UserEntity;
import org.springframework.core.convert.converter.Converter;

public class UserDetailedToEntityConverter implements Converter<UserDetailed, UserEntity> {
    @Override
    public UserEntity convert(UserDetailed source) {
        return UserEntity.builder()
                .uuid(source.getUuid())
                .dtCreate(source.getDtCreate())
                .dtUpdate(source.getDtUpdate())
                .password(source.getPassword())
                .passwordUpdateDate(source.getPasswordUpdateDate())
                .contacts(source.getContacts())
                .description(source.getDescription())
                .isLegalStatusVerified(source.isLegalStatusVerified())
                .isVerified(source.isVerified())
                .legalStatus(source.getLegalStatus() != null ? source.getLegalStatus().name() : null)
                .userName(source.getUserName())
                .initials(source.getInitials())
                .role(source.getRole() != null ? source.getRole().name() : null)
                .state(source.getState() != null ? source.getState().name() : null)
                .address(source.getAddress())
                .country(source.getCountry())
                .build();
    }
}
