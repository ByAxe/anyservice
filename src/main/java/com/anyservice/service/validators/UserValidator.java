package com.anyservice.service.validators;

import com.anyservice.dto.user.UserDetailed;
import com.anyservice.entity.Initials;
import com.anyservice.entity.UserEntity;
import com.anyservice.repository.UserRepository;
import com.anyservice.service.validators.api.AValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

@Service
public class UserValidator extends AValidator<UserDetailed> {
    private final UserRepository userRepository;
    private final MessageSource messageSource;

    @Value("${password.length.min}")
    private int passwordMinLength;

    @Value("${password.length.max}")
    private int passwordMaxLength;

    public UserValidator(UserRepository userRepository, MessageSource messageSource) {
        this.userRepository = userRepository;
        this.messageSource = messageSource;
    }

    @Override
    public MessageSource getMessageSource() {
        return messageSource;
    }

    public Map<String, Object> validateCreation(UserDetailed user) {
        Map<String, Object> errors = new HashMap<>();

        // UserName validation
        validateUserName(user.getUserName(), user.getUuid(), errors);

        // Password validation
        validatePassword(user.getPassword(), errors);

        // Validate firstName, lastName etc.
        validateInitials(user.getInitials(), errors);

        return errors;
    }

    public Map<String, Object> validateUpdates(UserDetailed user) {
        Map<String, Object> errors = new HashMap<>();

        String password = user.getPassword();

        // If user is not updating his password - it will be null, so we do not need to validate it
        // Otherwise, - validation is necessary
        if (password != null) {
            validatePassword(password, errors);
        }

        // UserName validation
        validateUserName(user.getUserName(), user.getUuid(), errors);

        return errors;
    }

    /**
     * Validate userName according to rules
     *
     * @param userName userName of a user
     * @param userUuid user identifier
     * @param errors   list of errors during validation
     */
    public void validateUserName(String userName, UUID userUuid, Map<String, Object> errors) {
        // Check userName validity
        if (userName == null || userName.isEmpty()) {
            errors.put("username", getMessageSource().getMessage("user.username.empty",
                    null, getLocale()));
        } else {

            // Make sure user with such userName does not already exist
            UserEntity userFoundByUserName = userRepository.findFirstByUserName(userName);

            if (userFoundByUserName != null) {

                // Make sure it's not the same user, with the same userName
                if (!userFoundByUserName.getUuid().equals(userUuid)) {

                    // Otherwise, - claim the mistake
                    errors.put("username", getMessageSource().getMessage("user.username.exists",
                            null, getLocale()));
                }
            }

            // Check if userName contains only allowed characters
            for (char ch : userName.toCharArray()) {
                if (!Character.isLetter(ch) && !Character.isDigit(ch)) {
                    errors.put("username.content", getMessageSource().getMessage("user.useraname.content",
                            null, getLocale()));
                }
            }
        }
    }

    /**
     * Validate the password and put errors to passed errors object-storage
     *
     * @param password that must be validated
     * @param errors   obtained during validation
     */
    public void validatePassword(String password, Map<String, Object> errors) {
        if (password == null) {
            errors.put("password", getMessageSource().getMessage("user.password.empty",
                    null, getLocale()));
        } else {

            // Password length validation
            if (password.length() < passwordMinLength) {
                errors.put("password.length", getMessageSource().getMessage("user.password.short",
                        new Object[]{passwordMinLength}, getLocale()));
            } else if (password.length() > passwordMaxLength) {
                errors.put("password.length", getMessageSource().getMessage("user.password.long",
                        new Object[]{passwordMaxLength}, getLocale()));
            }

            // Password content validation
            for (char ch : password.toCharArray()) {
                if (!Character.isLetter(ch) && !Character.isDigit(ch)) {
                    errors.put("password.content", getMessageSource().getMessage("user.password.content",
                            null, getLocale()));
                }
            }
        }
    }

    /**
     * Validate initials for user
     *
     * @param initials of a user
     * @param errors   all the errors gathered in process of validation
     */
    public void validateInitials(Initials initials, Map<String, Object> errors) {
        if (initials != null) {
            String firstName = initials.getFirstName();

            if (firstName == null || firstName.isEmpty()) {
                errors.put("initials.firstName", getMessageSource().getMessage("user.initials.firstname.empty",
                        null, getLocale()));
            } else {
                validateLettersOnlyField(firstName, "firstName", errors);
            }

            String lastName = initials.getLastName();
            String middleName = initials.getMiddleName();

            // TODO in the returned messages always will be these "fieldNames" despite the locale. Must be fixed.
            if (lastName != null) {
                validateLettersOnlyField(initials.getLastName(), "lastName", errors);
            }

            if (middleName != null) {
                validateLettersOnlyField(initials.getMiddleName(), "middleName", errors);
            }
        } else {
            errors.put("initials", getMessageSource().getMessage("user.initials.not.exist",
                    null, getLocale()));
        }
    }

}
