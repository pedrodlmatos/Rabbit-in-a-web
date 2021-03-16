package com.ua.hiah.controller;

import com.ua.hiah.model.FieldMapping;
import com.ua.hiah.service.fieldMapping.FieldMappingService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/fieldMap")
public class FieldMappingController {

    private Logger logger = LoggerFactory.getLogger(FieldMappingController.class);

    @Autowired
    private FieldMappingService service;

    /**
     * Creates a field mapping
     *
     * @param tableMap Table mapping id
     * @param source_id Source field id
     * @param target_id Target field id
     * @return created field mapping
     */

    @Operation(summary = "Creates a field mapping")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Created field mapping",
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
        FieldMapping response = service.addFieldMapping(source_id, target_id, tableMap);
        if (response == null) {
            response = new FieldMapping();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        logger.info("FIELD MAPPING - Add field mapping between {} and {} in table mapping {}", source_id, target_id, tableMap);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /**
     * Removes a field mapping
     *
     * @param tableMappingId Table mapping id
     * @param fieldMappingId Field mapping id
     * @return list with other field mappings
     */
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFieldMapping(@Param(value="tableMappingId") Long tableMappingId, @Param(value="fieldMappingId") Long fieldMappingId) {
        FieldMapping response = service.removeFieldMapping(fieldMappingId);

        if (response == null) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        logger.info("FIELD MAPPING - Removed field mapping with id " + fieldMappingId);

        List<FieldMapping> res = service.getFieldMappingsFromTableMapping(tableMappingId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
