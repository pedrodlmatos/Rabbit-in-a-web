package com.ua.riaw.etlProcedure.source.ehrField;

import com.fasterxml.jackson.annotation.JsonView;
import com.ua.riaw.utils.views.Views;
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
@RequestMapping("/v1/api/ehrField")
public class EHRFieldController {

    @Autowired
    private EHRFieldService fieldService;

    private static final Logger logger = LoggerFactory.getLogger(EHRFieldController.class);


    @Operation(summary = "Change the comment of a field from the EHR database")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Comment changed with success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EHRField.class)
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
    @JsonView(Views.ChangeComment.class)
    public ResponseEntity<?> changeFieldComment(
            @Param(value = "ehrFieldId") Long ehrFieldId,
            @Param(value = "comment") String comment,
            @Param(value = "etl_id") Long etl_id,
            @Param(value = "username") String username) {
        logger.info("EHR FIELD CONTROLLER - Change field {} comment", ehrFieldId);

        EHRField response = fieldService.changeComment(ehrFieldId, comment, etl_id, username);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
