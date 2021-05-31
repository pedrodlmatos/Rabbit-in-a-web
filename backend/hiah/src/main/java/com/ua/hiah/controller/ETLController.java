package com.ua.hiah.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.ua.hiah.model.ETL;
import com.ua.hiah.model.auth.User;
import com.ua.hiah.security.services.UserDetailsServiceImpl;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

// TODO: create ETL session with a custom OMOP CDM file

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/v1/api/etl")
public class ETLController {

    @Autowired
    ETLService etlService;

    @Autowired
    UserDetailsServiceImpl userService;


    private static final Logger logger = LoggerFactory.getLogger(ETLController.class);


    @Operation(summary = "Retrieve all ETL sessions")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "If user is an ADMIN, retrieves a list with all ETL procedures created and deleted",
                    content = { @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ETL.class))
                    )}
            )
    })
    @GetMapping("/procedures")
    @JsonView(Views.AdminETLProcedureList.class)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllETLs() {
        logger.info("ETL CONTROLLER - Requesting all ETL procedures");

        // retrieve all ETL procedures from repository
        List<ETL> response = etlService.getAllETL();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }


    @Operation(summary = "Retrieve all ETL sessions of a given user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Retrieves the ETL procedures which user has access to and are not deleted",
                    content = { @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ETL.class))
                    )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            )
    })
    @GetMapping("/user_procedures")
    @JsonView(Views.ETLSessionsList.class)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getUserETLs(@Param("username") String username) {
        logger.info("ETL CONTROLLER - Requesting ETL procedures of user " + username);

        List<ETL> response = etlService.getETLByUsername(username);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }


    @Operation(summary = "Retrieve a ETL procedure by its id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "If user is ADMIN or has access to ETL procedure, retrieves it",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ETL.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "ETL procedure not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User doesn't have access to ETL procedure",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error in request",
                    content = @Content
            )
    })
    @GetMapping("/procedures/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @JsonView(Views.ETLSession.class)
    public ResponseEntity<?> getETLById(@PathVariable Long id, @Param(value = "username") String username) {
        logger.info("ETL CONTROLLER - Requesting ETL procedure with id " + id);

        ETL response = etlService.getETLWithId(id, username);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }


    @Operation(summary = "Create an ETL procedure from a Scan report (generated by White-Rabbit)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "ETL procedure created with success",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ETL.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "OMOP CDM version not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal error",
                    content = @Content
            )
    })
    @PostMapping(value = "/procedures", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createETLProcedure(
            @Param(value = "name") String name,
            @RequestParam("file") MultipartFile file,
            @Param(value = "cdm") String cdm,
            @Param(value = "username") String username) {
        logger.info("ETL CONTROLLER - Creating new ETL procedure");

        ETL response = etlService.createETLProcedure(name, file, cdm, username);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }


    @Operation(summary = "Creates an ETL procedure from a JSON summary file (generated by backend)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "ETL procedure created with success",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ETL.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal error",
                    content = @Content
            )
    })
    @PostMapping(value = "/procedures/save", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createETLSessionFromFile(@RequestParam("file") MultipartFile file, @Param("username") String username) {
        logger.info("ETL CONTROLLER - Creating ETL session from file");

        ETL response = etlService.createETLProcedureFromFile(file, username);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }


    @Operation(summary = "Deletes and ETL procedure (only possible if user is ADMIN)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "ETL procedure deleted",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "ETL procedure not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal error",
                    content = @Content
            )
    })
    @DeleteMapping("/procedures")
    @JsonView(Views.ETLSessionsList.class)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteETLProcedure(@Param("etl_id") Long etl_id) {
        logger.info("ETL CONTROLLER - Deleting ETL procedure with id " + etl_id);

        etlService.deleteETLProcedure(etl_id);
        return ResponseEntity
                .status(HttpStatus.OK).build();
    }


    @Operation(summary = "Marks an ETL procedure as deleted (made by USER)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "ETL procedure altered with success",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "ETL procedure not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal error",
                    content = @Content
            )
    })
    @PutMapping("/procedures_del")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> markProcedureAsDeleted(@Param("etl_id") Long etl_id, @Param("username") String username) {
        logger.info("ETL CONTROLLER - Mark as deleted ETL procedure with id " + etl_id);

        etlService.markAsDeleted(etl_id, username);
        return ResponseEntity
                .status(HttpStatus.OK).build();
    }


    @Operation(summary = "Marks an ETL procedure as not deleted (made by ADMIN)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "ETL procedures altered with success",
                    content = { @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ETL.class))
                    )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "ETL procedure not found",
                    content = @Content
            )
    })
    @PutMapping("/procedures_undel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> unmarkProcedureAsDeleted(@Param("etl_id") Long etl_id) {
        logger.info("ETL CONTROLLER - Mark as not deleted ETL procedure with id " + etl_id);

        etlService.markAsNotDeleted(etl_id);
        return ResponseEntity
                .status(HttpStatus.OK).build();
    }


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
                    responseCode = "401",
                    description = "User doesn't have access to ETL procedure",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "ETL procedure not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "OMOP CDM not valid",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal error",
                    content = @Content
            )
    })
    @PutMapping("/procedures/targetDB")
    @JsonView(Views.ETLSession.class)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> changeTargetDatabase(
            @Param(value = "username") String username,
            @Param(value = "etl_id") Long etl_id,
            @Param(value = "cdm") String cdm) {
        logger.info("ETL CONTROLLER - Change target database of procedure {} to {}", etl_id, cdm);

        ETL response = etlService.changeTargetDatabase(etl_id, cdm, username);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }


    @Operation(summary = "Add stem tables on both databases")
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
                    responseCode = "401",
                    description = "User doesn't have access to ETL procedure",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "ETL not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal error",
                    content = @Content
            )
    })
    @PutMapping("/procedures/stem")
    @JsonView(Views.ETLSession.class)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addStemTables(@Param(value = "etl_id") Long etl_id, @Param(value = "username") String username) {
        logger.info("ETL CONTROLLER - Add stem tables on procedure {}", etl_id);

        ETL response = etlService.addStemTable(etl_id, username);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }


    @Operation(summary = "Remove stem tables from both databases")
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
                    responseCode = "401",
                    description = "User doesn't have access to ETL procedure",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "ETL not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal error",
                    content = @Content
            ),
    })
    @PutMapping("/procedures/remove_stem")
    @JsonView(Views.ETLSession.class)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> removeStemTables(@Param(value = "etl_id") Long etl_id, @Param(value = "username") String username) {
        logger.info("ETL CONTROLLER - Remove stem tables on procedure {}", etl_id);

        ETL response = etlService.removeStemTable(etl_id, username);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }


    @Operation(summary = "Generates the JSON file containing all ETL procedure information")
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
                    responseCode = "401",
                    description = "User doesn't have access to ETL procedure",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "ETL not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "ETL procedure not found",
                    content = @Content
            )
    })
    @GetMapping(value = "/procedures/save")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getSaveFile(@Param(value = "etl_id") Long etl_id, @Param(value = "username") String username) {
        logger.info("ETL CONTROLLER - Download save file of session {}", etl_id);

        byte[] content = etlService.createSavingFile(etl_id, username);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setContentLength(content.length);
        headers.set("Content-Disposition", "attachment; filename=Scan.json");
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(content);
    }


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
                    responseCode = "401",
                    description = "User doesn't have access to ETL procedure",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "ETL not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal error",
                    content = @Content
            )
    })
    @GetMapping(value = "/procedures/sourceCSV")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getSourceFieldListCSV(@Param(value = "etl_id") Long etl_id, @Param(value = "username") String username) {
        logger.info("ETL CONTROLLER - Download source field list CSV of session {}", etl_id);

        byte[] content = etlService.createSourceFieldListCSV(etl_id, username);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.parseMediaType("application/csv"));
        header.setContentDispositionFormData("sourceList.csv", "sourceList.csv");
        header.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(header)
                .body(content);
    }


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
                    responseCode = "401",
                    description = "User doesn't have access to ETL procedure",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "ETL not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal error",
                    content = @Content
            )
    })
    @GetMapping(value = "/procedures/targetCSV")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getTargetFieldListCSV(@Param(value = "etl_id") Long etl_id, @Param(value = "username") String username) {
        logger.info("ETL CONTROLLER - Download target field list CSV of session {}", etl_id);

        byte[] content = etlService.createTargetFieldListCSV(etl_id, username);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.parseMediaType("application/csv"));
        header.setContentDispositionFormData("targetList.csv", "targetList.csv");
        header.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(header)
                .body(content);
    }


    @Operation(summary = "Gets the Word document containing ETL procedure summary")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Retrieves the summary file of a given ETL procedure",
                    content = { @Content(
                            mediaType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                            schema = @Schema(allOf = byte[].class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User doesn't have access to ETL procedure",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "ETL not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal error",
                    content = @Content
            )
    })
    @GetMapping(value = "/procedures/summary", produces="application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getWordDocument(@Param(value = "etl_id") Long etl_id, @Param(value = "username") String username) {
        logger.info("ETL CONTROLLER - Download word summary file of procedure {}", etl_id);

        byte[] response = etlService.createWordSummaryFile(etl_id, username);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment;filename=hero.docx");
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(response);
    }
}

