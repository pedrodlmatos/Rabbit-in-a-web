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
import org.springframework.web.bind.annotation.*;
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
     * Retrieves a list with all ETL sessions
     *
     * @return ETL sessions
     */

    @Operation(summary = "Retrieve all ETL sessions")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "ETL sessions returned",
                    content = { @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ETL.class))
                    )}
            )
    })
    @GetMapping("/sessions")
    @JsonView(Views.ETLSessionsList.class)
    public ResponseEntity<?> getAllETLs() {
        logger.info("ETL CONTROLLER - Requesting all ETL sessions");

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

    @Operation(summary = "Retrieve a ETL session by its id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found the ETL session",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ETL.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "ETL session not found",
                    content = @Content
            )
    })
    @GetMapping("/sessions/{id}")
    @JsonView(Views.ETLSession.class)
    public ResponseEntity<?> getETLById(@PathVariable Long id) {
        logger.info("ETL CONTROLLER - Requesting ETL session with id " + id);

        ETL response = etlService.getETLWithId(id);

        if (response == null) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /**
     * Creates an ETL session
     *
     * @param file file created by White Rabbit that contains info about EHR database
     * @param cdm OMOP CDM version to use
     * @return created session or error
     */

    @Operation(summary = "Create an ETL session")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "ETL session created with success",
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
    public ResponseEntity<?> createETLSession(@Param(value = "name") String name, @RequestParam("file") MultipartFile file, @Param(value = "cdm") String cdm) {
        ETL etl = etlService.createETLSession(name, file, cdm);

        if (etl == null) {
            return new ResponseEntity<>(etl, HttpStatus.BAD_REQUEST);
        }

        logger.info("ETL CONTROLLER - Created ETL session with id: " + etl.getId());
        return new ResponseEntity<>(etl, HttpStatus.CREATED);
    }

    // TODO: create ETL session with a custom OMOP CDM file

    /**
     * Changes the OMOP CDM version in a given ETL session
     *
     * @param etl ETL session's id
     * @param cdm OMOP CDM version to change to
     * @return altered ETL or error
     */

    @Operation(summary = "Change OMOP CDM version in ETL session")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "ETL session changed with success",
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
        logger.info("ETL CONTROLLER - Change target database of session {} to {}", etl, cdm);

        ETL response = etlService.changeTargetDatabase(etl, cdm);
        if (response == null) {
            response = new ETL();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    // TODO
    @GetMapping(value = "/sessions/sourceCSV")
    public ResponseEntity<?> getSourceFieldListCSV(@Param(value = "etl") Long etl) {
        logger.info("ETL {} - Download source field list CSV", etl);

        byte[] content = etlService.createSourceFieldListCSV(etl);
        String filename = "sourceList.csv";

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.parseMediaType("application/csv"));
        header.setContentDispositionFormData(filename, filename);
        header.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<byte[]>(content, header, HttpStatus.OK);

    }
}
