package com.anyservice.web.security.exceptions;

import org.springframework.security.core.AuthenticationException;


public class ClaimsExtractionException extends AuthenticationException {
    public ClaimsExtractionException(String msg, Throwable t) {
        super(msg, t);
    }

    public ClaimsExtractionException(String msg) {
        super(msg);
    }
}
