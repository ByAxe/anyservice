package com.anyservice.service.validators.api.user;

import com.anyservice.dto.user.UserDetailed;
import com.anyservice.entity.user.Contacts;
import com.anyservice.entity.user.Initials;
import com.anyservice.service.validators.api.IValidator;

import java.util.Map;
import java.util.UUID;

public interface IUserValidator extends IValidator<UserDetailed> {

    /**
     * Validate userName according to rules
     *
     * @param userName userName of a user
     * @param userUuid user identifier
     * @return errors   storage for errors obtained during validation
     */
    Map<String, Object> validateUserName(String userName, UUID userUuid);

    /**
     * Validate the password and put errors to passed errors object-storage
     *
     * @param password that must be validated
     * @return errors   storage for errors obtained during validation
     */
    Map<String, Object> validatePassword(String password);

    /**
     * Validation of password for change
     *
     * @param oldPassword        the old password of user
     * @param newPassword        the new password of user
     * @param passwordHashFromDB the saved hash of a password from some storage
     * @return storage for errors obtained during validation
     */
    Map<String, Object> validatePasswordForChange(String oldPassword, String newPassword,
                                                  String passwordHashFromDB);

    /**
     * Validate initials for user
     *
     * @param initials of a user
     * @return errors   all the errors gathered in process of validation
     */
    Map<String, Object> validateInitials(Initials initials);

    /**
     * Ensure that given field contains only letters
     *
     * @param field     field content REQUIRED
     * @param fieldName field name REQUIRED
     * @return errors    errors during validation
     */
    Map<String, Object> validateLettersOnlyField(String field, String fieldName);

    /**
     * Validate presence and content of email address
     *
     * @param contacts objects, containing email and other contact fields
     * @return storage for errors obtained during validation
     */
    Map<String, Object> validateEmail(Contacts contacts);

    /**
     * Validate the content of an email address
     *
     * @param email address
     * @return storage for errors obtained during validation
     */
    Map<String, Object> validateEmailContent(String email);

    /**
     * Validation of a verified user
     *
     * @param verifiedUser user that will be validated
     * @return storage for errors obtained during validation
     */
    Map<String, Object> verifiedValidation(UserDetailed verifiedUser);

    /**
     * Validation of a non verified user
     *
     * @param nonVerifiedUser user that will be validated
     * @return storage for errors obtained during validation
     */
    Map<String, Object> nonVerifiedValidation(UserDetailed nonVerifiedUser);

}
