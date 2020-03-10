package com.anyservice.service;

import com.anyservice.dto.user.UserBrief;
import com.anyservice.dto.user.UserDetailed;
import com.anyservice.dto.user.UserDetailedNew;
import com.anyservice.entity.UserEntity;
import com.anyservice.repository.UserRepository;
import com.anyservice.service.api.CRUDService;
import com.anyservice.service.api.IPasswordService;
import com.anyservice.service.validators.UserValidator;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService implements CRUDService<UserBrief, UserDetailed, UUID> {

    private final UserRepository userRepository;
    private final ConversionService conversionService;
    private final UserValidator userValidator;
    private final IPasswordService passwordService;

    public UserService(UserRepository userRepository, ConversionService conversionService,
                       UserValidator userValidator, IPasswordService passwordService) {
        this.userRepository = userRepository;
        this.conversionService = conversionService;
        this.userValidator = userValidator;
        this.passwordService = passwordService;
    }

    @Override
    public UserDetailed create(UserDetailed dto) {
        throw new UnsupportedOperationException("Use other method instead");
    }

    public UserDetailed create(UserDetailedNew user) {
        // Validate user
        Map<String, Object> errors = userValidator.validateCreation(user);

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(errors.toString());
        }

        // Convert it to entity
        UserEntity entity = conversionService.convert(user, UserEntity.class);

        // Hash the password
        String hash = passwordService.hash(user.getPassword());
        Objects.requireNonNull(entity).setPassword(hash);

        // Save new user
        UserEntity savedEntity = userRepository.save(entity);

        // Return saved user back
        return conversionService.convert(savedEntity, UserDetailed.class);
    }

    @Override
    public UserDetailed update(UserDetailed dto, UUID uuid, Date version) {
        if (!existsById(uuid)) {
            throw new IllegalArgumentException("No entity was found with this uuid");
        }

        Optional<UserDetailed> id = findById(uuid);

        UserEntity entity = conversionService.convert(dto, UserEntity.class);

        UserEntity savedEntity = userRepository.save(entity);

        return conversionService.convert(savedEntity, UserDetailed.class);
    }

    @Override
    public Iterable<UserBrief> saveAll(Iterable<UserBrief> dtoIterable) {

        List<UserEntity> entityList = Stream.of(dtoIterable)
                .map(dto -> conversionService.convert(dto, UserEntity.class))
                .collect(Collectors.toList());

        Iterable<UserEntity> savedEntities = userRepository.saveAll(entityList);

        List<UserDetailed> savedDto = Stream.of(savedEntities)
                .map(entity -> conversionService.convert(entity, UserDetailed.class))
                .collect(Collectors.toList());

        return savedDto;
    }

    @Override
    public Optional<UserDetailed> findById(UUID uuid) {
        Optional<UserEntity> optionalUserEntity = userRepository.findById(uuid);

        Optional<UserDetailed> userDTOOptional = Optional.empty();

        if (optionalUserEntity.isPresent()) {
            UserEntity entity = optionalUserEntity.get();
            UserDetailed userDetailedDTO = conversionService.convert(entity, UserDetailed.class);
            userDTOOptional = Optional.of(userDetailedDTO);
        }

        return userDTOOptional;
    }

    @Override
    public boolean existsById(UUID uuid) {
        return userRepository.existsById(uuid);
    }

    @Override
    public Iterable<UserBrief> findAll() {
        Iterable<UserEntity> userEntities = userRepository.findAll();

        List<UserDetailed> savedDto = Stream.of(userEntities)
                .map(entity -> conversionService.convert(entity, UserDetailed.class))
                .collect(Collectors.toList());

        return savedDto;
    }

    @Override
    public Iterable<UserBrief> findAllById(Iterable<UUID> uuids) {
        Iterable<UserEntity> userEntities = userRepository.findAllById(uuids);

        List<UserDetailed> savedDto = Stream.of(userEntities)
                .map(entity -> conversionService.convert(entity, UserDetailed.class))
                .collect(Collectors.toList());

        return savedDto;
    }

    @Override
    public long count() {
        return userRepository.count();
    }

    @Override
    public void deleteById(UUID uuid) {
        userRepository.deleteById(uuid);
    }

    @Override
    public void delete(UserDetailed dto) {
        UserEntity entity = conversionService.convert(dto, UserEntity.class);

        userRepository.delete(entity);
    }

    @Override
    public void deleteAll(Iterable<? extends UserBrief> dtoIterable) {
        List<UserEntity> entityList = Stream.of(dtoIterable)
                .map(dto -> conversionService.convert(dto, UserEntity.class))
                .collect(Collectors.toList());

        userRepository.deleteAll(entityList);
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAll();
    }
}
