package com.cema.bovine.controllers;

import com.cema.bovine.domain.Bovine;
import com.cema.bovine.entities.CemaBovine;
import com.cema.bovine.exceptions.BovineAlreadyExistsException;
import com.cema.bovine.exceptions.BovineNotFoundException;
import com.cema.bovine.exceptions.InvalidParameterException;
import com.cema.bovine.mapping.BovineMapping;
import com.cema.bovine.repositories.BovineRepository;
import com.cema.bovine.services.database.DatabaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class ControllerTest {

    @Mock
    private BovineRepository bovineRepository;
    @Mock
    private BovineMapping bovineMapping;
    @Mock
    private DatabaseService databaseService;

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
        Controller controller = new Controller(bovineRepository, bovineMapping, databaseService);
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
        Controller controller = new Controller(bovineRepository, bovineMapping, databaseService);

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
        when(bovineMapping.mapDomainToEntity(bovine, cemaBovine)).thenReturn(cemaBovine);

        Controller controller = new Controller(bovineRepository, bovineMapping, databaseService);
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

        Controller controller = new Controller(bovineRepository, bovineMapping, databaseService);

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

        Controller controller = new Controller(bovineRepository, bovineMapping, databaseService);
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

        Controller controller = new Controller(bovineRepository, bovineMapping, databaseService);
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

        Controller controller = new Controller(bovineRepository, bovineMapping, databaseService);

        ResponseEntity<Bovine> result = controller.deleteBovine(tag);

        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.NO_CONTENT));
    }

    @Test
    public void deleteShouldAlwaysReturnNotFoundWhenUserDoesNotExists() {
        CemaBovine cemaBovine = new CemaBovine();
        String tag = "123";
        when(bovineRepository.findCemaBovineByTag(tag)).thenReturn(cemaBovine);

        Controller controller = new Controller(bovineRepository, bovineMapping, databaseService);

        Exception exception = assertThrows(BovineNotFoundException.class, () -> {
            controller.deleteBovine("234");
        });

        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, is("Bovine 234 doesn't exits"));
    }

    @Test
    public void searchBovinesShouldReturnBovinesForTheSearchCriteria(){
        Page<CemaBovine> bovinePage = Mockito.mock(Page.class);
        List<CemaBovine> bovineList = new ArrayList<>();
        CemaBovine cemaBovine1 = new CemaBovine();
        CemaBovine cemaBovine2 = new CemaBovine();
        Bovine bovine1 = new Bovine();
        bovine1.setTag("11");
        Bovine bovine2 = new Bovine();
        bovine2.setTag("12");
        bovineList.add(cemaBovine1);
        bovineList.add(cemaBovine2);

        when(bovinePage.getContent()).thenReturn(bovineList);
        when(bovinePage.getTotalElements()).thenReturn(2L);
        when(bovinePage.getTotalPages()).thenReturn(1);
        when(bovinePage.getNumber()).thenReturn(0);
        when(databaseService.searchBovines("1", null, null, 0, 2)).thenReturn(bovinePage);
        when(bovineMapping.mapEntityToDomain(cemaBovine1)).thenReturn(bovine1);
        when(bovineMapping.mapEntityToDomain(cemaBovine2)).thenReturn(bovine2);

        Controller controller = new Controller(bovineRepository, bovineMapping, databaseService);

        ResponseEntity<List<Bovine>> result = controller.searchBovines("1", null, null, 0, 2);

        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.OK));

        List<Bovine> resultBody = result.getBody();

        assertTrue(resultBody.contains(bovine1));
        assertTrue(resultBody.contains(bovine2));

        HttpHeaders responseHeaders = result.getHeaders();

        assertThat(responseHeaders.getFirst("total-elements"), is("2"));
        assertThat(responseHeaders.getFirst("total-pages"), is("1"));
        assertThat(responseHeaders.getFirst("current-page"), is("0"));
    }

    @Test
    public void searchBovinesShouldAlwaysReturnUnprocessableWhenNoSearchParametersPassed(){
        Controller controller = new Controller(bovineRepository, bovineMapping, databaseService);

        Exception exception = assertThrows(InvalidParameterException.class, () -> {
            controller.searchBovines(null, null, null, 0, 2);
        });

        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, is("Invalid search, all parameters blank"));
    }

}