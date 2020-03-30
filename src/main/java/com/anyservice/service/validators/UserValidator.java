package com.anyservice.service.validators;

import com.anyservice.dto.user.UserDetailed;
import com.anyservice.entity.user.Contacts;
import com.anyservice.entity.user.Initials;
import com.anyservice.entity.user.UserEntity;
import com.anyservice.repository.UserRepository;
import com.anyservice.service.user.PasswordService;
import com.anyservice.service.validators.api.IUserValidator;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

@Service
public class UserValidator implements IUserValidator {
    private final UserRepository userRepository;
    private final MessageSource messageSource;
    private final PasswordService passwordService;
    private final String ANY_COUNTRY = "Any";

    @Value("${user.validation.password.length.min}")
    private int passwordMinLength;

    @Value("${user.validation.password.length.max}")
    private int passwordMaxLength;

    @Value("${user.validation.email.allow.local}")
    private boolean allowLocal;

    public UserValidator(UserRepository userRepository, MessageSource messageSource,
                         PasswordService passwordService) {
        this.userRepository = userRepository;
        this.messageSource = messageSource;
        this.passwordService = passwordService;
    }

    @Override
    public MessageSource getMessageSource() {
        return messageSource;
    }

    @Override
    public Map<String, Object> validateCreation(UserDetailed user) {
        Map<String, Object> errors = new HashMap<>();

        // UserName validation
        errors.putAll(validateUserName(user.getUserName(), user.getUuid()));

        // Password validation
        errors.putAll(validatePassword(user.getPassword()));

        // Validate firstName, lastName etc.
        errors.putAll(validateInitials(user.getInitials()));

        // Validate email
        errors.putAll(validateEmail(user.getContacts()));

        return errors;
    }

    @Override
    public Map<String, Object> validateEmail(Contacts contacts) {
        Map<String, Object> errors = new HashMap<>();

        // Check presence of contacts-containing object
        if (contacts != null) {
            String email = contacts.getEmail();

            // Check presence of an email
            if (email != null) {

                // Check email address content and put all the errors to storage, if any
                errors.putAll(validateEmailContent(email));
            } else {
                errors.put("contacts.email", getMessageSource().getMessage("user.contacts.email.empty",
                        null, getLocale()));
            }
        } else {
            errors.put("contacts", getMessageSource().getMessage("user.contacts.empty",
                    null, getLocale()));
        }

        return errors;
    }

    @Override
    public Map<String, Object> validateEmailContent(String email) {
        Map<String, Object> errors = new HashMap<>();

        // Get email validation instance
        EmailValidator emailValidator = EmailValidator.getInstance(allowLocal);

        // Check email on validity
        boolean valid = emailValidator.isValid(email);

        // Put errors in storage if it's not valid
        if (!valid) {
            errors.put("contacts.email", getMessageSource().getMessage("user.contacts.email.nonvalid",
                    null, getLocale()));
        }

        return errors;
    }

    @Override
    public Map<String, Object> validateUpdates(UserDetailed user) {
        Map<String, Object> errors = new HashMap<>();

        String password = user.getPassword();

        // If user is not updating his password - it will be null, so we do not need to validate it
        // Otherwise, - validation is necessary
        if (password != null) {
            errors.putAll(validatePassword(password));
        }

        // UserName validation
        errors.putAll(validateUserName(user.getUserName(), user.getUuid()));

        // Check verification of a user to decide what to validate
        if (user.isVerified()) {
            errors.putAll(verifiedValidation(user));
        } else {
            errors.putAll(nonVerifiedValidation(user));
        }

        return errors;
    }

    @Override
    public Map<String, Object> verifiedValidation(UserDetailed verifiedUser) {
        Map<String, Object> errors = new HashMap<>();

        // If user legal status is verified but he does not mention the legal status, we must declare an error
        if (verifiedUser.isLegalStatusVerified() && verifiedUser.getLegalStatus() == null) {
            errors.put("verified.legalstatus", getMessageSource().getMessage("user.verified.legalstatus.verified.empty",
                    null, getLocale()));
        }

        return errors;
    }

    @Override
    public Map<String, Object> nonVerifiedValidation(UserDetailed nonVerifiedUser) {
        Map<String, Object> errors = new HashMap<>();

        if (nonVerifiedUser.isLegalStatusVerified()) {
            errors.put("nonverified.legalstatus.verified", getMessageSource().getMessage("user.nonverified.legalstatus.verified",
                    null, getLocale()));
        }

        if (nonVerifiedUser.getLegalStatus() != null) {
            errors.put("nonverified.legalstatus", getMessageSource().getMessage("user.nonverified.legalstatus",
                    null, getLocale()));
        }

        return errors;
    }

    @Override
    public Map<String, Object> validateUserName(String userName, UUID userUuid) {
        Map<String, Object> errors = new HashMap<>();

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

        return errors;
    }

    @Override
    public Map<String, Object> validatePasswordForChange(String oldPassword, String newPassword,
                                                         String passwordHashFromDB) {
        Map<String, Object> errors = new HashMap<>();

        // Check presence of new and old passwords
        if (newPassword != null) {
            if (oldPassword != null) {

                // If new password is not equal to old one
                if (!oldPassword.equals(newPassword)) {

                    // Check if the entered version of "oldPassword" is equal to the saved version
                    if (passwordService.verifyHash(oldPassword, passwordHashFromDB)) {

                        // Validate the content of password
                        errors.putAll(validatePassword(newPassword));

                    } else {
                        errors.put("password.old", getMessageSource().getMessage("user.password.old.wrong",
                                null, getLocale()));
                    }
                } else {
                    errors.put("password.old.new", getMessageSource().getMessage("user.password.old.equal.new",
                            null, getLocale()));
                }
            } else {
                errors.put("password.old", getMessageSource().getMessage("user.password.old.empty",
                        null, getLocale()));
            }
        } else {
            errors.put("password.new", getMessageSource().getMessage("user.password.new.empty",
                    null, getLocale()));
        }

        return errors;
    }

    @Override
    public Map<String, Object> validatePassword(String password) {
        Map<String, Object> errors = new HashMap<>();

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
        return errors;
    }

    @Override
    public Map<String, Object> validateInitials(Initials initials) {
        Map<String, Object> errors = new HashMap<>();

        if (initials != null) {
            String firstName = initials.getFirstName();

            if (firstName == null || firstName.isEmpty()) {
                errors.put("initials.firstName", getMessageSource().getMessage("user.initials.firstname.empty",
                        null, getLocale()));
            } else {
                errors.putAll(validateLettersOnlyField(firstName, "firstName"));
            }

            String lastName = initials.getLastName();
            String middleName = initials.getMiddleName();

            // TODO in the returned messages always will be these "fieldNames" despite the locale. Must be fixed.
            if (lastName != null) {
                errors.putAll(validateLettersOnlyField(initials.getLastName(), "lastName"));
            }

            if (middleName != null) {
                errors.putAll(validateLettersOnlyField(initials.getMiddleName(), "middleName"));
            }
        } else {
            errors.put("initials", getMessageSource().getMessage("user.initials.not.exist",
                    null, getLocale()));
        }

        return errors;
    }

    @Override
    public Map<String, Object> validateLettersOnlyField(String field, String fieldName) {
        Map<String, Object> errors = new HashMap<>();

        // Check whether filedName is present
        if (fieldName == null || fieldName.isEmpty()) {
            errors.put("fieldName", getMessageSource().getMessage("user.letter.only.field.fieldname",
                    new Object[]{"fieldName"}, getLocale()));
            return errors;
        }

        // Check whether field is present
        if (field == null || field.isEmpty()) {
            errors.put(fieldName, getMessageSource().getMessage("user.letter.only.field.empty",
                    new Object[]{fieldName}, getLocale()));
            return errors;
        }

        // Ensure that the field contains only letters
        for (char ch : field.toCharArray()) {
            if (!Character.isLetter(ch)) {
                errors.put(fieldName, getMessageSource().getMessage("user.letter.only.field",
                        new Object[]{fieldName}, getLocale()));
                return errors;
            }
        }

        return errors;
    }

    @Override
    public Map<String, Object> validateServiceCountries(UserDetailed user) {

        if (user.isLegalStatusVerified()) {

        }

        return null;
    }
}
