package com.anyservice.config;

import com.anyservice.service.converters.InitialsToStringConverter;
import com.anyservice.service.converters.user.dto_entity.UserBriefToEntityConverter;
import com.anyservice.service.converters.user.dto_entity.UserDetailedToEntityConverter;
import com.anyservice.service.converters.user.entity_dto.UserEntityToBriefConverter;
import com.anyservice.service.converters.user.entity_dto.UserEntityToDetailedConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new UserDetailedToEntityConverter());
        registry.addConverter(new UserBriefToEntityConverter());
        registry.addConverter(new UserEntityToDetailedConverter());
        registry.addConverter(new UserEntityToBriefConverter());
        registry.addConverter(new InitialsToStringConverter());
    }
}
