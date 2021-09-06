package com.cema.bovine.controllers;

import com.cema.bovine.domain.Bovine;
import com.cema.bovine.entities.CemaBovine;
import com.cema.bovine.exceptions.BovineAlreadyExistsException;
import com.cema.bovine.exceptions.BovineNotFoundException;
import com.cema.bovine.exceptions.UnauthorizedException;
import com.cema.bovine.mapping.BovineMapping;
import com.cema.bovine.repositories.BovineRepository;
import com.cema.bovine.services.authorization.AuthorizationService;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class BovineControllerTest {

    @Mock
    private BovineRepository bovineRepository;
    @Mock
    private BovineMapping bovineMapping;
    @Mock
    private DatabaseService databaseService;
    @Mock
    private AuthorizationService authorizationService;

    private BovineController bovineController;

    private String cuig = "321";

    @BeforeEach
    public void startUp() {
        openMocks(this);
        when(authorizationService.isOnTheSameEstablishment(cuig)).thenReturn(true);
        when(authorizationService.getCurrentUserCuig()).thenReturn(cuig);
        bovineController = new BovineController(bovineRepository, bovineMapping, databaseService, authorizationService);
    }

    @Test
    public void lookUpBovineByTagShouldAlwaysReturnBovineWhenExists() {
        CemaBovine cemaBovine = new CemaBovine();
        cemaBovine.setEstablishmentCuig(cuig);
        Bovine bovine = new Bovine();
        String tag = "123";
        when(bovineRepository.findCemaBovineByTagAndEstablishmentCuigIgnoreCase(tag, cuig)).thenReturn(cemaBovine);
        when(bovineMapping.mapEntityToDomain(cemaBovine)).thenReturn(bovine);
        ResponseEntity<Bovine> result = bovineController.lookUpBovineByTag(tag, cuig);
        Bovine resultingUser = result.getBody();

        assertThat(resultingUser, is(bovine));
        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.OK));
    }

    @Test
    public void lookUpBovineByTagShouldAlwaysReturnBovineWhenExistsWithSentCuigWhenAdmin() {
        when(authorizationService.isAdmin()).thenReturn(true);
        when(authorizationService.getCurrentUserCuig()).thenReturn("otherCuig");
        CemaBovine cemaBovine = new CemaBovine();
        cemaBovine.setEstablishmentCuig(cuig);
        Bovine bovine = new Bovine();
        String tag = "123";
        when(bovineRepository.findCemaBovineByTagAndEstablishmentCuigIgnoreCase(tag, cuig)).thenReturn(cemaBovine);
        when(bovineMapping.mapEntityToDomain(cemaBovine)).thenReturn(bovine);
        ResponseEntity<Bovine> result = bovineController.lookUpBovineByTag(tag, cuig);
        Bovine resultingUser = result.getBody();

        assertThat(resultingUser, is(bovine));
        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.OK));
    }

    @Test
    public void lookUpBovineByTagShouldAlwaysReturnNotFoundWhenBovineDoesntExists() {
        CemaBovine cemaBovine = new CemaBovine();
        cemaBovine.setEstablishmentCuig(cuig);
        Bovine bovine = new Bovine();
        String tag = "123";
        when(bovineRepository.findCemaBovineByTagAndEstablishmentCuigIgnoreCase(tag, cuig)).thenReturn(cemaBovine);
        when(bovineMapping.mapEntityToDomain(cemaBovine)).thenReturn(bovine);

        Exception exception = assertThrows(BovineNotFoundException.class, () -> {
            bovineController.lookUpBovineByTag("234", cuig);
        });
        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, is("Bovine with tag 234 doesn't exits"));
    }

    @Test
    public void updateBovineShouldAlwaysReturnOKWhenBovineUpdatedCorrectly() {
        CemaBovine cemaBovine = new CemaBovine();
        cemaBovine.setEstablishmentCuig(cuig);
        Bovine bovine = new Bovine();
        String tag = "123";
        bovine.setTag(tag);

        when(bovineRepository.findCemaBovineByTagAndEstablishmentCuigIgnoreCase(tag, cuig)).thenReturn(cemaBovine);
        when(bovineMapping.mapDomainToEntity(bovine, cemaBovine)).thenReturn(cemaBovine);

        ResponseEntity<Bovine> result = bovineController.updateBovine(tag, cuig, bovine);

        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.OK));
    }

    @Test
    public void updateBovineShouldAlwaysReturnOKWhenBovineUpdatedCorrectlyWithSentCuigWhenAdmin() {
        when(authorizationService.isAdmin()).thenReturn(true);
        when(authorizationService.getCurrentUserCuig()).thenReturn("otherCuig");
        CemaBovine cemaBovine = new CemaBovine();
        cemaBovine.setEstablishmentCuig(cuig);
        Bovine bovine = new Bovine();
        String tag = "123";
        bovine.setTag(tag);

        when(bovineRepository.findCemaBovineByTagAndEstablishmentCuigIgnoreCase(tag, cuig)).thenReturn(cemaBovine);
        when(bovineMapping.mapDomainToEntity(bovine, cemaBovine)).thenReturn(cemaBovine);

        ResponseEntity<Bovine> result = bovineController.updateBovine(tag, cuig, bovine);

        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.OK));
    }

    @Test
    public void updateBovineShouldAlwaysReturnNotFoundWhenBovineDoesntExists() {
        CemaBovine cemaBovine = new CemaBovine();
        Bovine bovine = new Bovine();
        bovine.setEstablishmentCuig(cuig);
        String tag = "123";
        when(bovineRepository.findCemaBovineByTagAndEstablishmentCuigIgnoreCase(tag, cuig)).thenReturn(cemaBovine);


        Exception exception = assertThrows(BovineNotFoundException.class, () -> {
            bovineController.updateBovine("234", cuig, bovine);
        });
        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, is("Bovine with tag 234 doesn't exits"));
    }

    @Test
    public void registerBovineShouldAlwaysReturnCreatedWhenBovineAddedCorrectly() {
        CemaBovine cemaBovine = new CemaBovine();
        Bovine bovine = new Bovine();
        bovine.setEstablishmentCuig(cuig);
        when(bovineMapping.mapDomainToEntity(bovine)).thenReturn(cemaBovine);

        ResponseEntity<Bovine> result = bovineController.registerBovine(bovine);

        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.CREATED));
    }

    @Test
    public void registerBovineShouldAlwaysReturnUnprocessableEntityWhenBovineExists() {
        CemaBovine cemaBovine = new CemaBovine();
        Bovine bovine = new Bovine();
        bovine.setEstablishmentCuig(cuig);
        String tag = "123";
        cemaBovine.setTag(tag);
        bovine.setTag(tag);

        when(bovineRepository.findCemaBovineByTagAndEstablishmentCuigIgnoreCase(tag, cuig)).thenReturn(cemaBovine);
        when(bovineMapping.mapDomainToEntity(bovine)).thenReturn(cemaBovine);

        Exception exception = assertThrows(BovineAlreadyExistsException.class, () -> {
            bovineController.registerBovine(bovine);
        });


        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, is(String.format("The bovine with tag %s already exists", bovine.getTag())));
    }

    @Test
    public void registerShouldThrowUnauthorizedExceptionWhenAnExternalCuigIsRequested() {
        String otherCuig = "000";
        CemaBovine cemaBovine = new CemaBovine();
        Bovine bovine = new Bovine();
        String tag = "123";
        bovine.setTag(tag);
        bovine.setEstablishmentCuig(otherCuig);

        when(bovineRepository.findCemaBovineByTagAndEstablishmentCuigIgnoreCase(tag, otherCuig)).thenReturn(cemaBovine);

        Exception exception = assertThrows(UnauthorizedException.class, () -> {
            bovineController.registerBovine(bovine);
        });

        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, is("Error trying to access resource from a different establishment 000."));
    }

    @Test
    public void deleteShouldAlwaysReturnOKWhenBovineExists() {
        CemaBovine cemaBovine = new CemaBovine();
        cemaBovine.setEstablishmentCuig(cuig);
        String tag = "123";
        when(bovineRepository.findCemaBovineByTagAndEstablishmentCuigIgnoreCase(tag, cuig)).thenReturn(cemaBovine);

        ResponseEntity<Bovine> result = bovineController.deleteBovine(tag, cuig);

        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.NO_CONTENT));
    }

    @Test
    public void deleteShouldAlwaysReturnOKWhenBovineExistsWithSentCuigWhenAdmin() {
        when(authorizationService.isAdmin()).thenReturn(true);
        when(authorizationService.getCurrentUserCuig()).thenReturn("otherCuig");
        CemaBovine cemaBovine = new CemaBovine();
        cemaBovine.setEstablishmentCuig(cuig);
        String tag = "123";
        when(bovineRepository.findCemaBovineByTagAndEstablishmentCuigIgnoreCase(tag, cuig)).thenReturn(cemaBovine);

        ResponseEntity<Bovine> result = bovineController.deleteBovine(tag, cuig);

        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.NO_CONTENT));
    }

    @Test
    public void deleteShouldAlwaysReturnNotFoundWhenUserDoesNotExists() {
        CemaBovine cemaBovine = new CemaBovine();
        cemaBovine.setEstablishmentCuig(cuig);
        String tag = "123";
        when(bovineRepository.findCemaBovineByTagAndEstablishmentCuigIgnoreCase(tag, cuig)).thenReturn(cemaBovine);

        Exception exception = assertThrows(BovineNotFoundException.class, () -> {
            bovineController.deleteBovine("234", cuig);
        });

        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, is("Bovine 234 doesn't exits"));
    }

    @Test
    public void searchBovinesShouldReturnBovinesFromTheSameCuigAsNonAdminUsers(){

        Page<CemaBovine> bovinePage = Mockito.mock(Page.class);
        List<CemaBovine> bovineList = new ArrayList<>();
        CemaBovine cemaBovine1 = new CemaBovine();
        CemaBovine cemaBovine2 = new CemaBovine();
        CemaBovine cemaBovine3 = new CemaBovine();
        Bovine bovine1 = new Bovine();
        bovine1.setEstablishmentCuig(cuig);
        bovine1.setTag("11");
        Bovine bovine2 = new Bovine();
        bovine2.setEstablishmentCuig(cuig);
        bovine2.setTag("12");
        Bovine bovine3 = new Bovine();
        bovine3.setEstablishmentCuig(cuig);
        bovine3.setTag("13");
        bovineList.add(cemaBovine1);
        bovineList.add(cemaBovine2);
        bovineList.add(cemaBovine3);

        when(bovinePage.getContent()).thenReturn(bovineList);
        when(bovinePage.getTotalElements()).thenReturn(2L);
        when(bovinePage.getTotalPages()).thenReturn(1);
        when(bovinePage.getNumber()).thenReturn(0);
        when(databaseService.searchBovines(cuig, "1", null, null, 0, 2)).thenReturn(bovinePage);
        when(bovineMapping.mapEntityToDomain(cemaBovine1)).thenReturn(bovine1);
        when(bovineMapping.mapEntityToDomain(cemaBovine2)).thenReturn(bovine2);
        when(bovineMapping.mapEntityToDomain(cemaBovine3)).thenReturn(bovine3);


        ResponseEntity<List<Bovine>> result = bovineController.searchBovines("otherCUIG", "1", null, null, 0, 2);

        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.OK));

        List<Bovine> resultBody = result.getBody();

        assertTrue(resultBody.contains(bovine1));
        assertTrue(resultBody.contains(bovine2));
        assertTrue(resultBody.contains(bovine3));

        HttpHeaders responseHeaders = result.getHeaders();

        assertThat(responseHeaders.getFirst("total-elements"), is("2"));
        assertThat(responseHeaders.getFirst("total-pages"), is("1"));
        assertThat(responseHeaders.getFirst("current-page"), is("0"));
    }

    @Test
    public void searchBovinesShouldReturnBovinesForTheSearchCriteriaFromAllCuigsForAdminUsers(){
        when(authorizationService.isAdmin()).thenReturn(true);
        String otherCuig = "000";
        Page<CemaBovine> bovinePage = Mockito.mock(Page.class);
        List<CemaBovine> bovineList = new ArrayList<>();
        CemaBovine cemaBovine1 = new CemaBovine();
        CemaBovine cemaBovine2 = new CemaBovine();
        CemaBovine cemaBovine3 = new CemaBovine();
        Bovine bovine1 = new Bovine();
        bovine1.setEstablishmentCuig(cuig);
        bovine1.setTag("11");
        Bovine bovine2 = new Bovine();
        bovine2.setEstablishmentCuig(cuig);
        bovine2.setTag("12");
        Bovine bovine3 = new Bovine();
        bovine3.setEstablishmentCuig(otherCuig);
        bovine3.setTag("13");
        bovineList.add(cemaBovine1);
        bovineList.add(cemaBovine2);
        bovineList.add(cemaBovine3);

        when(bovinePage.getContent()).thenReturn(bovineList);
        when(bovinePage.getTotalElements()).thenReturn(3L);
        when(bovinePage.getTotalPages()).thenReturn(1);
        when(bovinePage.getNumber()).thenReturn(0);
        when(databaseService.searchBovines(otherCuig, "1", null, null, 0, 2)).thenReturn(bovinePage);
        when(bovineMapping.mapEntityToDomain(cemaBovine1)).thenReturn(bovine1);
        when(bovineMapping.mapEntityToDomain(cemaBovine2)).thenReturn(bovine2);
        when(bovineMapping.mapEntityToDomain(cemaBovine3)).thenReturn(bovine3);


        ResponseEntity<List<Bovine>> result = bovineController.searchBovines(otherCuig, "1", null, null, 0, 2);

        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.OK));

        List<Bovine> resultBody = result.getBody();

        assertTrue(resultBody.contains(bovine1));
        assertTrue(resultBody.contains(bovine2));
        assertTrue(resultBody.contains(bovine3));

        HttpHeaders responseHeaders = result.getHeaders();

        assertThat(responseHeaders.getFirst("total-elements"), is("3"));
        assertThat(responseHeaders.getFirst("total-pages"), is("1"));
        assertThat(responseHeaders.getFirst("current-page"), is("0"));
    }
}