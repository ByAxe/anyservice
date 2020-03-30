package com.anyservice.dto.file;

import com.anyservice.core.enums.FileType;
import com.anyservice.dto.api.Detailed;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.InputStream;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Detailed
public class FileDetailed extends FileBrief {
    @JsonIgnore
    private InputStream inputStream;
    private FileType fileType;
}
