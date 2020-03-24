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
                .extension(FileExtension.valueOf(source.getExtension()))
                .size(source.getSize())
                .state(FileState.valueOf(source.getState()))
                .fileType(FileType.valueOf(source.getType()))
                .build();
    }
}
