package com.anyservice.service.validators.api.user;

import com.anyservice.entity.Initials;
import com.anyservice.service.validators.api.IValidator;

import java.util.Map;
import java.util.UUID;

public interface IUserValidator<USER> extends IValidator<USER> {

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
     * @param oldPassword         the old password of user
     * @param newPassword         the new password of user
     * @param passwordFromStorage the saved password from some storage
     * @return errors obtained during validation
     */
    Map<String, Object> validatePasswordForChange(String oldPassword, String newPassword,
                                                  String passwordFromStorage);

    /**
     * Validate initials for user
     *
     * @param initials of a user
     * @param errors   all the errors gathered in process of validation
     */
    void validateInitials(Initials initials, Map<String, Object> errors);
}
