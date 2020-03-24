package com.anyservice.service.validators;

import com.anyservice.dto.file.FileDetailed;
import com.anyservice.service.validators.api.file.IFileValidator;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FileValidator implements IFileValidator {
    @Override
    public Map<String, Object> validateCreation(FileDetailed entity) {
        // TODO implement
        return null;
    }

    @Override
    public Map<String, Object> validateUpdates(FileDetailed entity) {
        throw new UnsupportedOperationException();
    }
}
