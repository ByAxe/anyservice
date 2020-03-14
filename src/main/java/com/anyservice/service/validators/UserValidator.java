package com.anyservice.service.validators;

import com.anyservice.dto.user.UserDetailed;
import com.anyservice.entity.Initials;
import com.anyservice.entity.UserEntity;
import com.anyservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

@Service
public class UserValidator {

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

    public Map<String, Object> validateCreation(UserDetailed user) {
        Map<String, Object> errors = new HashMap<>();

        String userName = user.getUserName();

        // Check userName validity
        if (userName == null || userName.isEmpty()) {
            errors.put("userName", messageSource.getMessage("user.create.username.empty",
                    null, getLocale()));
        } else {

            // Make sure user with such userName does not already exist
            UserEntity userFoundByUserName = userRepository.findFirstByUserName(userName);

            if (userFoundByUserName != null) {
                errors.put("userName", messageSource.getMessage("user.create.username.exists",
                        null, getLocale()));
            }
        }

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

        return errors;
    }

    /**
     * Validate password and put errors to passed errors object-storage
     *
     * @param password
     * @param errors
     */
    private void validatePassword(String password, Map<String, Object> errors) {
        if (password == null) {
            errors.put("password", messageSource.getMessage("user.password.empty",
                    null, getLocale()));
        } else {

            // Password length validation
            if (password.length() < passwordMinLength) {
                errors.put("password.length", messageSource.getMessage("user.password.short",
                        new Object[]{passwordMinLength}, getLocale()));
            } else if (password.length() > passwordMaxLength) {
                errors.put("password.length", messageSource.getMessage("user.password.long",
                        new Object[]{passwordMaxLength}, getLocale()));
            }

            // Password content validation
            for (char ch : password.toCharArray()) {
                if (!Character.isLetter(ch) && !Character.isDigit(ch)) {
                    errors.put("password.content", messageSource.getMessage("user.password.content",
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
    private void validateInitials(Initials initials, Map<String, Object> errors) {
        if (initials != null) {
            String firstName = initials.getFirstName();

            if (firstName == null || firstName.isEmpty()) {
                errors.put("initials.firstName", messageSource.getMessage("user.initials.firstname.empty",
                        null, getLocale()));
            } else {
                validateLettersOnlyField(firstName, "firstName", errors);
            }

            // TODO in the returned messages always will be these "fieldNames" despite the locale. Must be fixed.
            validateLettersOnlyField(initials.getLastName(), "lastName", errors);
            validateLettersOnlyField(initials.getMiddleName(), "middleName", errors);

        } else {
            errors.put("initials", messageSource.getMessage("user.initials.not.exist",
                    null, getLocale()));
        }
    }

    /**
     * Ensure that given field contains only letters
     *
     * @param field     field content
     * @param fieldName field name
     * @param errors    errors during validation
     */
    private void validateLettersOnlyField(String field, String fieldName, Map<String, Object> errors) {
        // Do not need to validate this field if it's null
        // Because necessity of it must be ensured somewhere before calling this method
        if (field == null) return;

        for (char ch : field.toCharArray()) {
            if (!Character.isLetter(ch)) {
                errors.put(fieldName, messageSource.getMessage("user.letter.only.field",
                        new Object[]{fieldName}, getLocale()));
                return;
            }
        }
    }
}
