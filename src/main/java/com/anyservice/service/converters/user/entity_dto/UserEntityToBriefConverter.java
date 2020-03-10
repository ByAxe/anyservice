package com.anyservice.service.converters.user.entity_dto;

import com.anyservice.dto.user.UserBrief;
import com.anyservice.entity.UserEntity;
import org.springframework.core.convert.converter.Converter;

public class UserEntityToBriefConverter implements Converter<UserEntity, UserBrief> {
    @Override
    public UserBrief convert(UserEntity source) {
        UserBrief user = new UserBrief();

        user.setUuid(source.getUuid());
        user.setDtUpdate(source.getDtUpdate());
        user.setDtCreate(source.getDtCreate());
        user.setUserName(source.getUserName());

        return user;
    }
}
