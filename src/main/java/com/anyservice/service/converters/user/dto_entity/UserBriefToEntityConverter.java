package com.anyservice.service.converters.user.dto_entity;

import com.anyservice.dto.user.UserBrief;
import com.anyservice.entity.UserEntity;
import org.springframework.core.convert.converter.Converter;

public class UserBriefToEntityConverter implements Converter<UserBrief, UserEntity> {
    @Override
    public UserEntity convert(UserBrief source) {
        UserEntity entity = UserEntity.builder()
                .dtCreate(source.getDtCreate())
                .dtUpdate(source.getDtUpdate())
                .userName(source.getUserName())
                .initials(source.getInitials())
                .build();

        entity.setUuid(source.getUuid());

        return entity;
    }
}
