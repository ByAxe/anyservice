package com.anyservice.service.user;

import com.anyservice.core.enums.UserRole;
import com.anyservice.core.enums.UserState;
import com.anyservice.dto.user.UserBrief;
import com.anyservice.dto.user.UserDetailed;
import com.anyservice.dto.user.UserForChangePassword;
import com.anyservice.entity.user.UserEntity;
import com.anyservice.repository.UserRepository;
import com.anyservice.service.aop.markers.RemovePasswordFromReturningValue;
import com.anyservice.service.api.ICustomMailSender;
import com.anyservice.service.api.IPasswordService;
import com.anyservice.service.validators.api.user.IUserValidator;
import com.anyservice.web.security.exceptions.UserNotFoundException;
import com.anyservice.web.security.exceptions.WrongPasswordException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
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
@Log4j2
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final ConversionService conversionService;
    private final IUserValidator userValidator;
    private final IPasswordService passwordService;
    private final MessageSource messageSource;
    private final CacheManager cacheManager;
    private final ICustomMailSender mailSender;

    public UserService(UserRepository userRepository, ConversionService conversionService,
                       IUserValidator userValidator, IPasswordService passwordService,
                       MessageSource messageSource, CacheManager cacheManager,
                       ICustomMailSender mailSender) {
        this.userRepository = userRepository;
        this.conversionService = conversionService;
        this.userValidator = userValidator;
        this.passwordService = passwordService;
        this.messageSource = messageSource;
        this.cacheManager = cacheManager;
        this.mailSender = mailSender;
    }

    @Override
    @Transactional
    @RemovePasswordFromReturningValue
    public UserDetailed create(UserDetailed user) {
        // Validate user
        Map<String, Object> errors = userValidator.validateCreation(user);

        if (!errors.isEmpty()) {
            log.info(StringUtils.join(errors));
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
            log.error(message);
            throw new RuntimeException(message);
        }

        entity.setPassword(hash);

        // Set system fields
        setRequiredForCreationFieldsToEntity(entity);

        // Save new user
        UserEntity savedEntity = userRepository.saveAndFlush(entity);

        // Return saved user back
        UserDetailed savedUser = conversionService.convert(savedEntity, UserDetailed.class);

        // Generate verification code
        UUID verificationCode = UUID.randomUUID();

        // Send verification code to user's email
        mailSender.sendVerificationCode(savedUser, verificationCode);

        Cache verificationCodeMap = cacheManager.getCache("verificationCodeMap");

        // Save verification code to cache for further verification
        verificationCodeMap.put(savedUser.getUuid(), verificationCode);

        return savedUser;
    }

    /**
     * Set to {@link UserEntity} all system required fields for successful creation
     *
     * @param entity filled {@link UserEntity} for creation
     */
    private void setRequiredForCreationFieldsToEntity(UserEntity entity) {
        UUID uuid = UUID.randomUUID();
        entity.setUuid(uuid);

        OffsetDateTime now = OffsetDateTime.now();

        entity.setDtCreate(now);
        entity.setDtUpdate(now);

        entity.setPasswordUpdateDate(now);

        entity.setRole(UserRole.ROLE_USER.name());
        entity.setState(UserState.WAITING.name());

        entity.setIsVerified(false);
    }

    @Override
    @Transactional
    @RemovePasswordFromReturningValue
    public UserDetailed update(UserDetailed user, UUID uuid, Date version) {
        // Check if such user exists
        if (!existsById(uuid)) {
            String message = messageSource.getMessage("user.not.exists",
                    null, LocaleContextHolder.getLocale());
            log.info(message);
            throw new IllegalArgumentException(message);
        }

        // We know for sure such user exists
        UserDetailed versionOfUserFromDB = findByIdWithPassword(uuid).get();

        long lastUpdateDate = convertOffsetDateTimeToMills(versionOfUserFromDB.getDtUpdate());

        // Compare the versions of entities
        if (version.getTime() != lastUpdateDate) {
            String message = messageSource.getMessage("user.update.version",
                    null, LocaleContextHolder.getLocale());
            log.info(message);
            throw new NullPointerException(message);
        }

        // Set all the system fields
        user.setUuid(versionOfUserFromDB.getUuid());
        user.setDtCreate(versionOfUserFromDB.getDtCreate());
        user.setPasswordUpdateDate(versionOfUserFromDB.getPasswordUpdateDate());
        user.setRole(versionOfUserFromDB.getRole());
        user.setState(versionOfUserFromDB.getState());
        user.setDtUpdate(OffsetDateTime.now());

        // Validate user
        Map<String, Object> errors = userValidator.validateUpdates(user);

        if (!errors.isEmpty()) {
            log.info(StringUtils.join(errors));
            throw new IllegalArgumentException(errors.toString());
        }

        // Set password hash from the DB (change password operation not allowed in this method)
        user.setPassword(versionOfUserFromDB.getPassword());

        // If everything is ok - convert it to DB entity
        UserEntity entity = conversionService.convert(user, UserEntity.class);

        // If conversion was unsuccessful
        if (entity == null) {
            String message = messageSource.getMessage("user.conversion.exception",
                    null, LocaleContextHolder.getLocale());
            log.error(message);
            throw new RuntimeException(message);
        }

        // Save updated user to DB
        UserEntity savedEntity = userRepository.saveAndFlush(entity);
        return conversionService.convert(savedEntity, UserDetailed.class);
    }

    @Override
    @Transactional
    @RemovePasswordFromReturningValue
    public UserDetailed changePassword(UserForChangePassword userWithPassword) {

        // Check if uuid is present
        UUID uuid = userWithPassword.getUuid();
        if (uuid == null) {
            String message = messageSource.getMessage("uuid.empty",
                    null, LocaleContextHolder.getLocale());
            log.info(message);
            throw new IllegalArgumentException(message);
        }

        UserDetailed versionOfUserFromDB = findByIdWithPassword(uuid).get();

        // Check if such user exists
        if (!existsById(uuid)) {
            String message = messageSource.getMessage("user.not.exists",
                    null, LocaleContextHolder.getLocale());
            log.info(message);
            throw new IllegalArgumentException(message);
        }

        // Validate the password for change
        Map<String, Object> errors = userValidator.validatePasswordForChange(userWithPassword.getOldPassword(),
                userWithPassword.getNewPassword(), versionOfUserFromDB.getPassword());

        // If any errors - show it to the user
        if (!errors.isEmpty()) {
            log.info(StringUtils.join(errors));
            throw new IllegalArgumentException(errors.toString());
        }

        // Hash the password
        String hash = passwordService.hash(userWithPassword.getNewPassword());

        // Set password hash to user
        versionOfUserFromDB.setPassword(hash);

        // Set password update date
        OffsetDateTime dtUpdate = versionOfUserFromDB.getDtUpdate();
        versionOfUserFromDB.setPasswordUpdateDate(dtUpdate);

        // If everything is ok - convert it to DB entity
        UserEntity entity = conversionService.convert(versionOfUserFromDB, UserEntity.class);

        // If conversion was unsuccessful
        if (entity == null) {
            String message = messageSource.getMessage("user.conversion.exception",
                    null, LocaleContextHolder.getLocale());
            log.error(message);
            throw new RuntimeException(message);
        }

        // Save updated user to DB
        UserEntity savedEntity = userRepository.saveAndFlush(entity);

        return conversionService.convert(savedEntity, UserDetailed.class);
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
            log.info(message);
            throw new IllegalArgumentException(message);
        }

        // We know for sure such user exists
        UserDetailed versionOfUserFromDB = findByIdWithPassword(uuid).get();

        long lastUpdateDate = convertOffsetDateTimeToMills(versionOfUserFromDB.getDtUpdate());

        // Compare the versions ofNullable() entities
        if (version.getTime() != lastUpdateDate) {
            String message = messageSource.getMessage("user.delete.version",
                    null, LocaleContextHolder.getLocale());
            log.info(message);
            throw new IllegalArgumentException(message);
        }

        // Delete user
        userRepository.deleteById(uuid);
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    @RemovePasswordFromReturningValue
    public UserDetailed findUserForLogin(String userName, String password) {
        UserDetailed user = findByUserNameWithPassword(userName);

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

    @Override
    @RemovePasswordFromReturningValue
    public UserDetailed findByUserName(String userName) {
        UserEntity user = userRepository.findFirstByUserName(userName);

        return conversionService.convert(user, UserDetailed.class);
    }

    @Override
    @Transactional
    @RemovePasswordFromReturningValue
    public UserDetailed verifyUser(UUID uuid, UUID code) {

        // Check if such user exists
        if (!existsById(uuid)) {
            throw new IllegalArgumentException(messageSource.getMessage("user.not.exists",
                    null, LocaleContextHolder.getLocale()));
        }

        // Get user via uuid (we certainly know it exists)
        UserDetailed user = findByIdWithPassword(uuid).get();

        UserState state = user.getState();

        // Check if user is not blocked
        if (state == UserState.BLOCKED) {
            throw new IllegalArgumentException(messageSource.getMessage("security.user.blocked",
                    null, LocaleContextHolder.getLocale()));
        }

        if (state == UserState.ACTIVE) {
            throw new IllegalArgumentException(messageSource.getMessage("security.user.already.active",
                    null, LocaleContextHolder.getLocale()));
        }

        Cache verificationCodeMap = cacheManager.getCache("verificationCodeMap");
        // Get expected verification code from a cache
        UUID expectedVerificationCode = verificationCodeMap.get(uuid, UUID.class);

        // Check if verification code is correct
        if (!expectedVerificationCode.equals(code)) {
            throw new IllegalArgumentException(messageSource.getMessage("user.verification.code.wrong",
                    null, LocaleContextHolder.getLocale()));
        }

        // update user
        user.setState(UserState.ACTIVE);
        UserEntity entity = conversionService.convert(user, UserEntity.class);

        // Save updated user to DB
        UserEntity savedEntity = userRepository.saveAndFlush(entity);

        // return updated user
        UserDetailed updatedUser = conversionService.convert(savedEntity, UserDetailed.class);

        // remove verification code from cache
        verificationCodeMap.evictIfPresent(uuid);

        return updatedUser;
    }

    /**
     * Finds user by its userName (with a password)
     *
     * @param userName of a user
     * @return user found by userName
     */
    private UserDetailed findByUserNameWithPassword(String userName) {
        UserEntity user = userRepository.findFirstByUserName(userName);

        return conversionService.convert(user, UserDetailed.class);
    }
}
