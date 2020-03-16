package com.anyservice.web.security.exceptions.api;

import org.springframework.security.core.AuthenticationException;

public class PasswordException extends AuthenticationException {

    public PasswordException(String message) {
        super(message);
    }

    public PasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
