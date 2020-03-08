package com.anyservice.service.converters;

import com.anyservice.dto.UserDTO;
import com.anyservice.entity.UserEntity;
import org.springframework.core.convert.converter.Converter;

public class UserEntityToDTOConverter implements Converter<UserEntity, UserDTO> {
    @Override
    public UserDTO convert(UserEntity source) {
        return UserDTO.builder()
                .uuid(source.getUuid())
                .dtUpdate(source.getDtUpdate())
                .dtCreate(source.getDtCreate())
                .contacts(source.getContacts())
                .description(source.getDescription())
                .isVerified(source.getIsVerified())
                .legalStatus(source.getLegalStatus())
                .isLegalStatusVerified(source.getIsLegalStatusVerified())
                .build();
    }
}
