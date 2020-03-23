package com.anyservice.service.converters;

import com.anyservice.entity.user.Initials;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;

public class InitialsToStringConverter implements Converter<Initials, String> {

    @Override
    public String convert(Initials source) {
        StringBuilder builder = new StringBuilder();

        if (StringUtils.isNotEmpty(source.getLastName())) {
            builder.append(source.getMiddleName()).append(" ");
        }

        builder.append(source.getFirstName());

        if (StringUtils.isNotEmpty(source.getMiddleName())) {
            builder.append(" ").append(source.getMiddleName());
        }

        return builder.toString();
    }
}
