package com.cema.bovine.controllers.handlers;

import com.cema.bovine.domain.ErrorResponse;
import com.cema.bovine.exceptions.BovineAlreadyExistsException;
import com.cema.bovine.exceptions.BovineNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class CemaExceptionHandlerTest {

    @Test
    public void handleBovineNotFoundExceptionShouldReturnResponseEntityWithMessageAndStatusCode(){
        CemaExceptionHandler cemaExceptionHandler = new CemaExceptionHandler();

        BovineNotFoundException ex = new BovineNotFoundException("Bovine 123 Not Found");

        ResponseEntity<Object> result = cemaExceptionHandler.handleBovineNotFoundException(ex, null);
        ErrorResponse body = (ErrorResponse) result.getBody();
        HttpStatus status = result.getStatusCode();
        assertThat(body.getMessage(), is("Bovine Not Found"));
        assertThat(body.getDetails(), is("Bovine 123 Not Found"));
        assertThat(status, is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void handleBovineExistsExceptionShouldReturnResponseEntityWithMessageAndStatusCode(){
        CemaExceptionHandler cemaExceptionHandler = new CemaExceptionHandler();

        BovineAlreadyExistsException ex = new BovineAlreadyExistsException("Bovine 123 already exists");

        ResponseEntity<Object> result = cemaExceptionHandler.handleBovineAlreadyExistsException(ex, null);
        ErrorResponse body = (ErrorResponse) result.getBody();
        HttpStatus status = result.getStatusCode();
        assertThat(body.getMessage(), is("Bovine Already Exists"));
        assertThat(body.getDetails(), is("Bovine 123 already exists"));
        assertThat(status, is(HttpStatus.CONFLICT));
    }

}