package com.cema.bovine.controllers;

import com.cema.bovine.domain.Bovine;
import com.cema.bovine.entities.CemaBovine;
import com.cema.bovine.exceptions.BovineAlreadyExistsException;
import com.cema.bovine.exceptions.BovineNotFoundException;
import com.cema.bovine.mapping.BovineMapping;
import com.cema.bovine.repositories.BovineRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
@Api(produces = "application/json", value = "Allows interaction with the bovine database. V1")
public class Controller {

    private static final String BASE_URL = "/bovines/";

    private static final Logger LOG = LoggerFactory.getLogger(Controller.class);

    private final BovineRepository bovineRepository;
    private final BovineMapping bovineMapping;

    public Controller(BovineRepository bovineRepository, BovineMapping bovineMapping) {
        this.bovineRepository = bovineRepository;
        this.bovineMapping = bovineMapping;
    }

    @ApiOperation(value = "Retrieve bovine from tag sent data", response = Bovine.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully found bovine"),
            @ApiResponse(code = 404, message = "Not found bovine")
    })
    @GetMapping(value = BASE_URL +"{tag}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Bovine> lookUpBovineByTag(
            @ApiParam(
                    value = "The tag of the bovine you are looking for.",
                    example = "123")
            @PathVariable("tag") String tag) {

        LOG.info("Request for bovine with {}", tag);

        CemaBovine cemaBovine = bovineRepository.findCemaBovineByTag(tag);
        if (cemaBovine == null) {
            throw new BovineNotFoundException(String.format("Bovine with tag %s doesn't exits", tag));
        }
        Bovine bovine = bovineMapping.mapEntityToDomain(cemaBovine);

        return new ResponseEntity<>(bovine, HttpStatus.OK);
    }

    @ApiOperation(value = "Register a new bovine to the database")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Bovine created successfully"),
            @ApiResponse(code = 409, message = "The bovine you were trying to create already exists")
    })
    @PostMapping(value = BASE_URL, produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Bovine> registerBovine(
            @ApiParam(
                    value = "Bovine data to be inserted.")
            @RequestBody Bovine bovine) {

        LOG.info("Request to register new bovine");

        CemaBovine existsBovine = bovineRepository.findCemaBovineByTag(bovine.getTag());
        if (existsBovine != null) {
            LOG.info("Bovine tag already exists");
            throw new BovineAlreadyExistsException(String.format("The bovine with tag %s already exists", bovine.getTag()));
        }

        CemaBovine newBovine = bovineMapping.mapDomainToEntity(bovine);

        bovineRepository.save(newBovine);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation(value = "Modifies an existent Bovine")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Bovine modified successfully"),
            @ApiResponse(code = 404, message = "The bovine you were trying to modify doesn't exists")
    })
    @PutMapping(value = BASE_URL+"{tag}", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Bovine> updateBovine(
            @ApiParam(
                    value = "The tag of the bovine we are looking for.",
                    example = "123")
            @PathVariable("tag") String tag,
            @ApiParam(
                    value = "The bovine data we are modifying")
            @RequestBody Bovine bovine) {

        LOG.info("Request to modify bovine with tag: {}", tag);

        CemaBovine cemaBovine = bovineRepository.findCemaBovineByTag(tag);
        if (cemaBovine == null) {
            LOG.info("Bovine doesn't exists");
            throw new BovineNotFoundException(String.format("Bovine with tag %s doesn't exits", tag));
        }

        cemaBovine = bovineMapping.mapDomainToEntity(bovine, tag);

        bovineRepository.save(cemaBovine);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "Delete an existing bovine by tag")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Bovine deleted successfully"),
            @ApiResponse(code = 404, message = "The bovine you were trying to reach is not found")
    })
    @DeleteMapping(value = BASE_URL +"{tag}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Bovine> deleteBovine(
            @ApiParam(
                    value = "The tag for the bovine we are looking for.",
                    example = "123")
            @PathVariable("tag") String tag) {

        LOG.info("Request to delete user: {}", tag);

        CemaBovine bovine = bovineRepository.findCemaBovineByTag(tag);
        if (bovine != null) {
            LOG.info("Bovine exists, deleting");
            bovineRepository.delete(bovine);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        LOG.info("Not found");
        throw new BovineNotFoundException(String.format("Bovine %s doesn't exits", tag));
    }

}
