package com.cema.bovine.controllers;


import com.cema.bovine.domain.Batch;
import com.cema.bovine.entities.CemaBatch;
import com.cema.bovine.exceptions.AlreadyExistsException;
import com.cema.bovine.exceptions.NotFoundException;
import com.cema.bovine.exceptions.UnauthorizedException;
import com.cema.bovine.mapping.BatchMapping;
import com.cema.bovine.repositories.BatchRepository;
import com.cema.bovine.services.authorization.AuthorizationService;
import com.cema.bovine.services.database.DatabaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class BatchControllerTest {

    @Mock
    private BatchRepository batchRepository;
    @Mock
    private BatchMapping batchMapping;
    @Mock
    private DatabaseService databaseService;
    @Mock
    private AuthorizationService authorizationService;

    private BatchController batchController;

    private String cuig = "321";

    @BeforeEach
    public void setUp() {
        openMocks(this);
        when(authorizationService.isOnTheSameEstablishment(cuig)).thenReturn(true);
        when(authorizationService.getCurrentUserCuig()).thenReturn(cuig);
        batchController = new BatchController(batchRepository, batchMapping,
                databaseService, authorizationService);
    }

    @Test
    public void lookUpBatchByTagShouldAlwaysReturnBatchWhenExists() {
        CemaBatch cemaBatch = new CemaBatch();
        cemaBatch.setEstablishmentCuig(cuig);
        Batch batch = Batch.builder().build();
        String name = "123";
        when(batchRepository.findCemaBatchByBatchNameAndEstablishmentCuigIgnoreCase(name, cuig)).thenReturn(cemaBatch);
        when(batchMapping.mapEntityToDomain(cemaBatch)).thenReturn(batch);
        ResponseEntity<Batch> result = batchController.lookUpBatchByName(name, cuig);
        Batch resultingUser = result.getBody();

        assertThat(resultingUser, is(batch));
        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.OK));
    }

    @Test
    public void lookUpBatchByTagShouldAlwaysReturnBatchWhenExistsWithSentCuigWhenAdmin() {
        when(authorizationService.isAdmin()).thenReturn(true);
        when(authorizationService.getCurrentUserCuig()).thenReturn("otherCuig");
        CemaBatch cemaBatch = new CemaBatch();
        cemaBatch.setEstablishmentCuig(cuig);
        Batch batch = Batch.builder().build();
        String tag = "123";
        when(batchRepository.findCemaBatchByBatchNameAndEstablishmentCuigIgnoreCase(tag, cuig)).thenReturn(cemaBatch);
        when(batchMapping.mapEntityToDomain(cemaBatch)).thenReturn(batch);
        ResponseEntity<Batch> result = batchController.lookUpBatchByName(tag, cuig);
        Batch resultingUser = result.getBody();

        assertThat(resultingUser, is(batch));
        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.OK));
    }

    @Test
    public void registerBatchShouldAlwaysReturnCreatedWhenBatchAddedCorrectly() {
        CemaBatch cemaBatch = new CemaBatch();
        Batch batch = Batch.builder().build();
        batch.setEstablishmentCuig(cuig);
        when(batchMapping.mapDomainToEntity(batch)).thenReturn(cemaBatch);

        ResponseEntity<String> result = batchController.registerBatch(batch);

        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.CREATED));
    }

    @Test
    public void registerBatchShouldAlwaysReturnUnprocessableEntityWhenBatchExists() {
        CemaBatch cemaBatch = new CemaBatch();
        Batch batch = Batch.builder().build();
        batch.setEstablishmentCuig(cuig);
        String name = "123";
        cemaBatch.setBatchName(name);
        batch.setBatchName(name);

        when(batchRepository.findCemaBatchByBatchNameAndEstablishmentCuigIgnoreCase(name, cuig)).thenReturn(cemaBatch);
        when(batchMapping.mapDomainToEntity(batch)).thenReturn(cemaBatch);

        Exception exception = assertThrows(AlreadyExistsException.class, () -> {
            batchController.registerBatch(batch);
        });


        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, is(String.format("The batch with name %s already exists", batch.getBatchName())));
    }

    @Test
    public void registerShouldThrowUnauthorizedExceptionWhenAnExternalCuigIsRequested() {
        String otherCuig = "000";
        CemaBatch cemaBatch = new CemaBatch();
        Batch batch = Batch.builder().build();
        String tag = "123";
        batch.setBatchName(tag);
        batch.setEstablishmentCuig(otherCuig);

        when(batchRepository.findCemaBatchByBatchNameAndEstablishmentCuigIgnoreCase(tag, otherCuig)).thenReturn(cemaBatch);

        Exception exception = assertThrows(UnauthorizedException.class, () -> {
            batchController.registerBatch(batch);
        });

        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, is("Error trying to access resource from a different establishment 000."));
    }

    @Test
    public void deleteShouldAlwaysReturnOKWhenBatchExistsWithSentCuigWhenAdmin() {
        when(authorizationService.isAdmin()).thenReturn(true);
        when(authorizationService.getCurrentUserCuig()).thenReturn("otherCuig");
        CemaBatch cemaBatch = new CemaBatch();
        cemaBatch.setEstablishmentCuig(cuig);
        String name = "123";
        when(batchRepository.findCemaBatchByBatchNameAndEstablishmentCuigIgnoreCase(name, cuig)).thenReturn(cemaBatch);

        ResponseEntity<String> result = batchController.deleteBatch(name, cuig);

        verify(databaseService).deleteBatch(cemaBatch);

        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.NO_CONTENT));
    }

    @Test
    public void deleteShouldAlwaysReturnNotFoundWhenUserDoesNotExists() {
        CemaBatch cemaBatch = new CemaBatch();
        cemaBatch.setEstablishmentCuig(cuig);
        String name = "123";
        when(batchRepository.findCemaBatchByBatchNameAndEstablishmentCuigIgnoreCase(name, cuig)).thenReturn(cemaBatch);

        Exception exception = assertThrows(NotFoundException.class, () -> {
            batchController.deleteBatch("234", cuig);
        });

        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, is("Batch 234 doesn't exits"));
    }

    @Test
    public void addBovinesShouldAlwaysReturnOKWhenBatchExistsWithSentCuigWhenAdmin() {
        CemaBatch cemaBatch = new CemaBatch();
        cemaBatch.setEstablishmentCuig(cuig);
        CemaBatch updatedCemaBatch = new CemaBatch();
        Batch batch = Batch.builder().build();
        String name = "123";
        List<String> tags = new ArrayList<>();
        when(batchRepository.findCemaBatchByBatchNameAndEstablishmentCuigIgnoreCase(name, cuig)).thenReturn(cemaBatch);
        when(databaseService.addBovinesToBatch(cemaBatch, tags)).thenReturn(updatedCemaBatch);
        when(batchMapping.mapEntityToDomain(updatedCemaBatch)).thenReturn(batch);

        ResponseEntity<Batch> result = batchController.addBovinesToBatch(name, cuig, tags);

        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.OK));
        assertThat(result.getBody(), is(batch));
    }

    @Test
    public void removeBovinesShouldAlwaysReturnOKWhenBatchExistsWithSentCuigWhenAdmin() {
        CemaBatch cemaBatch = new CemaBatch();
        cemaBatch.setEstablishmentCuig(cuig);
        CemaBatch updatedCemaBatch = new CemaBatch();
        Batch batch = Batch.builder().build();
        String name = "123";
        List<String> tags = new ArrayList<>();
        when(batchRepository.findCemaBatchByBatchNameAndEstablishmentCuigIgnoreCase(name, cuig)).thenReturn(cemaBatch);
        when(databaseService.removeBovinesFromBatch(cemaBatch, tags)).thenReturn(updatedCemaBatch);
        when(batchMapping.mapEntityToDomain(updatedCemaBatch)).thenReturn(batch);

        ResponseEntity<Batch> result = batchController.removeBovinesFromBatch(name, cuig, tags);

        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.OK));
        assertThat(result.getBody(), is(batch));
    }

    @Test
    public void lookUpBatchByTagShouldAlwaysReturnNotFoundWhenBatchDoesntExists() {
        CemaBatch cemaBatch = new CemaBatch();
        cemaBatch.setEstablishmentCuig(cuig);
        Batch batch = Batch.builder().build();
        String tag = "123";
        when(batchRepository.findCemaBatchByBatchNameAndEstablishmentCuigIgnoreCase(tag, cuig)).thenReturn(cemaBatch);
        when(batchMapping.mapEntityToDomain(cemaBatch)).thenReturn(batch);

        Exception exception = assertThrows(NotFoundException.class, () -> {
            batchController.lookUpBatchByName("234", cuig);
        });
        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, is("Batch with name 234 doesn't exits"));
    }

}