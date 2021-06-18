package com.cema.bovine.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BovineNotFoundException extends RuntimeException{

    public BovineNotFoundException(String message) {
        super(message);
    }

    public BovineNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public BovineNotFoundException(Throwable cause) {
        super(cause);
    }

    public BovineNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
