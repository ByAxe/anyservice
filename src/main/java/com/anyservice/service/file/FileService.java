package com.anyservice.service.file;

import com.anyservice.core.enums.FileType;
import com.anyservice.dto.file.FileBrief;
import com.anyservice.dto.file.FileDetailed;
import com.anyservice.entity.file.FileEntity;
import com.anyservice.repository.FileRepository;
import com.anyservice.service.api.IFileService;
import com.anyservice.service.validators.api.IFileValidator;
import com.jlefebure.spring.boot.minio.MinioException;
import com.jlefebure.spring.boot.minio.MinioService;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.*;

import static com.anyservice.core.DateUtils.convertOffsetDateTimeToMills;

@Service
@Transactional(readOnly = true)
@Log4j2
public class FileService implements IFileService {

    private final FileRepository fileRepository;
    private final MinioService minioService;
    private final MessageSource messageSource;
    private final ConversionService conversionService;
    private final Environment environment;
    private final IFileValidator fileValidator;

    public FileService(FileRepository fileRepository, MinioService minioService,
                       MessageSource messageSource, ConversionService conversionService,
                       Environment environment, IFileValidator fileValidator) {
        this.fileRepository = fileRepository;
        this.minioService = minioService;
        this.messageSource = messageSource;
        this.conversionService = conversionService;
        this.environment = environment;
        this.fileValidator = fileValidator;
    }

    @Override
    @Transactional
    public FileDetailed create(FileDetailed file) {

        // Generate an identifier
        UUID uuid = UUID.randomUUID();

        file.setUuid(uuid);
        file.setDtCreate(OffsetDateTime.now());

        // Validate file
        Map<String, Object> errors = fileValidator.validateCreation(file);

        if (!errors.isEmpty()) {
            log.info(StringUtils.join(errors));
            throw new IllegalArgumentException(errors.toString());
        }

        // Build path for file
        Path path = getPathToFile(file.getFileType(), uuid);

        // Upload file on minio
        try {
            minioService.upload(path, file.getInputStream(), file.getExtension().getContentType());
        } catch (MinioException e) {
            String message = messageSource.getMessage("file.minio.cannot.upload",
                    null, LocaleContextHolder.getLocale());
            log.error(message, e);
            throw new IllegalStateException(message, e);
        }

        // Save entity to database
        FileEntity entity = conversionService.convert(file, FileEntity.class);
        fileRepository.saveAndFlush(entity);

        return conversionService.convert(entity, FileDetailed.class);
    }

    /**
     * Build a valid path for file
     *
     * @param fileType type of a file
     * @param uuid     file identifier
     * @return path {@link Path} to file
     */
    public Path getPathToFile(FileType fileType, UUID uuid) {
        String directory = getDirectoryToFileType(fileType);

        // Full path to file
        String pathAsString = directory + "/" + uuid;

        // Convert it to special object
        return Paths.get(pathAsString);
    }

    /**
     * Get a directory for the specified file type
     *
     * @param fileType
     * @return path to directory
     */
    public String getDirectoryToFileType(FileType fileType) {
        // Find the property name for this file type
        StringBuilder propertyPath = new StringBuilder("spring.minio.folder.");

        switch (fileType) {
            case PROFILE_PHOTO:
                propertyPath.append("user.photo");
                break;
            case DOCUMENT:
                propertyPath.append("user.documents");
                break;
            case PORTFOLIO:
                propertyPath.append("user.portfolio");
                break;
        }

        // Get the directory from property
        String directory = environment.getProperty(propertyPath.toString());

        return directory;
    }

    @Override
    public FileDetailed update(FileDetailed dto, UUID uuid, Date date) {
        throw new UnsupportedOperationException();
    }

    @Override
    @SneakyThrows
    public Optional<FileDetailed> findById(UUID uuid) {

        // Find file description in repository
        Optional<FileEntity> entityOptional = fileRepository.findById(uuid);

        // Check if it's present
        if (!entityOptional.isPresent()) return Optional.empty();

        // Convert file get extract from Optional
        FileDetailed fileDetailed = entityOptional.map(e -> conversionService.convert(e, FileDetailed.class)).get();

        // Get path for the file
        Path path = getPathToFile(fileDetailed.getFileType(), fileDetailed.getUuid());

        // Get an actual file from storage
        InputStream inputStream = minioService.get(path);

        // Put it into object and return
        fileDetailed.setInputStream(inputStream);

        return Optional.of(fileDetailed);
    }

    @Override
    public boolean existsById(UUID uuid) {
        return fileRepository.existsById(uuid);
    }

    @Override
    public Iterable<FileBrief> findAll() {
        Iterable<FileEntity> userEntities = fileRepository.findAll();

        List<FileBrief> savedDto = new ArrayList<>();

        for (FileEntity e : userEntities) {
            FileBrief convert = conversionService.convert(e, FileBrief.class);
            savedDto.add(convert);
        }

        return savedDto;
    }

    @Override
    public Iterable<FileBrief> findAllById(Iterable<UUID> uuids) {
        Iterable<FileEntity> userEntities = fileRepository.findAllById(uuids);

        List<FileBrief> savedDto = new ArrayList<>();

        for (FileEntity e : userEntities) {
            FileBrief convert = conversionService.convert(e, FileBrief.class);
            savedDto.add(convert);
        }

        return savedDto;
    }

    @Override
    public long count() {
        return fileRepository.count();
    }

    @Override
    @Transactional
    @SneakyThrows
    public void deleteById(UUID uuid, Date version) {
        // Check if such file exists
        if (!existsById(uuid)) {
            String message = messageSource.getMessage("file.not.exists",
                    null, LocaleContextHolder.getLocale());
            log.info(message);
            throw new IllegalArgumentException(message);
        }

        // We know for sure such file exists
        FileDetailed versionOfUserFromDB = fileRepository.findById(uuid)
                .map(e -> conversionService.convert(e, FileDetailed.class))
                .get();

        long createDate = convertOffsetDateTimeToMills(versionOfUserFromDB.getDtCreate());

        // Compare the versions of entities
        if (version.getTime() != createDate) {
            String message = messageSource.getMessage("delete.version",
                    null, LocaleContextHolder.getLocale());
            log.info(message);
            throw new IllegalArgumentException(message);
        }

        // Get path for the file
        Path path = getPathToFile(versionOfUserFromDB.getFileType(), versionOfUserFromDB.getUuid());

        // Delete from storage
        minioService.remove(path);

        // Delete file
        fileRepository.deleteById(uuid);
    }

}
