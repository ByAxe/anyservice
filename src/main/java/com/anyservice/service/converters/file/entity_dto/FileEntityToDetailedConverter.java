package com.anyservice.service.converters.file.entity_dto;

import com.anyservice.core.enums.FileExtension;
import com.anyservice.core.enums.FileState;
import com.anyservice.core.enums.FileType;
import com.anyservice.dto.file.FileDetailed;
import com.anyservice.entity.file.FileEntity;
import org.springframework.core.convert.converter.Converter;

public class FileEntityToDetailedConverter implements Converter<FileEntity, FileDetailed> {
    @Override
    public FileDetailed convert(FileEntity source) {
        return FileDetailed.builder()
                .uuid(source.getUuid())
                .dtCreate(source.getDtCreate())
                .name(source.getName())
                .extension(source.getExtension() != null ? FileExtension.valueOf(source.getExtension()) : null)
                .size(source.getSize())
                .state(source.getState() != null ? FileState.valueOf(source.getState()) : null)
                .fileType(source.getType() != null ? FileType.valueOf(source.getType()) : null)
                .build();
    }
}
