package com.anyservice.web.controller;

import com.anyservice.dto.UserDTO;
import com.anyservice.service.UserService;
import com.anyservice.web.controller.api.CRUDController;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
public class UserController implements CRUDController<UserDTO, UUID> {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDTO save(UserDTO dto) {
        return userService.save(dto);
    }

    @Override
    public Iterable<UserDTO> saveAll(Iterable<UserDTO> dtoIterable) {
        return userService.saveAll(dtoIterable);
    }

    @Override
    public Optional<UserDTO> findById(UUID uuid) {
        return userService.findById(uuid);
    }

    @Override
    public boolean existsById(UUID uuid) {
        return userService.existsById(uuid);
    }

    @Override
    public Iterable<UserDTO> findAll() {
        return userService.findAll();
    }

    @Override
    public Iterable<UserDTO> findAllById(Iterable<UUID> uuids) {
        return userService.findAllById(uuids);
    }

    @Override
    public long count() {
        return userService.count();
    }

    @Override
    public void deleteById(UUID uuid) {
        userService.deleteById(uuid);
    }

    @Override
    public void delete(UserDTO dto) {
        userService.delete(dto);
    }

    @Override
    public void deleteAll(Iterable<? extends UserDTO> dtoIterable) {
        userService.deleteAll(dtoIterable);
    }

    @Override
    public void deleteAll() {
        userService.deleteAll();
    }
}
