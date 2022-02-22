package com.cema.bovine.controllers;

import com.cema.bovine.constants.Messages;
import com.cema.bovine.domain.Bovine;
import com.cema.bovine.domain.health.Illness;
import com.cema.bovine.entities.CemaBovine;
import com.cema.bovine.exceptions.AlreadyExistsException;
import com.cema.bovine.exceptions.NotFoundException;
import com.cema.bovine.exceptions.UnauthorizedException;
import com.cema.bovine.mapping.BovineMapping;
import com.cema.bovine.repositories.BovineRepository;
import com.cema.bovine.services.administration.AdministrationClientService;
import com.cema.bovine.services.authorization.AuthorizationService;
import com.cema.bovine.services.database.DatabaseService;
import com.cema.bovine.services.health.HealthClientService;
import com.cema.bovine.services.validation.BovineValidationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1")
@Api(produces = "application/json", value = "Allows interaction with the bovine database. V1")
@Validated
public class BovineController {

    private static final String BASE_URL = "/bovines/";

    private static final Logger LOG = LoggerFactory.getLogger(BovineController.class);

    private final BovineRepository bovineRepository;
    private final BovineMapping bovineMapping;
    private final DatabaseService databaseService;
    private final AuthorizationService authorizationService;
    private final BovineValidationService bovineValidationService;
    private final AdministrationClientService administrationClientService;
    private final HealthClientService healthClientService;

    public BovineController(BovineRepository bovineRepository, BovineMapping bovineMapping,
                            DatabaseService databaseService, AuthorizationService authorizationService,
                            BovineValidationService bovineValidationService,
                            AdministrationClientService administrationClientService,
                            HealthClientService healthClientService) {
        this.bovineRepository = bovineRepository;
        this.bovineMapping = bovineMapping;
        this.databaseService = databaseService;
        this.authorizationService = authorizationService;
        this.bovineValidationService = bovineValidationService;
        this.administrationClientService = administrationClientService;
        this.healthClientService = healthClientService;
    }

    @ApiOperation(value = "Validate a bovine", response = Bovine.class)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Bovine is valid"),
            @ApiResponse(code = 404, message = "Bovine not found"),
            @ApiResponse(code = 422, message = "Bovine is not available")
    })
    @GetMapping(value = BASE_URL + "validate/{tag}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> validateBovineByTag(
            @ApiParam(
                    value = "The tag of the bovine to validate.",
                    example = "123")
            @PathVariable("tag") String tag,
            @ApiParam(
                    value = "The cuig of the establishment of the bovine. If the user is not admin will be ignored.",
                    example = "312")
            @RequestParam(value = "cuig", required = false) String cuig) {

        if (!authorizationService.isAdmin() || !StringUtils.hasLength(cuig)) {
            cuig = authorizationService.getCurrentUserCuig();
        }
        LOG.info("Request for bovine with tag {} and cuig {}", tag, cuig);
        CemaBovine cemaBovine = bovineRepository.findCemaBovineByTagAndEstablishmentCuigIgnoreCase(tag, cuig);
        if (cemaBovine == null) {
            throw new NotFoundException(String.format("Bovine with tag %s doesn't exits", tag));
        }
        Bovine bovine = bovineMapping.mapEntityToDomain(cemaBovine);

        bovineValidationService.validateBovineForUsage(bovine);

        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Retrieve bovine from tag sent data", response = Bovine.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully found bovine"),
            @ApiResponse(code = 404, message = "Bovine not found")
    })
    @GetMapping(value = BASE_URL + "{tag}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Bovine> lookUpBovineByTag(
            @ApiParam(
                    value = "The tag of the bovine you are looking for.",
                    example = "123")
            @PathVariable("tag") String tag,
            @ApiParam(
                    value = "The cuig of the establishment of the bovine. If the user is not admin will be ignored.",
                    example = "312")
            @RequestParam(value = "cuig", required = false) String cuig) {

        if (!authorizationService.isAdmin() || !StringUtils.hasLength(cuig)) {
            cuig = authorizationService.getCurrentUserCuig();
        }
        LOG.info("Request for bovine with tag {} and cuig {}", tag, cuig);
        CemaBovine cemaBovine = bovineRepository.findCemaBovineByTagAndEstablishmentCuigIgnoreCase(tag, cuig);
        if (cemaBovine == null) {
            throw new NotFoundException(String.format("Bovine with tag %s doesn't exits", tag));
        }
        Illness illness = healthClientService.getBovineIllness(tag);
        Bovine bovine = bovineMapping.mapEntityToDomain(cemaBovine);
        bovine.setIllness(illness);

        return new ResponseEntity<>(bovine, HttpStatus.OK);
    }

    @ApiOperation(value = "Register a new bovine to the database")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Bovine created successfully"),
            @ApiResponse(code = 409, message = "The bovine you were trying to create already exists"),
            @ApiResponse(code = 401, message = "You are not allowed to register this bovine")
    })
    @PostMapping(value = BASE_URL, produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Bovine> registerBovine(
            @ApiParam(
                    value = "Bovine data to be inserted.")
            @RequestBody @Valid Bovine bovine) {

        String cuig = bovine.getEstablishmentCuig();
        LOG.info("Request to register new bovine {}", bovine);
        if (!authorizationService.isOnTheSameEstablishment(cuig)) {
            throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, cuig));
        }
        administrationClientService.validateEstablishment(cuig);
        CemaBovine existsBovine = bovineRepository.findCemaBovineByTagAndEstablishmentCuigIgnoreCase(bovine.getTag(), cuig);
        if (existsBovine != null) {
            LOG.info("Bovine tag already exists");
            throw new AlreadyExistsException(String.format("The bovine with tag %s already exists", bovine.getTag()));
        }

        bovineValidationService.validateBovineCreation(bovine);

        CemaBovine cemaBovine = bovineMapping.mapDomainToEntity(bovine);

        bovineRepository.save(cemaBovine);

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
                    value = "The cuig of the establishment of the bovine. If the user is not admin will be ignored.",
                    example = "312")
            @RequestParam(value = "cuig", required = false) String cuig,
            @ApiParam(
                    value = "The bovine data we are modifying")
            @RequestBody Bovine bovine) {

        if (!authorizationService.isAdmin() || !StringUtils.hasLength(cuig)) {
            cuig = authorizationService.getCurrentUserCuig();
        }
        LOG.info("Request to modify bovine with tag {} and cuig {}", tag, cuig);
        CemaBovine cemaBovine = bovineRepository.findCemaBovineByTagAndEstablishmentCuigIgnoreCase(tag, cuig);

        if (cemaBovine == null) {
            LOG.info("Bovine with tag {} and cuig {} doesn't exists", tag, cuig);
            throw new NotFoundException(String.format("Bovine with tag %s doesn't exits", tag));
        }
        administrationClientService.validateEstablishment(cuig);
        bovineValidationService.validateBovineUpdate(bovine, cemaBovine);

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
            @PathVariable("tag") String tag,
            @ApiParam(
                    value = "The cuig of the establishment of the bovine. If the user is not admin will be ignored.",
                    example = "312")
            @RequestParam(value = "cuig", required = false) String cuig) {

        if (!authorizationService.isAdmin() || !StringUtils.hasLength(cuig)) {
            cuig = authorizationService.getCurrentUserCuig();
        }
        LOG.info("Request to delete bovine with tag {} and cuig {}", tag, cuig);
        CemaBovine cemaBovine = bovineRepository.findCemaBovineByTagAndEstablishmentCuigIgnoreCase(tag, cuig);
        if (cemaBovine != null) {
            LOG.info("Bovine exists, deleting");
            bovineRepository.delete(cemaBovine);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        LOG.info("Not found");
        throw new NotFoundException(String.format("Bovine %s doesn't exits", tag));
    }

    @ApiOperation(value = "Retrieve a list of bovines matching the sent data", response = Bovine.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully found bovines", responseHeaders = {
                    @ResponseHeader(name = "total-elements", response = String.class, description = "Total number of search results"),
                    @ResponseHeader(name = "total-pages", response = String.class, description = "Total number of pages to navigate"),
                    @ResponseHeader(name = "current-page", response = String.class, description = "The page being returned, zero indexed")
            })
    })
    @GetMapping(value = BASE_URL + "search", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Bovine>> searchBovines(
            @ApiParam(
                    value = "The cuig of the establishment of the bovine. If the user is not admin will be ignored.",
                    example = "312")
            @RequestParam(value = "cuig", required = false) String cuig,
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


        String userCuig = authorizationService.getCurrentUserCuig();
        if (authorizationService.isAdmin()) {
            userCuig = cuig;
        }

        LOG.info("Searching bovines for bovine with cuig {}, tag {}, genre {} and description {}", userCuig, tag, genre, description);

        Page<CemaBovine> bovinePage = databaseService.searchBovines(userCuig, tag, genre, description, page, size);
        List<CemaBovine> bovineList = bovinePage.getContent();
        LOG.info("Returned {} bovines from db", bovineList.size());

        List<Bovine> mappedBovines = bovineList.stream().map(bovineMapping::mapEntityToDomain).collect(Collectors.toList());

        for (Bovine bovine : mappedBovines) {
            Illness illness = healthClientService.getBovineIllness(bovine.getTag());
            bovine.setIllness(illness);
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("total-elements", String.valueOf(bovinePage.getTotalElements()));
        responseHeaders.set("total-pages", String.valueOf(bovinePage.getTotalPages()));
        responseHeaders.set("current-page", String.valueOf(bovinePage.getNumber()));

        return ResponseEntity.ok().headers(responseHeaders).body(mappedBovines);
    }

    @ApiOperation(value = "Retrieve a list of bovines from a list of tags", response = Bovine.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully found bovines")
    })
    @PostMapping(value = BASE_URL + "list", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Bovine>> listBovines(
            @ApiParam(value = "Bovines to list. If not found will be ignored")
            @RequestBody List<String> bovineTags) {

        final String userCuig = authorizationService.getCurrentUserCuig();

        LOG.info("Listing bovines {} for cuig {}", userCuig, bovineTags);
        List<CemaBovine> cemaBovines = bovineRepository.findCemaBovinesByTagIn(bovineTags);
        if (!authorizationService.isAdmin()) {
            cemaBovines = cemaBovines.stream()
                    .filter(cemaBovine -> cemaBovine.getEstablishmentCuig().equalsIgnoreCase(userCuig))
                    .collect(Collectors.toList());
        }
        List<Bovine> bovines = cemaBovines.stream().map(bovineMapping::mapEntityToDomain).collect(Collectors.toList());

        for (Bovine bovine : bovines) {
            Illness illness = healthClientService.getBovineIllness(bovine.getTag());
            bovine.setIllness(illness);
        }
        return new ResponseEntity<>(bovines, HttpStatus.OK);
    }


}
