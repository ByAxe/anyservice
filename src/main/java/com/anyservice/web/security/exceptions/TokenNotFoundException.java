package com.anyservice.web.security.exceptions;

import org.springframework.security.core.AuthenticationException;


public class TokenNotFoundException extends AuthenticationException {
    public TokenNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }

    public TokenNotFoundException(String msg) {
        super(msg);
    }
}
