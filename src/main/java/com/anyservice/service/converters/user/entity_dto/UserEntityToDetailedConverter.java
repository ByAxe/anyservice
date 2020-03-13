package com.anyservice.service.converters.user.entity_dto;

import com.anyservice.dto.enums.LegalStatus;
import com.anyservice.dto.user.UserDetailed;
import com.anyservice.entity.UserEntity;
import org.springframework.core.convert.converter.Converter;

public class UserEntityToDetailedConverter implements Converter<UserEntity, UserDetailed> {
    @Override
    public UserDetailed convert(UserEntity source) {
        UserDetailed user = new UserDetailed();

        user.setUuid(source.getUuid());
        user.setDtUpdate(source.getDtUpdate());
        user.setDtCreate(source.getDtCreate());
        user.setUserName(source.getUserName());
        user.setContacts(source.getContacts());
        user.setDescription(source.getDescription());
        user.setIsLegalStatusVerified(source.getIsLegalStatusVerified());
        user.setIsVerified(source.getIsVerified());
        user.setLegalStatus(LegalStatus.valueOf(source.getLegalStatus()));
        user.setInitials(source.getInitials());

        user.setPassword(source.getPassword());

        return user;
    }
}
