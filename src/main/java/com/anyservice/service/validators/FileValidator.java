package com.anyservice.service.validators;

import com.anyservice.core.enums.FileExtension;
import com.anyservice.dto.file.FileDetailed;
import com.anyservice.service.validators.api.IFileValidator;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.anyservice.core.enums.FileExtension.isPhoto;
import static com.anyservice.core.enums.FileExtension.pdf;
import static org.springframework.context.i18n.LocaleContextHolder.getLocale;
import static org.springframework.util.StringUtils.isEmpty;

@Service
public class FileValidator implements IFileValidator {

    private final MessageSource messageSource;

    public FileValidator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public MessageSource getMessageSource() {
        return messageSource;
    }

    @Override
    public Map<String, Object> validateCreation(FileDetailed file) {
        Map<String, Object> errors = new HashMap<>();

        FileExtension extension = file.getExtension();

        // File type must present
        if (file.getFileType() == null) {
            errors.put("file.filetype", getMessageSource().getMessage("file.filetype.empty",
                    null, getLocale()));
        } else {
            boolean isPhoto = isPhoto(extension);

            // Validate file extension taking to into account its type
            switch (file.getFileType()) {
                case PROFILE_PHOTO:
                    if (!isPhoto) {
                        errors.put("file.extension", getMessageSource().getMessage("file.extension.photo",
                                null, getLocale()));
                    }
                    break;
                case DOCUMENT:
                    if (!isPhoto && pdf != extension) {
                        errors.put("file.extension", getMessageSource().getMessage("file.extension.document",
                                null, getLocale()));
                    }
                    break;
                case PORTFOLIO:
                    break;
            }
        }

        // Name must present
        if (isEmpty(file.getName())) {
            errors.put("file.name", getMessageSource().getMessage("file.name.empty",
                    null, getLocale()));
        }

        // Size must present
        Long size = file.getSize();
        if (size == null || size == 0) {
            errors.put("file.size", getMessageSource().getMessage("file.size.empty",
                    null, getLocale()));
        }

        // Extension must present
        if (extension == null) {
            errors.put("file.extension", getMessageSource().getMessage("file.extension.empty",
                    null, getLocale()));
        }

        return errors;
    }

    @Override
    public Map<String, Object> validateUpdates(FileDetailed entity) {
        throw new UnsupportedOperationException();
    }
}
