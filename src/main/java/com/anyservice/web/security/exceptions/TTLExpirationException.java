package com.anyservice.web.security.exceptions;


import com.anyservice.web.security.exceptions.api.TTLException;

public class TTLExpirationException extends TTLException {

    public TTLExpirationException(String message) {
        super(message);
    }

    public TTLExpirationException(String message, Throwable cause) {
        super(message, cause);
    }
}
