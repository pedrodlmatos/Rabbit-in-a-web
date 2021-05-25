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
                    description = "ETL procedures returned",
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
        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Operation(summary = "Retrieve all ETL sessions of a given user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "ETL procedures returned",
                    content = { @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ETL.class))
                    )}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "User not found",
                    content = @Content
            )
    })
    @GetMapping("/user_procedures")
    @JsonView(Views.ETLSessionsList.class)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getUserETLs(@Param("username") String username) {
        logger.info("ETL CONTROLLER - Requesting ETL procedures of user " + username);

        User user = userService.getUserByUsername(username);
        if (user != null) {
            List<ETL> response = etlService.getETLByUsername(user);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }


    @Operation(summary = "Deletes and ETL procedures")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "ETL procedure deleted",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "ETL procedure not found",
                    content = @Content
            )
    })
    @DeleteMapping("/procedures")
    @JsonView(Views.ETLSessionsList.class)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteETLProcedure(@Param("etl_id") Long etl_id) {
        logger.info("ETL CONTROLLER - Deleting ETL procedure with id " + etl_id);

        ETL etl = etlService.deleteETLProcedure(etl_id);
        if (etl != null) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }


    @Operation(summary = "Marks an ETL procedure as deleted (made by USER)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "ETL procedures changed",
                    content = { @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ETL.class))
                    )}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "User not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "ETL procedure not found",
                    content = @Content
            )
    })
    @PutMapping("/procedures_del")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> markProcedureAsDeleted(@Param("etl_id") Long etl_id, @Param("username") String username) {
        logger.info("ETL CONTROLLER - Mark as deleted ETL procedure with id " + etl_id);

        User user = userService.getUserByUsername(username);
        if (user != null) {
            ETL etl = etlService.getETLWithId(etl_id);

            if (etl != null && etlService.userHasAccessToEtl(etl, user)) {
                etlService.markAsDeleted(etl);
                return new ResponseEntity<>(HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }


    @Operation(summary = "Marks an ETL procedure as not deleted (made by ADMIN)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "ETL procedures changed",
                    content = { @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ETL.class))
                    )}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "User not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "ETL procedure not found",
                    content = @Content
            )
    })
    @PutMapping("/procedures_undel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> unmarkProcedureAsDeleted(@Param("etl_id") Long etl_id) {
        logger.info("ETL CONTROLLER - Mark as not deleted ETL procedure with id " + etl_id);

        ETL etl = etlService.getETLWithId(etl_id);

        if (etl != null) {
            etlService.markAsNotDeleted(etl);
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }


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

        User user = userService.getUserByUsername(username);
        ETL response = etlService.getETLWithId(id);
        if (user != null && response != null) {
            boolean isAdmin = userService.userIsAdmin(user);
            boolean etlUser = etlService.userHasAccessToEtl(response, user);

            // user is admin or has permission to access etl procedure
            if (isAdmin || etlUser) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }

        // etl procedure or user not found or user without access
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }


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
    @PostMapping(value = "/procedures", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createETLProcedure(
            @Param(value = "name") String name,
            @RequestParam("file") MultipartFile file,
            @Param(value = "cdm") String cdm,
            @Param(value = "username") String username) {
        logger.info("ETL CONTROLLER - Creating new ETL procedure");

        User user = userService.getUserByUsername(username);
        if (user != null) {
            ETL etl = etlService.createETLProcedure(name, file, cdm, user);
            if (etl != null)
                return new ResponseEntity<>(etl.getId(), HttpStatus.CREATED);

        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }


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
    @PostMapping(value = "/procedures/save", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createETLSessionFromFile(@RequestParam("file") MultipartFile file, @Param("username") String username) {
        logger.info("ETL CONTROLLER - Creating ETL session from file");

        User user = userService.getUserByUsername(username);
        if (user != null) {
            ETL etl = etlService.createETLProcedureFromFile(file, user);
            if (etl != null)
                return new ResponseEntity<>(etl.getId(), HttpStatus.CREATED);
        }

        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
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
                    responseCode = "500",
                    description = "OMOP CDM not valid",
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

        User user = userService.getUserByUsername(username);
        if (user == null)
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        ETL etl = etlService.getETLWithId(etl_id);
        if (etl == null)
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        if (etlService.userHasAccessToEtl(etl, user)) {
            ETL response = etlService.changeTargetDatabase(etl_id, cdm);
            if (response == null)
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }


    @Operation(summary = "Add stem table on EHR and OMOP CDM databases")
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
                    description = "ETL not found",
                    content = @Content
            )
    })
    @PutMapping("/procedures/stem")
    @JsonView(Views.ETLSession.class)
    public ResponseEntity<?> addStemTables(@Param(value = "etl") Long etl) {
        logger.info("ETL CONTROLLER - Add stem tables on procedure {}", etl);
        ETL response = etlService.addStemTable(etl);
        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Operation(summary = "Remove stem table on EHR and OMOP CDM databases")
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
                    description = "ETL not found",
                    content = @Content
            )
    })
    @PutMapping("/procedures/remove_stem")
    @JsonView(Views.ETLSession.class)
    public ResponseEntity<?> removeStemTables(@Param(value = "etl") Long etl) {
        logger.info("ETL CONTROLLER - Remove stem tables on procedure {}", etl);
        ETL response = etlService.removeStemTable(etl);
        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(response, HttpStatus.OK);
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
                    responseCode = "500",
                    description = "ETL procedure not found",
                    content = @Content
            )
    })
    @GetMapping(value = "/procedures/sourceCSV")
    public ResponseEntity<?> getSourceFieldListCSV(@Param(value = "etl") Long etl) {
        logger.info("ETL CONTROLLER - Download source field list CSV of session {}", etl);

        byte[] content = etlService.createSourceFieldListCSV(etl);

        if (content == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.parseMediaType("application/csv"));
        header.setContentDispositionFormData("sourceList.csv", "sourceList.csv");
        header.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(content, header, HttpStatus.OK);
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
                    responseCode = "500",
                    description = "ETL procedure not found",
                    content = @Content
            )
    })
    @GetMapping(value = "/procedures/targetCSV")
    public ResponseEntity<?> getTargetFieldListCSV(@Param(value = "etl") Long etl) {
        logger.info("ETL CONTROLLER - Download target field list CSV of session {}", etl);

        byte[] content = etlService.createTargetFieldListCSV(etl);
        if (content == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.parseMediaType("application/csv"));
        header.setContentDispositionFormData("targetList.csv", "targetList.csv");
        header.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(content, header, HttpStatus.OK);
    }


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
    @GetMapping(value = "/procedures/save")
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
                    responseCode = "500",
                    description = "ETL procedure not found",
                    content = @Content
            )
    })
    @GetMapping(value = "/procedures/summary", produces="application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    public ResponseEntity<?> getWordDocument(@Param(value = "etl") Long etl) {
        logger.info("ETL CONTROLLER - Download word summary file of procedure {}", etl);

        byte[] response = etlService.createWordSummaryFile(etl);
        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment;filename=hero.docx");
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }
}
