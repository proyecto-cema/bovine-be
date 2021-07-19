package com.cema.bovine.controllers.handlers;

import com.cema.bovine.domain.ErrorResponse;
import com.cema.bovine.exceptions.BovineAlreadyExistsException;
import com.cema.bovine.exceptions.BovineNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CemaExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BovineNotFoundException.class)
    public final ResponseEntity<Object> handleBovineNotFoundException(BovineNotFoundException ex, WebRequest request) {

        ErrorResponse error = new ErrorResponse("Bovine Not Found", ex.getMessage());
        return new ResponseEntity(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BovineAlreadyExistsException.class)
    public final ResponseEntity<Object> handleBovineAlreadyExistsException(BovineAlreadyExistsException ex, WebRequest request) {

        ErrorResponse error = new ErrorResponse("Bovine Already Exists", ex.getMessage());
        return new ResponseEntity(error, HttpStatus.CONFLICT);
    }
}
