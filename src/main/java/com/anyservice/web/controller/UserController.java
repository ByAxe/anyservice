package com.anyservice.web.controller;

import com.anyservice.dto.user.UserDetailed;
import com.anyservice.dto.user.UserDetailedNew;
import com.anyservice.service.UserService;
import com.anyservice.web.controller.api.CRUDController;
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
public class UserController implements CRUDController<UserDetailed, UUID> {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody UserDetailedNew dto) {
        UserDetailed saved = userService.create(dto);

        HttpHeaders httpHeaders = new HttpHeaders();

        UUID uuid = saved.getUuid();

        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(uuid).toUri());

        return new ResponseEntity<>(saved, httpHeaders, CREATED);
    }

    @PutMapping("/uuid/{uuid}/version/{version}")
    public ResponseEntity<?> update(@RequestBody UserDetailed dto, @PathVariable UUID uuid, @PathVariable Date version) {
        // TODO implement
        return null;
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
        Iterable<UserDetailed> dtoIterable = userService.findAll();

        return new ResponseEntity<>(dtoIterable, OK);
    }

    @Override
    @GetMapping("/{uuids}")
    public ResponseEntity<?> findAllById(@PathVariable List<UUID> uuids) {
        Iterable<UserDetailed> dtoIterable = userService.findAllById(uuids);

        return new ResponseEntity<>(dtoIterable, OK);
    }

    @Override
    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        long count = userService.count();
        return new ResponseEntity<>(count, OK);
    }

    @Override
    @DeleteMapping("/{uuid}")
    public ResponseEntity<?> deleteById(@PathVariable UUID uuid) {
        userService.deleteById(uuid);

        return new ResponseEntity<>(null, NO_CONTENT);
    }

    @Override
    @DeleteMapping
    public ResponseEntity<?> delete(@RequestBody UserDetailed dto) {
        userService.delete(dto);

        return new ResponseEntity<>(null, NO_CONTENT);
    }
}
