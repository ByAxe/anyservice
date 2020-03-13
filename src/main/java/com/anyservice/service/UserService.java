package com.anyservice.service;

import com.anyservice.dto.user.UserBrief;
import com.anyservice.dto.user.UserDetailed;
import com.anyservice.entity.UserEntity;
import com.anyservice.repository.UserRepository;
import com.anyservice.service.api.CRUDService;
import com.anyservice.service.api.IPasswordService;
import com.anyservice.service.validators.UserValidator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.anyservice.core.DateUtils.convertOffsetDateTimeToMills;

@Service
public class UserService implements CRUDService<UserBrief, UserDetailed, UUID> {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final ConversionService conversionService;
    private final UserValidator userValidator;
    private final IPasswordService passwordService;
    private final MessageSource messageSource;

    public UserService(UserRepository userRepository, ConversionService conversionService,
                       UserValidator userValidator, IPasswordService passwordService,
                       MessageSource messageSource) {
        this.userRepository = userRepository;
        this.conversionService = conversionService;
        this.userValidator = userValidator;
        this.passwordService = passwordService;
        this.messageSource = messageSource;
    }

    public UserDetailed create(UserDetailed user) {
        // Validate user
        Map<String, Object> errors = userValidator.validateCreation(user);

        if (!errors.isEmpty()) {
            logger.info(StringUtils.join(errors));
            throw new IllegalArgumentException(errors.toString());
        }

        // Convert it to entity
        UserEntity entity = conversionService.convert(user, UserEntity.class);

        // Hash the password
        String hash = passwordService.hash(user.getPassword());

        // if conversion was unsuccessful
        if (entity == null) {
            String message = messageSource.getMessage("user.create.validate.username",
                    null, LocaleContextHolder.getLocale());
            logger.error(message);
            throw new RuntimeException(message);
        }

        entity.setPassword(hash);

        // Save new user
        UserEntity savedEntity = userRepository.save(entity);

        // Return saved user back
        return conversionService.convert(savedEntity, UserDetailed.class);
    }

    @Override
    public UserDetailed update(UserDetailed user, UUID uuid, Date version) {
        if (!existsById(uuid)) {
            String message = messageSource.getMessage("user.not.exists",
                    null, LocaleContextHolder.getLocale());
            logger.info(message);
            throw new IllegalArgumentException(message);
        }

        // We know for sure such user exists
        UserDetailed versionOfUserFromDB = findByIdWithPassword(uuid).get();

        long lastUpdateDate = convertOffsetDateTimeToMills(versionOfUserFromDB.getDtUpdate());

        if (version.getTime() != lastUpdateDate) {
            String message = messageSource.getMessage("user.update.version",
                    null, LocaleContextHolder.getLocale());
            logger.info(message);
            throw new NullPointerException(message);
        }

        user.setUuid(versionOfUserFromDB.getUuid());
        user.setDtCreate(versionOfUserFromDB.getDtCreate());
        user.setDtUpdate(OffsetDateTime.now());

        userValidator.validateUpdates(user);

        UserEntity entity = conversionService.convert(user, UserEntity.class);

        String password = user.getPassword();
        if (password == null || password.isEmpty()) {
            // set new a password to the user
            String hash = passwordService.hash(password);

            // if conversion was unsuccessful
            if (entity == null) {
                String message = messageSource.getMessage("user.create.validate.username",
                        null, LocaleContextHolder.getLocale());
                logger.error(message);
                throw new RuntimeException(message);
            }

            entity.setPassword(hash);
        }

        UserEntity savedEntity = userRepository.save(entity);
        return conversionService.convert(savedEntity, UserDetailed.class);
    }

    @Override
    public Iterable<UserBrief> saveAll(Iterable<UserBrief> dtoIterable) {

        List<UserEntity> entityList = Stream.of(dtoIterable)
                .map(dto -> conversionService.convert(dto, UserEntity.class))
                .collect(Collectors.toList());

        Iterable<UserEntity> savedEntities = userRepository.saveAll(entityList);

        List<UserBrief> savedDto = Stream.of(savedEntities)
                .map(entity -> conversionService.convert(entity, UserBrief.class))
                .collect(Collectors.toList());

        return savedDto;
    }

    @Override
    public Optional<UserDetailed> findById(UUID uuid) {
        Optional<UserDetailed> userDetailedWithPassword = findByIdWithPassword(uuid);
        userDetailedWithPassword.ifPresent(u -> u.setPassword(null));
        return userDetailedWithPassword;
    }

    private Optional<UserDetailed> findByIdWithPassword(UUID id) {
        Optional<UserEntity> optionalUserEntity = userRepository.findById(id);

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

        List<UserBrief> savedDto = Stream.of(userEntities)
                .map(entity -> conversionService.convert(entity, UserBrief.class))
                .collect(Collectors.toList());

        return savedDto;
    }

    @Override
    public Iterable<UserBrief> findAllById(Iterable<UUID> uuids) {
        Iterable<UserEntity> userEntities = userRepository.findAllById(uuids);

        List<UserBrief> savedDto = Stream.of(userEntities)
                .map(entity -> conversionService.convert(entity, UserBrief.class))
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
