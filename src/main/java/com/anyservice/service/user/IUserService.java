package com.anyservice.service.user;

import com.anyservice.dto.api.APrimary;
import com.anyservice.dto.user.UserBrief;
import com.anyservice.dto.user.UserDetailed;
import com.anyservice.dto.user.UserForChangePassword;
import com.anyservice.service.api.ICRUDService;
import com.anyservice.web.security.exceptions.UserNotFoundException;
import com.anyservice.web.security.exceptions.WrongPasswordException;

import java.util.Date;
import java.util.UUID;

public interface IUserService extends ICRUDService<UserBrief, UserDetailed, UUID, Date> {

    /**
     * Change password operation
     * For all other updates - use {@link ICRUDService#update(APrimary, Object, Object)}
     *
     * @param userWithPassword {@link UserForChangePassword} that stores data for change password operation
     * @return user for that operation was performed
     */
    UserDetailed changePassword(UserForChangePassword userWithPassword);

    /**
     * Returns user if the userName and password are correct
     *
     * @param userName userName of a user
     * @param password password of a user
     * @return user
     * @throws UserNotFoundException  if user was not found by specified userName
     * @throws WrongPasswordException if password if verification of hash was unsuccessful
     */
    UserDetailed findUserForLogin(String userName, String password);

    /**
     * Finds user by its userName
     *
     * @param userName of a user
     * @return user found by userName
     */
    UserDetailed findByUserName(String userName);

    /**
     * User verification method
     *
     * @param uuid identifier of a user, that must be verified
     * @param code verification code
     * @return verified {@link UserDetailed}
     * @throws IllegalArgumentException if something goes wrong with validation of passed values
     */
    UserDetailed verifyUser(UUID uuid, UUID code);
}
