package com.cema.bovine.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BovineAlreadyExistsException extends RuntimeException{

    public BovineAlreadyExistsException() {
    }

    public BovineAlreadyExistsException(String message) {
        super(message);
    }

    public BovineAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public BovineAlreadyExistsException(Throwable cause) {
        super(cause);
    }

    public BovineAlreadyExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }


}
