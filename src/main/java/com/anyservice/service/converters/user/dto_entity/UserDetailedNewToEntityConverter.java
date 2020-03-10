package com.anyservice.service.converters.user.dto_entity;

import com.anyservice.dto.user.UserDetailedNew;
import com.anyservice.entity.UserEntity;
import org.springframework.core.convert.converter.Converter;

public class UserDetailedNewToEntityConverter implements Converter<UserDetailedNew, UserEntity> {
    @Override
    public UserEntity convert(UserDetailedNew source) {
        UserEntity entity = UserEntity.builder()
                .dtCreate(source.getDtCreate())
                .dtUpdate(source.getDtUpdate())
                .contacts(source.getContacts())
                .description(source.getDescription())
                .isLegalStatusVerified(source.getIsLegalStatusVerified())
                .isVerified(source.getIsVerified())
                .legalStatus(source.getLegalStatus().name())
                .userName(source.getUserName())
                .build();

        entity.setUuid(source.getUuid());

        return entity;
    }
}
