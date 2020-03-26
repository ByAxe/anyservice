package com.anyservice.service.converters.file.entity_dto;

import com.anyservice.core.enums.FileExtension;
import com.anyservice.core.enums.FileState;
import com.anyservice.dto.file.FileBrief;
import com.anyservice.entity.file.FileEntity;
import org.springframework.core.convert.converter.Converter;

public class FileEntityToBriefConverter implements Converter<FileEntity, FileBrief> {
    @Override
    public FileBrief convert(FileEntity source) {
        return FileBrief.builder()
                .uuid(source.getUuid())
                .dtCreate(source.getDtCreate())
                .name(source.getName())
                .extension(source.getExtension() != null ? FileExtension.valueOf(source.getExtension()) : null)
                .size(source.getSize())
                .state(source.getState() != null ? FileState.valueOf(source.getState()) : null)
                .build();
    }
}
