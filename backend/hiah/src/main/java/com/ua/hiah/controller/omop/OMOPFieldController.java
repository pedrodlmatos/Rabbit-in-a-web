package com.ua.hiah.controller.omop;

import com.fasterxml.jackson.annotation.JsonView;
import com.ua.hiah.model.omop.OMOPField;
import com.ua.hiah.service.omop.field.OMOPFieldService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/v1/api/omopField")
public class OMOPFieldController {

    @Autowired
    private OMOPFieldService fieldService;

    private static final Logger logger = LoggerFactory.getLogger(OMOPFieldController.class);

    @Operation(summary = "Change comment of field from the OMOP CDM database")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Changed comment with success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OMOPField.class)
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
    @JsonView(Views.ChangeComment.class)
    public ResponseEntity<?> changeFieldComment(
            @Param(value = "omopFieldId") Long omopFieldId,
            @Param(value = "comment") String comment,
            @Param(value = "etl_id") Long etl_id,
            @Param(value = "username") String username) {
        logger.info("OMOP FIELD CONTROLLER - Change field {} comment", omopFieldId);

        OMOPField response = fieldService.changeComment(omopFieldId, comment, etl_id, username);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
