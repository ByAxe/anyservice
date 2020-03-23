package com.anyservice.service.api;


import com.anyservice.dto.user.UserDetailed;

import java.util.UUID;

public interface ICustomMailSender {

    /**
     * Send special verification code to user
     *
     * @param user             that will receive verification email
     * @param verificationCode code to verify the account
     * @throws RuntimeException if any errors occur
     */
    void sendVerificationCode(UserDetailed user, UUID verificationCode);
}
