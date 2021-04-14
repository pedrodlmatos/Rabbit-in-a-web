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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/v1/api/fieldMap")
public class FieldMappingController {

    private static final Logger logger = LoggerFactory.getLogger(FieldMappingController.class);

    @Autowired
    private FieldMappingService service;

    /**
     * Creates a field mapping
     *
     * @param tableMap Table mapping id
     * @param source_id Source field id
     * @param target_id Target field id
     * @return created field mapping or error
     */

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
                    responseCode = "404",
                    description = "Table mapping not found",
                    content = @Content
            )
    })
    @PostMapping("/create")
    public ResponseEntity<?> createFieldMapping(@Param(value = "tableMap") Long tableMap, @Param(value = "source_id") Long source_id, @Param(value = "target_id") Long target_id) {
        logger.info("FIELD MAPPING CONTROLLER - Add field mapping between {} and {} in table mapping {}", source_id, target_id, tableMap);
        FieldMapping response = service.addFieldMapping(source_id, target_id, tableMap);
        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /**
     * Removes a field mapping given its id
     *
     * @param tableMappingId Table mapping id
     * @param fieldMappingId Field mapping id
     * @return list with other field mappings
     */

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
                    responseCode = "404",
                    description = "Field mapping not found",
                    content = { @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = FieldMapping.class))
                    )}
            )
    })
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFieldMapping(@Param(value="tableMappingId") Long tableMappingId, @Param(value="fieldMappingId") Long fieldMappingId) {
        logger.info("FIELD MAPPING CONTROLLER - Removed field mapping with id " + fieldMappingId);
        FieldMapping response = service.removeFieldMapping(fieldMappingId);

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        List<FieldMapping> res = service.getFieldMappingsFromTableMapping(tableMappingId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }


    /**
     * Changes the logic of a field mapping
     *
     * @param id field mapping id
     * @param logic field mapping logic
     * @return field mapping altered
     */

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
                    responseCode = "404",
                    description = "Table mapping not found",
                    content = @Content
            )
    })
    @PutMapping("/map/{id}/logic")
    public ResponseEntity<?> editMappingLogic(@PathVariable Long id, @Param(value = "logic") String logic) {
        logger.info("FIELD MAPPING - Change mapping logic of mapping " + id);
        FieldMapping response = service.changeMappingLogic(id, logic);

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
