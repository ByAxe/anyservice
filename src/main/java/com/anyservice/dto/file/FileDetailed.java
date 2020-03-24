package com.anyservice.dto.file;

import com.anyservice.core.enums.FileType;
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
public class FileDetailed extends FileBrief {
    private InputStream inputStream;
    private FileType fileType;
}
