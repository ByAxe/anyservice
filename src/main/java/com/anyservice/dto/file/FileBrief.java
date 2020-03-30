package com.anyservice.dto.file;

import com.anyservice.core.enums.FileExtension;
import com.anyservice.core.enums.FileState;
import com.anyservice.dto.api.APrimary;
import com.anyservice.dto.api.Brief;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Brief
public class FileBrief extends APrimary {
    private String name;
    private Long size;
    private FileExtension extension;
    private FileState state;
}
