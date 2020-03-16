package com.anyservice.web.security.exceptions;

import org.springframework.security.core.AuthenticationException;

public class RefreshTokenException extends AuthenticationException {
    public RefreshTokenException(String msg, Throwable t) {
        super(msg, t);
    }

    public RefreshTokenException(String msg) {
        super(msg);
    }
}
