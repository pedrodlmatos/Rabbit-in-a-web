package com.ua.hiah.controller.source;

import com.ua.hiah.model.source.SourceField;
import com.ua.hiah.service.source.field.SourceFieldService;
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
@RequestMapping("/v1/api/sourceField")
public class SourceFieldController {

    @Autowired
    private SourceFieldService fieldService;

    private static final Logger logger = LoggerFactory.getLogger(SourceFieldController.class);


    @Operation(summary = "Change the comment of a field from the EHR database")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Comment changed with success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SourceField.class)
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
                    description = "Source Field not found",
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
        logger.info("SOURCE FIELD CONTROLLER - Change field {} comment", fieldId);

        SourceField response = fieldService.changeComment(fieldId, comment, etl_id, username);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
