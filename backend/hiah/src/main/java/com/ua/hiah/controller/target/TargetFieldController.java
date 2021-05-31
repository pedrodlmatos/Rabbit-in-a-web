package com.ua.hiah.controller.target;

import com.ua.hiah.model.target.TargetField;
import com.ua.hiah.service.target.field.TargetFieldService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/v1/api/targetField")
public class TargetFieldController {

    @Autowired
    private TargetFieldService fieldService;

    private static final Logger logger = LoggerFactory.getLogger(TargetFieldController.class);

    @Operation(summary = "Change comment of field from the OMOP CDM database")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Changed comment with success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TargetField.class)
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
                    responseCode = "404",
                    description = "Target Table not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal error",
                    content = @Content
            )
    })
    @PutMapping("/comment")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> changeFieldComment(
            @Param(value = "fieldId") Long fieldId,
            @Param(value = "comment") String comment,
            @Param(value = "etl_id") Long etl_id,
            @Param(value = "username") String username) {
        logger.info("TARGET FIELD CONTROLLER - Change field {} comment", fieldId);

        TargetField response = fieldService.changeComment(fieldId, comment, etl_id, username);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
