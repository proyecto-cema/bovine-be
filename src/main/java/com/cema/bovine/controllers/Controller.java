package com.cema.bovine.controllers;

import com.cema.bovine.domain.Bovine;
import com.cema.bovine.entities.CemaBovine;
import com.cema.bovine.exceptions.BovineAlreadyExistsException;
import com.cema.bovine.exceptions.BovineNotFoundException;
import com.cema.bovine.exceptions.InvalidParameterException;
import com.cema.bovine.mapping.BovineMapping;
import com.cema.bovine.repositories.BovineRepository;
import com.cema.bovine.services.database.DatabaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;
import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1")
@Api(produces = "application/json", value = "Allows interaction with the bovine database. V1")
public class Controller {

    private static final String BASE_URL = "/bovines/";

    private static final Logger LOG = LoggerFactory.getLogger(Controller.class);

    private final BovineRepository bovineRepository;
    private final BovineMapping bovineMapping;
    private final DatabaseService databaseService;

    public Controller(BovineRepository bovineRepository, BovineMapping bovineMapping, DatabaseService databaseService) {
        this.bovineRepository = bovineRepository;
        this.bovineMapping = bovineMapping;
        this.databaseService = databaseService;
    }

    @ApiOperation(value = "Retrieve bovine from tag sent data", response = Bovine.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully found bovine"),
            @ApiResponse(code = 404, message = "Not found bovine")
    })
    @GetMapping(value = BASE_URL + "{tag}", produces = {MediaType.APPLICATION_JSON_VALUE})
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
    @PutMapping(value = BASE_URL + "{tag}", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
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

        cemaBovine = bovineMapping.mapDomainToEntity(bovine, cemaBovine);

        bovineRepository.save(cemaBovine);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "Delete an existing bovine by tag")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Bovine deleted successfully"),
            @ApiResponse(code = 404, message = "The bovine you were trying to reach is not found")
    })
    @DeleteMapping(value = BASE_URL + "{tag}", produces = {MediaType.APPLICATION_JSON_VALUE})
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

    @ApiOperation(value = "Retrieve a list of bovines matching the sent data", response = Bovine.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully found bovines", responseHeaders = {
                    @ResponseHeader(name = "total-elements", response = String.class, description = "Total number of search results"),
                    @ResponseHeader(name = "total-pages", response = String.class, description = "Total number of pages to navigate"),
                    @ResponseHeader(name = "current-page", response = String.class, description = "The page being returned, zero indexed")
            }),
            @ApiResponse(code = 422, message = "Invalid search, all parameters blank")
    })
    @GetMapping(value = BASE_URL + "search", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Bovine>> searchBovines(
            @ApiParam(
                    value = "The tag of the bovine you are looking for.",
                    example = "123")
            @RequestParam(value = "tag", required = false) String tag,
            @ApiParam(
                    value = "The genre of the bovine you are looking for.",
                    example = "male")
            @RequestParam(value = "genre", required = false) String genre,
            @ApiParam(
                    value = "Some string to search in the description.",
                    example = "brown")
            @RequestParam(value = "description", required = false) String description,
            @ApiParam(
                    value = "The page you want to retrieve.",
                    example = "1")
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @ApiParam(
                    value = "The maximum number of cows to return per page.",
                    example = "10")
            @RequestParam(value = "size", required = false, defaultValue = "3") int size) {

        LOG.info("Searching bovines for bovine with tag {}, genre {} and description {}", tag, genre, description);


        Page<CemaBovine> bovinePage = databaseService.searchBovines(tag, genre, description, page, size);
        List<CemaBovine> bovineList = bovinePage.getContent();
        LOG.info("Returned {} bovines from db", bovineList.size());

        List<Bovine> mappedBovines = bovineList.stream().map(bovineMapping::mapEntityToDomain).collect(Collectors.toList());

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("total-elements", String.valueOf(bovinePage.getTotalElements()));
        responseHeaders.set("total-pages", String.valueOf(bovinePage.getTotalPages()));
        responseHeaders.set("current-page", String.valueOf(bovinePage.getNumber()));

        return ResponseEntity.ok().headers(responseHeaders).body(mappedBovines);
    }

}
