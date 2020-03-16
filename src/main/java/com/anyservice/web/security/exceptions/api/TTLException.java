package com.anyservice.web.security.exceptions.api;

import org.springframework.security.core.AuthenticationException;

public class TTLException extends AuthenticationException {

    public TTLException(String message) {
        super(message);
    }

    public TTLException(String message, Throwable cause) {
        super(message, cause);
    }
}
