package com.anyservice.config;

import com.anyservice.service.converters.InitialsToStringConverter;
import com.anyservice.service.converters.file.dto_entity.FileBriefToEntityConverter;
import com.anyservice.service.converters.file.dto_entity.FileDetailedToEntityConverter;
import com.anyservice.service.converters.file.entity_dto.FileEntityToBriefConverter;
import com.anyservice.service.converters.file.entity_dto.FileEntityToDetailedConverter;
import com.anyservice.service.converters.user.dto_entity.UserBriefToEntityConverter;
import com.anyservice.service.converters.user.dto_entity.UserDetailedToEntityConverter;
import com.anyservice.service.converters.user.entity_dto.UserEntityToBriefConverter;
import com.anyservice.service.converters.user.entity_dto.UserEntityToDetailedConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Registration of converters those implement {@link org.springframework.core.convert.converter.Converter}
     * To be able to use them through {@link org.springframework.core.convert.ConversionService}
     *
     * @param registry registry of all converters
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        FileDetailedToEntityConverter fileDetailedToEntityConverter = new FileDetailedToEntityConverter();
        FileEntityToDetailedConverter fileEntityToDetailedConverter = new FileEntityToDetailedConverter();

        registry.addConverter(new FileBriefToEntityConverter());
        registry.addConverter(fileDetailedToEntityConverter);
        registry.addConverter(new FileEntityToBriefConverter());
        registry.addConverter(fileEntityToDetailedConverter);

        registry.addConverter(new UserDetailedToEntityConverter(fileDetailedToEntityConverter));
        registry.addConverter(new UserBriefToEntityConverter());
        registry.addConverter(new UserEntityToDetailedConverter(fileEntityToDetailedConverter));
        registry.addConverter(new UserEntityToBriefConverter());

        registry.addConverter(new InitialsToStringConverter());
    }
}
