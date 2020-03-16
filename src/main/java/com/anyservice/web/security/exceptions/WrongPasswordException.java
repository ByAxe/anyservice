package com.anyservice.web.security.exceptions;

import org.springframework.security.core.AuthenticationException;

public class WrongPasswordException extends AuthenticationException {
    public WrongPasswordException(String msg, Throwable t) {
        super(msg, t);
    }

    public WrongPasswordException(String msg) {
        super(msg);
    }
}
