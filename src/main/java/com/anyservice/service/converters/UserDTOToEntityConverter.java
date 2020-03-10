package com.anyservice.service.converters;

import com.anyservice.dto.UserDTO;
import com.anyservice.entity.UserEntity;
import org.springframework.core.convert.converter.Converter;

public class UserDTOToEntityConverter implements Converter<UserDTO, UserEntity> {
    @Override
    public UserEntity convert(UserDTO source) {
        UserEntity entity = UserEntity.builder()
                .dtCreate(source.getDtCreate())
                .dtUpdate(source.getDtUpdate())
                .contacts(source.getContacts())
                .description(source.getDescription())
                .isLegalStatusVerified(source.getIsLegalStatusVerified())
                .isVerified(source.getIsVerified())
                .legalStatus(source.getLegalStatus().name())
                .build();

        entity.setUuid(source.getUuid());

        return entity;
    }
}
