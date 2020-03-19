package com.anyservice.service.validators.api.user;

import com.anyservice.dto.user.UserDetailed;
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
     * @param errors   list of errors during validation
     */
    void validateUserName(String userName, UUID userUuid, Map<String, Object> errors);

    /**
     * Validate the password and put errors to passed errors object-storage
     *
     * @param password that must be validated
     * @param errors   obtained during validation
     */
    void validatePassword(String password, Map<String, Object> errors);

    /**
     * Validation of password for change
     *
     * @param oldPassword        the old password of user
     * @param newPassword        the new password of user
     * @param passwordHashFromDB the saved hash of a password from some storage
     * @return errors obtained during validation
     */
    Map<String, Object> validatePasswordForChange(String oldPassword, String newPassword,
                                                  String passwordHashFromDB);

    /**
     * Validate initials for user
     *
     * @param initials of a user
     * @param errors   all the errors gathered in process of validation
     */
    void validateInitials(Initials initials, Map<String, Object> errors);

    /**
     * Ensure that given field contains only letters
     *
     * @param field     field content REQUIRED
     * @param fieldName field name REQUIRED
     * @param errors    errors during validation
     */
    void validateLettersOnlyField(String field, String fieldName, Map<String, Object> errors);
}
