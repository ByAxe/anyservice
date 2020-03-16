package com.anyservice.web.security.exceptions;


import com.anyservice.web.security.exceptions.api.PasswordException;

public class PasswordExpirationException extends PasswordException {

    public PasswordExpirationException(String message) {
        super(message);
    }

    public PasswordExpirationException(String message, Throwable cause) {
        super(message, cause);
    }
}
