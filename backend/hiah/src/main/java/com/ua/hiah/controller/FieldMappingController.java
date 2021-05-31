package com.ua.hiah.controller;

import com.ua.hiah.model.FieldMapping;
import com.ua.hiah.service.fieldMapping.FieldMappingService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/v1/api/fieldMap")
public class FieldMappingController {

    private static final Logger logger = LoggerFactory.getLogger(FieldMappingController.class);

    @Autowired
    private FieldMappingService service;


    @Operation(summary = "Creates a field mapping with a field from the EHR database and other from the OMOP CDM")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Field mapping created with success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FieldMapping.class)
                    )
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
    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createFieldMapping(
            @Param(value = "tableMappingId") Long tableMappingId,
            @Param(value = "sourceFieldId") Long sourceFieldId,
            @Param(value = "targetFieldId") Long targetFieldId,
            @Param(value = "etl_id") Long etl_id,
            @Param(value = "username") String username) {
        logger.info("FIELD MAPPING CONTROLLER - Add field mapping between {} and {} in table mapping {}", sourceFieldId, targetFieldId, tableMappingId);

        FieldMapping response = service.addFieldMapping(sourceFieldId, targetFieldId, tableMappingId, etl_id, username);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }


    @Operation(summary = "Deletes a field mapping")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Field mapping deleted with success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FieldMapping.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User doesn't have access to ETL procedure",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Field mapping not found",
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
    @DeleteMapping("/remove")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> removeFieldMapping(
            @Param(value = "fieldMappingId") Long fieldMappingId,
            @Param(value = "etl_id") Long etl_id,
            @Param(value = "username") String username) {
        logger.info("FIELD MAPPING CONTROLLER - Removed field mapping with id " + fieldMappingId);

        service.removeFieldMapping(fieldMappingId, etl_id, username);
        return ResponseEntity
                .status(HttpStatus.OK).build();
    }


    @Operation(summary = "Change field mapping logic")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Logic changed",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FieldMapping.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User doesn't have access to ETL procedure",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Field mapping not found",
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
    @PutMapping("/map/{id}/logic")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> editMappingLogic(
            @PathVariable Long id,
            @Param(value = "logic") String logic,
            @Param(value = "etl_id") Long etl_id,
            @Param(value = "username") String username) {
        logger.info("FIELD MAPPING - Change mapping logic of mapping " + id);

        FieldMapping response = service.changeMappingLogic(id, logic, etl_id, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
