package com.anyservice.web.controller;

import com.anyservice.dto.user.UserBrief;
import com.anyservice.dto.user.UserDetailed;
import com.anyservice.service.UserService;
import com.anyservice.web.controller.api.CRUDController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController implements CRUDController<UserBrief, UserDetailed, UUID, Long> {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final MessageSource messageSource;

    public UserController(UserService userService, MessageSource messageSource) {
        this.userService = userService;
        this.messageSource = messageSource;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody UserDetailed dto) {
        UserDetailed saved;

        try {
            saved = userService.create(dto);
        } catch (Exception e) {
            logger.info(messageSource.getMessage("user.create",
                    null, LocaleContextHolder.getLocale()));
            throw e;
        }

        HttpHeaders httpHeaders = new HttpHeaders();

        UUID uuid = saved.getUuid();

        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(uuid).toUri());

        return new ResponseEntity<>(saved, httpHeaders, CREATED);
    }

    @PutMapping("/{uuid}/version/{version}")
    public ResponseEntity<?> update(@RequestBody UserDetailed dto,
                                    @PathVariable UUID uuid, @PathVariable Long version) {
        UserDetailed updatedUser;

        try {
            updatedUser = userService.update(dto, uuid, new Date(version));
        } catch (Exception e) {
            logger.info(messageSource.getMessage("user.update",
                    null, LocaleContextHolder.getLocale()));
            throw e;
        }

        HttpHeaders httpHeaders = new HttpHeaders();

        return new ResponseEntity<>(updatedUser, httpHeaders, OK);
    }

    @Override
    @GetMapping("/{uuid}")
    public ResponseEntity<?> findById(@PathVariable UUID uuid) {
        Optional<UserDetailed> userDTOOptional = userService.findById(uuid);

        if (userDTOOptional.isPresent()) {
            return new ResponseEntity<>(userDTOOptional.get(), OK);
        } else {
            return new ResponseEntity<>(null, NO_CONTENT);
        }
    }

    @Override
    @GetMapping("/exists/{uuid}")
    public ResponseEntity<Boolean> existsById(@PathVariable UUID uuid) {
        boolean exists = userService.existsById(uuid);

        return new ResponseEntity<>(exists, OK);
    }

    @Override
    @GetMapping
    public ResponseEntity<?> findAll() {
        Iterable<UserBrief> dtoIterable = userService.findAll();

        return new ResponseEntity<>(dtoIterable, OK);
    }

    @Override
    @GetMapping("uuid/list/{uuids}")
    public ResponseEntity<?> findAllById(@PathVariable List<UUID> uuids) {
        Iterable<UserBrief> dtoIterable = userService.findAllById(uuids);

        return new ResponseEntity<>(dtoIterable, OK);
    }

    @Override
    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        long count = userService.count();
        return new ResponseEntity<>(count, OK);
    }

    @Override
    @DeleteMapping("/{uuid}/version/{version}")
    public ResponseEntity<?> deleteById(@PathVariable UUID uuid, @PathVariable Long version) {
        userService.deleteById(uuid, new Date(version));

        return new ResponseEntity<>(null, NO_CONTENT);
    }
}
