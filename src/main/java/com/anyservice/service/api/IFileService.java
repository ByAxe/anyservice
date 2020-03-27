package com.anyservice.service.api;

import com.anyservice.dto.file.FileBrief;
import com.anyservice.dto.file.FileDetailed;

import java.util.Date;
import java.util.UUID;

public interface IFileService extends ICRUDService<FileBrief, FileDetailed, UUID, Date> {
}
