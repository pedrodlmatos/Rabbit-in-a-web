package com.ua.hiah.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.ua.hiah.model.ETL;
import com.ua.hiah.service.etlService.ETLService;
import com.ua.hiah.views.Views;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import javassist.bytecode.ByteArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
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
                    description = "ETL sessions",
                    content = { @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ETL.class))
                    )}
            )
    })
    @GetMapping("/sessions")
    @JsonView(Views.ETLSessionsList.class)
    public ResponseEntity<?> getAllETLs() {
        logger.info("ETL - Requesting all ETL sessions");

        List<ETL> response = etlService.getAllETL();

        if (response == null)
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);

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
        logger.info("ETL - Requesting ETL session with id " + id);

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
     * @return 201 code with ETL session created
     */

    @Operation(summary = "Create an ETL session")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Creates a new ETL session",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ETL.class)
                    )}
            )
    })
    @PostMapping(value = "/sessions", consumes = "multipart/form-data")
    public ResponseEntity<?> createETLSession(@RequestParam("file") MultipartFile file, @Param(value = "cdm") String cdm) {
        ETL etl = etlService.createETLSession(file, cdm);
        logger.info("ETL - Created ETL session with id: " + etl.getId());
        return new ResponseEntity<>(etl, HttpStatus.CREATED);
    }


    /**
     *
     * @param etl
     * @param cdm
     * @return
     */

    @Operation(summary = "Change OMOP CDM version in ETL session")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"
            )
    })
    @PutMapping("/sessions/targetDB")
    public ResponseEntity<?> changeTargetDatabase(@Param(value = "etl") Long etl, @Param(value = "cdm") String cdm) {
        logger.info("ETL - Change target database of session {} to {}", etl, cdm);

        ETL response = etlService.changeTargetDatabase(etl, cdm);
        if (response == null) {
            response = new ETL();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /**
     * Change table comment
     *
     * @param etl ETL session id
     * @param table Table id
     * @param comment comment to change to
     * @return altered ETL session
     */

    @Operation(summary = "Change table comment")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Changed table comment",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ETL.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found",
                    content = @Content
            )
    })
    @PutMapping("/sessions/comment")
    public ResponseEntity<?> changeTableComment(@Param(value = "etl") Long etl, @Param(value = "table") Long table, @Param(value = "comment") String comment) {
        logger.info("ETL {} - Change table {} comment", etl, table);

        ETL response = etlService.changeComment(etl, table, comment);
        if (response == null) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


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