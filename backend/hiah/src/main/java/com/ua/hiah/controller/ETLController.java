package com.ua.hiah.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.ua.hiah.model.ETL;
import com.ua.hiah.service.etl.ETLService;
import com.ua.hiah.views.Views;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/v1/api/etl")
public class ETLController {

    @Autowired
    ETLService etlService;

    private static final Logger logger = LoggerFactory.getLogger(ETLController.class);

    /**
     * Retrieves a list with all ETL procedures
     *
     * @return ETL procedures
     */

    @Operation(summary = "Retrieve all ETL sessions")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "ETL procedures returned",
                    content = { @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ETL.class))
                    )}
            )
    })
    @GetMapping("/sessions")
    @JsonView(Views.ETLSessionsList.class)
    public ResponseEntity<?> getAllETLs() {
        logger.info("ETL CONTROLLER - Requesting all ETL procedures");
        List<ETL> response = etlService.getAllETL();
        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Gets an ETL session given its id
     *
     * @param id ETL session's id
     * @return ETL session
     */

    @Operation(summary = "Retrieve a ETL procedure by its id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found the ETL procedure",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ETL.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "ETL procedure not found",
                    content = @Content
            )
    })
    @GetMapping("/sessions/{id}")
    @JsonView(Views.ETLSession.class)
    public ResponseEntity<?> getETLById(@PathVariable Long id) {
        logger.info("ETL CONTROLLER - Requesting ETL procedure with id " + id);
        ETL response = etlService.getETLWithId(id);
        if (response == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /**
     * Creates an ETL procedure with Scan Report
     *
     * @param file file created by White Rabbit that contains info about EHR database
     * @param cdm OMOP CDM version to use
     * @return created procedure or error
     */

    @Operation(summary = "Create an ETL procedure")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "ETL procedure created with success",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ETL.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "OMOP CDM not valid",
                    content = @Content
            )
    })
    @PostMapping(value = "/sessions", consumes = "multipart/form-data")
    public ResponseEntity<?> createETLProcedure(@Param(value = "name") String name, @RequestParam("file") MultipartFile file, @Param(value = "cdm") String cdm) {
        logger.info("ETL CONTROLLER - Creating new ETL procedure");
        ETL etl = etlService.createETLProcedure(name, file, cdm);
        if (etl == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(etl.getId(), HttpStatus.CREATED);
    }


    /**
     * Creates an ETL procedure from a JSON file that contains a summary of an ETL procedure
     *
     * @param file JSON summary file
     * @return ETL procedure created, error if file is invalid
     */

    @Operation(summary = "Creates an ETL procedure from a JSON summary file")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "ETL procedure created with success",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ETL.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Invalid file",
                    content = @Content
            )
    })
    @PostMapping(value = "/sessions/save", consumes = "multipart/form-data")
    public ResponseEntity<?> createETLSessionFromFile(@RequestParam("file") MultipartFile file) {
        logger.info("ETL CONTROLLER - Creating ETL session from file");
        ETL etl = etlService.createETLSessionFromFile(file);

        if (etl == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(etl, HttpStatus.CREATED);
    }

    // TODO: create ETL session with a custom OMOP CDM file

    /**
     * Changes the OMOP CDM version in a given ETL procedure
     *
     * @param etl ETL procedure's id
     * @param cdm OMOP CDM version to change to
     * @return altered ETL or error
     */

    @Operation(summary = "Change OMOP CDM version in ETL procedure")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "ETL procedure changed with success",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ETL.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "OMOP CDM not valid",
                    content = @Content
            )
    })
    @PutMapping("/sessions/targetDB")
    @JsonView(Views.ETLSession.class)
    public ResponseEntity<?> changeTargetDatabase(@Param(value = "etl") Long etl, @Param(value = "cdm") String cdm) {
        logger.info("ETL CONTROLLER - Change target database of procedure {} to {}", etl, cdm);
        ETL response = etlService.changeTargetDatabase(etl, cdm);
        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /**
     * Gets the file containing all source field and its attributes and relations
     *
     * @param etl ETL procedure's id
     * @return source fields file
     */

    @Operation(summary = "Gets file of the source fields")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Retrieves the source fields file of a given ETL procedure",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(allOf = byte[].class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "ETL procedure not found",
                    content = @Content
            )
    })
    @GetMapping(value = "/sessions/sourceCSV")
    public ResponseEntity<?> getSourceFieldListCSV(@Param(value = "etl") Long etl) {
        logger.info("ETL CONTROLLER - Download source field list CSV of session {}", etl);

        byte[] content = etlService.createSourceFieldListCSV(etl);

        if (content == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        String filename = "sourceList.csv";
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.parseMediaType("application/csv"));
        header.setContentDispositionFormData(filename, filename);
        header.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(content, header, HttpStatus.OK);

    }


    /**
     * Gets the file containing all target fields and its attributes and relations
     *
     * @param etl ETL procedure's id
     * @return target fields file
     */

    @Operation(summary = "Gets file of the target fields")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Retrieves the target fields file of a given ETL procedure",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(allOf = byte[].class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "ETL procedure not found",
                    content = @Content
            )
    })
    @GetMapping(value = "/sessions/targetCSV")
    public ResponseEntity<?> getTargetFieldListCSV(@Param(value = "etl") Long etl) {
        logger.info("ETL CONTROLLER - Download target field list CSV of session {}", etl);

        byte[] content = etlService.createTargetFieldListCSV(etl);
        if (content == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        String filename = "targetList.csv";
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.parseMediaType("application/csv"));
        header.setContentDispositionFormData(filename, filename);
        header.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(content, header, HttpStatus.OK);
    }


    /**
     * Retrieves the JSON file of an ETL procedure
     *
     * @param etl ETL procedure's id
     * @return ETL summary JSON file, error if not found
     */

    @Operation(summary = "Gets the JSON file containing all ETL procedure information")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Retrieves the JSON file of a given ETL procedure",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(allOf = byte[].class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "ETL procedure not found",
                    content = @Content
            )
    })
    @GetMapping(value = "/sessions/save")
    public ResponseEntity<?> getSaveFile(@Param(value = "etl") Long etl) {
        logger.info("ETL CONTROLLER - Download save file of session {}", etl);

        byte[] content = etlService.createSavingFile("Scan.json", etl);
        if (content == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        header.setContentLength(content.length);
        header.set("Content-Disposition", "attachment; filename=Scan.json");
        return new ResponseEntity<>(content, header, HttpStatus.OK);
    }
}
