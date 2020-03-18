package com.anyservice.service.converters.user.dto_entity;

import com.anyservice.dto.user.UserBrief;
import com.anyservice.entity.user.UserEntity;
import org.springframework.core.convert.converter.Converter;

public class UserBriefToEntityConverter implements Converter<UserBrief, UserEntity> {
    @Override
    public UserEntity convert(UserBrief source) {
        return UserEntity.builder()
                .uuid(source.getUuid())
                .dtCreate(source.getDtCreate())
                .dtUpdate(source.getDtUpdate())
                .userName(source.getUserName())
                .initials(source.getInitials())
                .role(source.getRole() != null ? source.getRole().name() : null)
                .state(source.getState() != null ? source.getState().name() : null)
                .build();
    }
}
