package com.cema.bovine.controllers;

import com.cema.bovine.domain.Bovine;
import com.cema.bovine.entities.CemaBovine;
import com.cema.bovine.exceptions.BovineAlreadyExistsException;
import com.cema.bovine.exceptions.BovineNotFoundException;
import com.cema.bovine.mapping.BovineMapping;
import com.cema.bovine.repositories.BovineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class ControllerTest {

    @Mock
    private BovineRepository bovineRepository;
    @Mock
    private BovineMapping bovineMapping;

    @BeforeEach
    public void startUp() {
        openMocks(this);
    }

    @Test
    public void lookUpBovineByTagShouldAlwaysReturnBovineWhenExists() {
        CemaBovine cemaBovine = new CemaBovine();
        Bovine bovine = new Bovine();
        String tag = "123";
        when(bovineRepository.findCemaBovineByTag(tag)).thenReturn(cemaBovine);
        when(bovineMapping.mapEntityToDomain(cemaBovine)).thenReturn(bovine);
        Controller controller = new Controller(bovineRepository, bovineMapping);
        ResponseEntity<Bovine> result = controller.lookUpBovineByTag(tag);
        Bovine resultingUser = result.getBody();

        assertThat(resultingUser, is(bovine));
        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.OK));
    }

    @Test
    public void lookUpBovineByTagShouldAlwaysReturnNotFoundWhenBovineDoesntExists() {
        CemaBovine cemaBovine = new CemaBovine();
        Bovine bovine = new Bovine();
        String tag = "123";
        when(bovineRepository.findCemaBovineByTag(tag)).thenReturn(cemaBovine);
        when(bovineMapping.mapEntityToDomain(cemaBovine)).thenReturn(bovine);
        Controller controller = new Controller(bovineRepository, bovineMapping);

        Exception exception = assertThrows(BovineNotFoundException.class, () -> {
            controller.lookUpBovineByTag("234");
        });
        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, is("Bovine with tag 234 doesn't exits"));
    }

    @Test
    public void updateBovineShouldAlwaysReturnOKWhenBovineUpdatedCorrectly() {
        CemaBovine cemaBovine = new CemaBovine();
        Bovine bovine = new Bovine();

        when(bovineRepository.findCemaBovineByTag(bovine.getTag())).thenReturn(cemaBovine);
        when(bovineMapping.mapDomainToEntity(bovine, bovine.getTag())).thenReturn(cemaBovine);

        Controller controller = new Controller(bovineRepository, bovineMapping);
        ResponseEntity<Bovine> result = controller.updateBovine(bovine.getTag(), bovine);

        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.OK));
    }

    @Test
    public void updateBovineShouldAlwaysReturnNotFoundWhenBovineDoesntExists() {
        CemaBovine cemaBovine = new CemaBovine();
        Bovine bovine = new Bovine();
        String tag = "123";
        when(bovineRepository.findCemaBovineByTag(tag)).thenReturn(cemaBovine);

        Controller controller = new Controller(bovineRepository, bovineMapping);

        Exception exception = assertThrows(BovineNotFoundException.class, () -> {
            controller.updateBovine("234", bovine);
        });
        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, is("Bovine with tag 234 doesn't exits"));
    }

    @Test
    public void registerBovineShouldAlwaysReturnCreatedWhenBovineAddedCorrectly() {
        CemaBovine cemaBovine = new CemaBovine();
        Bovine bovine = new Bovine();

        when(bovineMapping.mapDomainToEntity(bovine)).thenReturn(cemaBovine);

        Controller controller = new Controller(bovineRepository, bovineMapping);
        ResponseEntity<Bovine> result = controller.registerBovine(bovine);

        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.CREATED));
    }

    @Test
    public void registerBovineShouldAlwaysReturnUnprocessableEntityWhenBovineExists() {
        CemaBovine cemaBovine = new CemaBovine();
        Bovine bovine = new Bovine();

        String tag = "123";
        cemaBovine.setTag(tag);
        bovine.setTag(tag);

        when(bovineRepository.findCemaBovineByTag(tag)).thenReturn(cemaBovine);
        when(bovineMapping.mapDomainToEntity(bovine)).thenReturn(cemaBovine);

        Controller controller = new Controller(bovineRepository, bovineMapping);
        Exception exception = assertThrows(BovineAlreadyExistsException.class, () -> {
            controller.registerBovine(bovine);
        });


        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, is(String.format("The bovine with tag %s already exists", bovine.getTag())));
    }

    @Test
    public void deleteShouldAlwaysReturnOKWhenBovineExists() {
        CemaBovine cemaBovine = new CemaBovine();
        String tag = "123";
        when(bovineRepository.findCemaBovineByTag(tag)).thenReturn(cemaBovine);

        Controller controller = new Controller(bovineRepository, bovineMapping);

        ResponseEntity<Bovine> result = controller.deleteBovine(tag);

        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.NO_CONTENT));
    }

    @Test
    public void deleteShouldAlwaysReturnNotFoundWhenUserDoesNotExists() {
        CemaBovine cemaBovine = new CemaBovine();
        String tag = "123";
        when(bovineRepository.findCemaBovineByTag(tag)).thenReturn(cemaBovine);

        Controller controller = new Controller(bovineRepository, bovineMapping);

        Exception exception = assertThrows(BovineNotFoundException.class, () -> {
            controller.deleteBovine("234");
        });

        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, is("Bovine 234 doesn't exits"));
    }

}