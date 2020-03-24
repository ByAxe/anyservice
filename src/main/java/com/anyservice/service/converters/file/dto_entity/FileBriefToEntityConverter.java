package com.anyservice.service.converters.file.dto_entity;

import com.anyservice.dto.file.FileBrief;
import com.anyservice.entity.file.FileEntity;
import org.springframework.core.convert.converter.Converter;

public class FileBriefToEntityConverter implements Converter<FileBrief, FileEntity> {
    @Override
    public FileEntity convert(FileBrief source) {
        return FileEntity.builder()
                .uuid(source.getUuid())
                .dtCreate(source.getDtCreate())
                .name(source.getName())
                .extension(source.getExtension() != null ? source.getExtension().name() : null)
                .size(source.getSize())
                .state(source.getState() != null ? source.getState().name() : null)
                .build();
    }
}
