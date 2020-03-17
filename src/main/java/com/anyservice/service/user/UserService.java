package com.anyservice.service.user;

import com.anyservice.core.enums.UserRole;
import com.anyservice.core.enums.UserState;
import com.anyservice.dto.user.UserBrief;
import com.anyservice.dto.user.UserDetailed;
import com.anyservice.dto.user.UserForChangePassword;
import com.anyservice.entity.user.UserEntity;
import com.anyservice.repository.UserRepository;
import com.anyservice.service.api.ICRUDService;
import com.anyservice.service.api.IPasswordService;
import com.anyservice.service.validators.api.user.IUserValidator;
import com.anyservice.web.security.exceptions.UserNotFoundException;
import com.anyservice.web.security.exceptions.WrongPasswordException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;

import static com.anyservice.core.DateUtils.convertOffsetDateTimeToMills;
import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

@Service
@Transactional(readOnly = true)
public class UserService implements ICRUDService<UserBrief, UserDetailed, UUID, Date> {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final ConversionService conversionService;
    private final IUserValidator<UserDetailed> userValidator;
    private final IPasswordService passwordService;
    private final MessageSource messageSource;

    public UserService(UserRepository userRepository, ConversionService conversionService,
                       IUserValidator<UserDetailed> userValidator, IPasswordService passwordService,
                       MessageSource messageSource) {
        this.userRepository = userRepository;
        this.conversionService = conversionService;
        this.userValidator = userValidator;
        this.passwordService = passwordService;
        this.messageSource = messageSource;
    }

    @Override
    @Transactional
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

        // If conversion was unsuccessful
        if (entity == null) {
            String message = messageSource.getMessage("user.conversion.exception",
                    null, LocaleContextHolder.getLocale());
            logger.error(message);
            throw new RuntimeException(message);
        }

        entity.setPassword(hash);

        // Set system fields
        setRequiredFieldsToEntity(entity);

        // Save new user
        UserEntity savedEntity = userRepository.save(entity);

        // Return saved user back
        return conversionService.convert(savedEntity, UserDetailed.class);
    }

    private void setRequiredFieldsToEntity(UserEntity entity) {
        UUID uuid = UUID.randomUUID();
        entity.setUuid(uuid);

        OffsetDateTime now = OffsetDateTime.now();

        entity.setDtCreate(now);
        entity.setDtUpdate(now);

        entity.setPasswordUpdateDate(now);

        entity.setRole(UserRole.ROLE_USER.name());
        entity.setState(UserState.ACTIVE.name());
    }

    @Override
    @Transactional
    public UserDetailed update(UserDetailed user, UUID uuid, Date version) {
        // Check if such user exists
        if (!existsById(uuid)) {
            String message = messageSource.getMessage("user.not.exists",
                    null, LocaleContextHolder.getLocale());
            logger.info(message);
            throw new IllegalArgumentException(message);
        }

        // We know for sure such user exists
        UserDetailed versionOfUserFromDB = findByIdWithPassword(uuid).get();

        long lastUpdateDate = convertOffsetDateTimeToMills(versionOfUserFromDB.getDtUpdate());

        // Compare the versions ofNullable() entities
        if (version.getTime() != lastUpdateDate) {
            String message = messageSource.getMessage("user.update.version",
                    null, LocaleContextHolder.getLocale());
            logger.info(message);
            throw new NullPointerException(message);
        }

        // Set all the system fields
        user.setUuid(versionOfUserFromDB.getUuid());
        user.setDtCreate(versionOfUserFromDB.getDtCreate());
        user.setDtUpdate(OffsetDateTime.now());

        // Validate user
        Map<String, Object> errors = userValidator.validateUpdates(user);

        if (!errors.isEmpty()) {
            logger.info(StringUtils.join(errors));
            throw new IllegalArgumentException(errors.toString());
        }

        // If everything is ok - convert it to DB entity
        UserEntity entity = conversionService.convert(user, UserEntity.class);

        // If conversion was unsuccessful
        if (entity == null) {
            String message = messageSource.getMessage("user.conversion.exception",
                    null, LocaleContextHolder.getLocale());
            logger.error(message);
            throw new RuntimeException(message);
        }

        // Save updated user to DB
        UserEntity savedEntity = userRepository.save(entity);
        return conversionService.convert(savedEntity, UserDetailed.class);
    }

    @Transactional
    public UserDetailed changePassword(UserForChangePassword userWithPassword) {

        // Check if uuid is present
        UUID uuid = userWithPassword.getUuid();
        if (uuid == null) {
            String message = messageSource.getMessage("uuid.empty",
                    null, LocaleContextHolder.getLocale());
            logger.info(message);
            throw new IllegalArgumentException(message);
        }

        UserDetailed versionOfUserFromDB = findByIdWithPassword(uuid).get();

        // Check if such user exists
        if (!existsById(uuid)) {
            String message = messageSource.getMessage("user.not.exists",
                    null, LocaleContextHolder.getLocale());
            logger.info(message);
            throw new IllegalArgumentException(message);
        }

        // Validate the password for change
        Map<String, Object> errors = userValidator.validatePasswordForChange(userWithPassword.getNewPassword(),
                userWithPassword.getOldPassword(), versionOfUserFromDB.getPassword());

        // If any errors - show it to the user
        if (!errors.isEmpty()) {
            logger.info(StringUtils.join(errors));
            throw new IllegalArgumentException(errors.toString());
        }

        // At this moment, we know that everything is fine
        versionOfUserFromDB.setPassword(userWithPassword.getNewPassword());

        OffsetDateTime dtUpdate = versionOfUserFromDB.getDtUpdate();
        versionOfUserFromDB.setPasswordUpdateDate(dtUpdate);

        // If everything is ok - update the user
        Date version = new Date(convertOffsetDateTimeToMills(dtUpdate));
        return update(versionOfUserFromDB, uuid, version);
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
            userDTOOptional = Optional.ofNullable(userDetailedDTO);
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

        List<UserBrief> savedDto = new ArrayList<>();

        for (UserEntity e : userEntities) {
            UserBrief convert = conversionService.convert(e, UserBrief.class);
            savedDto.add(convert);
        }

        return savedDto;
    }

    @Override
    public Iterable<UserBrief> findAllById(Iterable<UUID> uuids) {
        Iterable<UserEntity> userEntities = userRepository.findAllById(uuids);

        List<UserBrief> savedDto = new ArrayList<>();

        for (UserEntity e : userEntities) {
            UserBrief convert = conversionService.convert(e, UserBrief.class);
            savedDto.add(convert);
        }

        return savedDto;
    }

    @Override
    public long count() {
        return userRepository.count();
    }

    @Override
    @Transactional
    public void deleteById(UUID uuid, Date version) {
        // Check if such user exists
        if (!existsById(uuid)) {
            String message = messageSource.getMessage("user.not.exists",
                    null, LocaleContextHolder.getLocale());
            logger.info(message);
            throw new IllegalArgumentException(message);
        }

        // We know for sure such user exists
        UserDetailed versionOfUserFromDB = findByIdWithPassword(uuid).get();

        long lastUpdateDate = convertOffsetDateTimeToMills(versionOfUserFromDB.getDtUpdate());

        // Compare the versions ofNullable() entities
        if (version.getTime() != lastUpdateDate) {
            String message = messageSource.getMessage("user.delete.version",
                    null, LocaleContextHolder.getLocale());
            logger.info(message);
            throw new IllegalArgumentException(message);
        }

        // Delete user
        userRepository.deleteById(uuid);
    }

    @Override
    @Transactional
    public void deleteAll() {
        userRepository.deleteAll();
    }


    /**
     * Returns user if the userName and password are correct
     *
     * @param userName userName of a user
     * @param password password of a user
     * @return user
     * @throws UserNotFoundException  if user was not found by specified userName
     * @throws WrongPasswordException if password if verification of hash was unsuccessful
     */
    public UserDetailed findUserForLogin(String userName, String password) {
        UserDetailed user = findByUserName(userName);

        if (user == null) {
            throw new UserNotFoundException(messageSource.getMessage("security.controller.login.user.not.found",
                    null, LocaleContextHolder.getLocale()));
        }

        // Check whether password is correct by verifying the hash
        boolean verificationSuccessful = passwordService.verifyHash(password, user.getPassword());

        if (!verificationSuccessful) {
            throw new WrongPasswordException(messageSource.getMessage("security.controller.login.password.wrong",
                    null, getLocale()));
        }

        return user;
    }

    /**
     * Finds user by its userName
     *
     * @param userName of a user
     * @return user found by userName
     */
    public UserDetailed findByUserName(String userName) {
        UserEntity user = userRepository.findFirstByUserName(userName);

        return conversionService.convert(user, UserDetailed.class);
    }
}
