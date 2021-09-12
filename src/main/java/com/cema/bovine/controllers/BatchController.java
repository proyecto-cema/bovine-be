package com.cema.bovine.controllers;

import com.cema.bovine.constants.Messages;
import com.cema.bovine.domain.Batch;
import com.cema.bovine.domain.Bovine;
import com.cema.bovine.entities.CemaBatch;
import com.cema.bovine.exceptions.AlreadyExistsException;
import com.cema.bovine.exceptions.NotFoundException;
import com.cema.bovine.exceptions.UnauthorizedException;
import com.cema.bovine.mapping.BatchMapping;
import com.cema.bovine.repositories.BatchRepository;
import com.cema.bovine.services.authorization.AuthorizationService;
import com.cema.bovine.services.database.DatabaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1")
@Api(produces = "application/json", value = "Allows interaction with the batch database. V1")
@Validated
public class BatchController {

    private static final String BASE_URL = "/batches/";

    private static final Logger LOG = LoggerFactory.getLogger(BatchController.class);

    private final BatchRepository batchRepository;
    private final BatchMapping batchMapping;
    private final DatabaseService databaseService;
    private final AuthorizationService authorizationService;

    public BatchController(BatchRepository batchRepository, BatchMapping batchMapping,
                           DatabaseService databaseService, AuthorizationService authorizationService) {
        this.batchRepository = batchRepository;
        this.batchMapping = batchMapping;
        this.databaseService = databaseService;
        this.authorizationService = authorizationService;
    }

    @ApiOperation(value = "Retrieve a batch by batch name", response = Batch.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully found Batch"),
            @ApiResponse(code = 404, message = "Batch not found")
    })
    @GetMapping(value = BASE_URL + "{batch_name}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Batch> lookUpBatchByName(
            @ApiParam(
                    value = "The name of the batch you are looking for.",
                    example = "123")
            @PathVariable("batch_name") String name,
            @ApiParam(
                    value = "The cuig of the establishment of the batch. If the user is not admin will be ignored.",
                    example = "312")
            @RequestParam(value = "cuig") String cuig) {

        if (!authorizationService.isAdmin()) {
            cuig = authorizationService.getCurrentUserCuig();
        }
        LOG.info("Request for batch with name {} and cuig {}", name, cuig);
        CemaBatch cemaBatch = batchRepository.findCemaBatchByBatchNameAndEstablishmentCuigIgnoreCase(name, cuig);
        if (cemaBatch == null) {
            throw new NotFoundException(String.format("Batch with name %s doesn't exits", name));
        }
        Batch batch = batchMapping.mapEntityToDomain(cemaBatch);

        return new ResponseEntity<>(batch, HttpStatus.OK);
    }

    @ApiOperation(value = "Register a new batch to the database")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Batch created successfully"),
            @ApiResponse(code = 409, message = "The batch you were trying to create already exists"),
            @ApiResponse(code = 401, message = "You are not allowed to register this batch")
    })
    @PostMapping(value = BASE_URL, produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> registerBatch(
            @ApiParam(
                    value = "Batch data to be inserted.")
            @RequestBody @Valid Batch batch) {

        String cuig = batch.getEstablishmentCuig();
        LOG.info("Request to register new batch {}", batch);
        if (!authorizationService.isOnTheSameEstablishment(cuig)) {
            throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, cuig));
        }
        CemaBatch cemaBatch = batchRepository.findCemaBatchByBatchNameAndEstablishmentCuigIgnoreCase(batch.getBatchName(), cuig);
        if (cemaBatch != null) {
            LOG.info("Batch already exists");
            throw new AlreadyExistsException(String.format("The batch with name %s already exists", batch.getBatchName()));
        }

        databaseService.saveCemaBatch(batch);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation(value = "Delete an existing batch by name")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Batch deleted successfully"),
            @ApiResponse(code = 404, message = "The batch you were trying to reach is not found")
    })
    @DeleteMapping(value = BASE_URL + "{batch_name}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> deleteBatch(
            @ApiParam(
                    value = "The name for the batch we are looking for.",
                    example = "123")
            @PathVariable("batch_name") String batchName,
            @ApiParam(
                    value = "The cuig of the establishment of the batch. If the user is not admin will be ignored.",
                    example = "312")
            @RequestParam(value = "cuig") String cuig) {

        if (!authorizationService.isAdmin()) {
            cuig = authorizationService.getCurrentUserCuig();
        }
        LOG.info("Request to delete batch with name {} and cuig {}", batchName, cuig);
        CemaBatch cemaBatch = batchRepository.findCemaBatchByBatchNameAndEstablishmentCuigIgnoreCase(batchName, cuig);
        if (cemaBatch != null) {
            LOG.info("Batch exists, deleting");
            databaseService.deleteBatch(cemaBatch);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        LOG.info("Batch Not found");
        throw new NotFoundException(String.format("Batch %s doesn't exits", batchName));
    }

    @ApiOperation(value = "Adds bovines to an existing batch")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Bovines added successfuly"),
            @ApiResponse(code = 404, message = "The batch you were trying to reach is not found")
    })
    @PostMapping(value = BASE_URL + "{batch_name}/add", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Batch> addBovinesToBatch(
            @ApiParam(
                    value = "The name for the batch we are looking for.",
                    example = "123")
            @PathVariable("batch_name") String batchName,
            @ApiParam(
                    value = "The cuig of the establishment of the batch. If the user is not admin will be ignored.",
                    example = "312")
            @RequestParam(value = "cuig") String cuig,
            @ApiParam(value = "Bovine tags to add. If not found will be ignored", example = "[\"1234\",\"1235\",\"2222\",\"3333\"]")
            @RequestBody List<String> bovineTags) {

        if (!authorizationService.isAdmin()) {
            cuig = authorizationService.getCurrentUserCuig();
        }
        CemaBatch cemaBatch = batchRepository.findCemaBatchByBatchNameAndEstablishmentCuigIgnoreCase(batchName, cuig);

        cemaBatch = databaseService.addBovinesToBatch(cemaBatch, bovineTags);

        Batch updated = batchMapping.mapEntityToDomain(cemaBatch);

        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @ApiOperation(value = "Removes bovines from an existing batch")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Bovines removed successfuly"),
            @ApiResponse(code = 404, message = "The batch you were trying to reach is not found")
    })
    @PostMapping(value = BASE_URL + "{batch_name}/remove", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Batch> removeBovinesFromBatch(
            @ApiParam(
                    value = "The name for the batch we are looking for.",
                    example = "123")
            @PathVariable("batch_name") String batchName,
            @ApiParam(
                    value = "The cuig of the establishment of the batch. If the user is not admin will be ignored.",
                    example = "312")
            @RequestParam(value = "cuig") String cuig,
            @ApiParam(value = "Bovines to add. If not found will be ignored", example = "[\"1234\",\"1235\",\"2222\",\"3333\"]")
            @RequestBody List<String> bovineTags) {

        if (!authorizationService.isAdmin()) {
            cuig = authorizationService.getCurrentUserCuig();
        }
        CemaBatch cemaBatch = batchRepository.findCemaBatchByBatchNameAndEstablishmentCuigIgnoreCase(batchName, cuig);

        cemaBatch = databaseService.removeBovinesFromBatch(cemaBatch, bovineTags);

        Batch updated = batchMapping.mapEntityToDomain(cemaBatch);

        return new ResponseEntity<>(updated, HttpStatus.OK);
    }


    @ApiOperation(value = "Retrieve all batches for your cuig", response = Bovine.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Listed all batches")
    })
    @GetMapping(value = BASE_URL + "list", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Batch>> listBatches() {
        String cuig = authorizationService.getCurrentUserCuig();

        List<CemaBatch> cemaBatches;
        if (authorizationService.isAdmin()) {
            cemaBatches = batchRepository.findAll();
        } else {
            cemaBatches = batchRepository.findAllByEstablishmentCuig(cuig);
        }

        List<Batch> batches = cemaBatches.stream().map(batchMapping::mapEntityToDomain).collect(Collectors.toList());

        return new ResponseEntity<>(batches, HttpStatus.OK);
    }

}
