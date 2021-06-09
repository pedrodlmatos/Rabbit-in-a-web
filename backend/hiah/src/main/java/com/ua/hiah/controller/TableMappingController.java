package com.ua.hiah.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.ua.hiah.error.exceptions.UnauthorizedAccessException;
import com.ua.hiah.model.ETL;
import com.ua.hiah.model.TableMapping;
import com.ua.hiah.model.auth.User;
import com.ua.hiah.security.services.UserDetailsServiceImpl;
import com.ua.hiah.service.etl.ETLService;
import com.ua.hiah.service.tableMapping.TableMappingService;
import com.ua.hiah.views.Views;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/v1/api/tableMap")
public class TableMappingController {

    private static final Logger logger = LoggerFactory.getLogger(TableMappingController.class);

    @Autowired
    private TableMappingService service;

    @Autowired
    private ETLService etlService;


    @Operation(summary = "Retrieves a table mapping")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Table mapping found",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TableMapping.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Table mapping not found",
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
                    responseCode = "500",
                    description = "Internal error",
                    content = @Content
            )
    })
    @GetMapping("/map/{tableMappingId}")
    @JsonView(Views.TableMapping.class)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getTableMapping(@PathVariable Long tableMappingId, @Param(value = "etl_id") Long etl_id, @Param(value = "username") String username) {
        logger.info("TABLE MAPPING CONTROLLER - Requesting table mapping with id " + tableMappingId);

        TableMapping response = service.getTableMappingById(tableMappingId, etl_id, username);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @Operation(summary = "Creates a table mapping")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Table mapping created",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TableMapping.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Table mapping not found",
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
                    responseCode = "500",
                    description = "Internal error",
                    content = @Content
            )
    })
    @PostMapping("/map")
    @PreAuthorize("hasRole('USER')")
    @JsonView(Views.CreateMapping.class)
    public ResponseEntity<?> createTableMapping(
            @Param(value = "source_id") Long source_id,
            @Param(value = "target_id") Long target_id,
            @Param(value = "username") String username,
            @Param(value = "elt_id") Long etl_id) {
        logger.info("TABLE MAPPING CONTROLLER - Add table mapping between {} and {} in session {}", source_id, target_id, etl_id);

        TableMapping response = service.addTableMapping(source_id, target_id, etl_id, username);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }


    @Operation(summary = "Deletes a table mapping")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Table mapping deleted",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ETL.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Table mapping not found",
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
                    responseCode = "500",
                    description = "Internal error",
                    content = @Content
            )
    })
    @DeleteMapping("/map")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> removeTableMapping(
            @Param(value = "map_id") Long map_id,
            @Param(value = "etl_id") Long etl_id,
            @Param(value = "username") String username) {
        logger.info("TABLE MAPPING CONTROLLER - Removed table mapping with id " + map_id);

        service.removeTableMapping(map_id, etl_id, username);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }


    @Operation(summary = "Change table mapping completion status")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Changed completion status",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TableMapping.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Table mapping not found",
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
                    responseCode = "500",
                    description = "Internal error",
                    content = @Content
            )
    })
    @PutMapping("/map/{map_id}/complete")
    @JsonView(Views.ChangeCompletion.class)
    public ResponseEntity<?> editCompleteMapping(
            @PathVariable Long map_id,
            @Param(value = "completion") boolean completion,
            @Param(value = "etl_id") Long etl_id,
            @Param(value = "username") String username) {
        logger.info("TABLE MAPPING CONTROLLER - Change completion status of mapping " + map_id);

        TableMapping response = service.changeCompletionStatus(map_id, completion, etl_id, username);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }


    @Operation(summary = "Change table mapping logic")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Logic changed",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TableMapping.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Table mapping not found",
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
                    responseCode = "500",
                    description = "Internal error",
                    content = @Content
            )
    })
    @PutMapping("/map/{map_id}/logic")
    @JsonView(Views.ChangeLogic.class)
    public ResponseEntity<?> editMappingLogic(
            @PathVariable Long map_id,
            @Param(value = "logic") String logic,
            @Param(value = "etl_id") Long etl_id,
            @Param(value = "username") String username) {
        logger.info("TABLE MAPPING - Change mapping logic of mapping " + map_id);

        TableMapping response = service.changeMappingLogic(map_id, logic, etl_id, username);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
