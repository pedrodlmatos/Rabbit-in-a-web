package com.ua.riah.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.ua.riah.model.ETL;
import com.ua.riah.model.TableMapping;
import com.ua.riah.service.etlService.ETLService;
import com.ua.riah.views.Views;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.hibernate.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/v1/api/etl")
public class ETLController {

    @Autowired
    ETLService etlService;

    private static final Logger logger = LoggerFactory.getLogger(ETLController.class);

    @Operation(summary = "Retrieve all ETL sessions")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "ETL sessions",
                    content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ETL.class)))}
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


    @Operation(summary = "Retrieve a ETL session by its id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Found the ETL session",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ETL.class))}
            ),
            @ApiResponse(
                    responseCode = "404", description = "ETL session not found", content = @Content
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


    @Operation(summary = "Create an ETL session")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201", description = "Creates a new ETL session",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ETL.class))}
            )
    })
    @PostMapping(value = "/sessions", consumes = "multipart/form-data")
    public ResponseEntity<?> createETLSession(@RequestParam("file") MultipartFile file, @Param(value = "cdm") String cdm) {
        ETL etl = etlService.createETLSession(file, cdm);
        logger.info("ETL - Created ETL session with id: " + etl.getId());
        return new ResponseEntity<>(etl, HttpStatus.CREATED);
    }


    @Operation(summary = "Change OMOP CDM version in ETL session")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"
            )
    })
    @PutMapping("/sessions/targetDB")
    public ResponseEntity<?> changeTargetDatabase(@RequestBody ETL etl, @Param(value = "cdm") String cdm) {
        logger.info("ETL - Change target database of session {} to {}", etl.getId(), cdm);

        ETL response = etlService.changeTargetDatabase(etl.getId(), cdm);
        if (response == null) {
            response = new ETL();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/sessions/comment")
    public ResponseEntity<?> changeTableComment(@RequestBody ETL etl, @Param(value = "id") Long tableId, @Param(value = "comment") String comment) {
        logger.info("ETL - Change table {} comment", tableId);

        ETL response = etlService.changeComment(etl.getId(), tableId, comment);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}