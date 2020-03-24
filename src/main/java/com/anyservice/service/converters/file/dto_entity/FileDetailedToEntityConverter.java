package com.anyservice.service.converters.file.dto_entity;

import com.anyservice.dto.file.FileDetailed;
import com.anyservice.entity.file.FileEntity;
import org.springframework.core.convert.converter.Converter;

public class FileDetailedToEntityConverter implements Converter<FileDetailed, FileEntity> {
    @Override
    public FileEntity convert(FileDetailed source) {
        return FileEntity.builder()
                .uuid(source.getUuid())
                .dtCreate(source.getDtCreate())
                .name(source.getName())
                .extension(source.getExtension() != null ? source.getExtension().name() : null)
                .size(source.getSize())
                .state(source.getState() != null ? source.getState().name() : null)
                .type(source.getFileType() != null ? source.getFileType().name() : null)
                .build();
    }
}
