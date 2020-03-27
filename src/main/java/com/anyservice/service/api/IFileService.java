package com.anyservice.service.api;

import com.anyservice.dto.file.FileBrief;
import com.anyservice.dto.file.FileDetailed;

import java.util.Date;
import java.util.UUID;

public interface IFileService extends ICRUDService<FileBrief, FileDetailed, UUID, Date> {

    /**
     * Delete file by identifier while not having its version
     * !Only for service-service calling purposes!
     *
     * @param uuid identifier
     */
    void deleteById(UUID uuid);
}
