package com.anyservice.web.controller;

import com.anyservice.core.enums.FileExtension;
import com.anyservice.core.enums.FileType;
import com.anyservice.dto.file.FileBrief;
import com.anyservice.dto.file.FileDetailed;
import com.anyservice.service.api.IFileService;
import lombok.Cleanup;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@Log4j2
@RestController
@RequestMapping("/api/v1/file")
public class FileController {
    private final MessageSource messageSource;
    private final IFileService fileService;

    public FileController(MessageSource messageSource, IFileService fileService) {
        this.messageSource = messageSource;
        this.fileService = fileService;
    }

    @SneakyThrows
    @PostMapping("/upload/{type}")
    public ResponseEntity<?> create(@PathVariable FileType type,
                                    @NonNull @RequestParam("file") MultipartFile file) {

        // Build file object
        FileDetailed detailed = FileDetailed.builder()
                .extension(FileExtension.findExtension(file.getContentType()))
                .name(file.getOriginalFilename())
                .size(file.getSize())
                .inputStream(file.getInputStream())
                .fileType(type)
                .build();

        // Save it
        FileDetailed saved = fileService.create(detailed);

        HttpHeaders httpHeaders = new HttpHeaders();

        UUID uuid = saved.getUuid();

        // Put identifier into headers
        httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(uuid)
                .toUri());

        return new ResponseEntity<>(saved, httpHeaders, CREATED);
    }

    @GetMapping("/{uuid}")
    @SneakyThrows
    public void findById(@PathVariable UUID uuid, HttpServletResponse response) {
        // File file by identifier
        Optional<FileDetailed> fileDetailedOptional = fileService.findById(uuid);

        // Return nothing if file not found
        if (!fileDetailedOptional.isPresent()) return;

        // If file present - extract it from Optional
        FileDetailed fileDetailed = fileDetailedOptional.get();

        // Take file
        @Cleanup InputStream inputStream = fileDetailed.getInputStream();

        // Encode fileName
        String fileName = URLEncoder.encode(fileDetailed.getName(), "UTF-8");

        // Replace forbidden characters
        String fileNameCleanedUp = fileName.replaceAll("\\+", "_");

        // Create contentDisposition header
        String contentDisposition = String.format("attachment; filename=\"%s\"", fileNameCleanedUp);

        // Fill response with needed meta information
        response.setHeader("Content-Disposition", contentDisposition);
        response.setContentType(fileDetailed.getExtension().getContentType());

        // Copy file to response
        IOUtils.copy(inputStream, response.getOutputStream());

        // Send response to the client
        response.flushBuffer();
    }

    @GetMapping("/exists/{uuid}")
    public ResponseEntity<Boolean> existsById(@PathVariable UUID uuid) {
        boolean exists = fileService.existsById(uuid);

        return new ResponseEntity<>(exists, OK);
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        Iterable<FileBrief> dtoIterable = fileService.findAll();

        return new ResponseEntity<>(dtoIterable, OK);
    }

    @GetMapping("uuid/list/{uuids}")
    public ResponseEntity<?> findAllById(@PathVariable List<UUID> uuids) {
        Iterable<FileBrief> dtoIterable = fileService.findAllById(uuids);

        return new ResponseEntity<>(dtoIterable, OK);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        long count = fileService.count();
        return new ResponseEntity<>(count, OK);
    }

    @DeleteMapping("/{uuid}/version/{version}")
    public ResponseEntity<?> deleteById(@PathVariable UUID uuid, @PathVariable Long version) {
        fileService.deleteById(uuid, new Date(version));

        return new ResponseEntity<>(null, NO_CONTENT);
    }
}
