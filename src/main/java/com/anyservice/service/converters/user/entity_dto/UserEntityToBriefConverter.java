package com.anyservice.service.converters.user.entity_dto;

import com.anyservice.core.enums.UserRole;
import com.anyservice.core.enums.UserState;
import com.anyservice.dto.user.UserBrief;
import com.anyservice.entity.UserEntity;
import org.springframework.core.convert.converter.Converter;

public class UserEntityToBriefConverter implements Converter<UserEntity, UserBrief> {
    @Override
    public UserBrief convert(UserEntity source) {
        return UserBrief.builder()
                .uuid(source.getUuid())
                .dtCreate(source.getDtCreate())
                .dtUpdate(source.getDtUpdate())
                .userName(source.getUserName())
                .initials(source.getInitials())
                .role(UserRole.valueOf(source.getRole()))
                .state(UserState.valueOf(source.getState()))
                .build();
    }
}
