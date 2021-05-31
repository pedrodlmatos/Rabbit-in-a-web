package com.ua.hiah.controller.source;

import com.ua.hiah.model.ETL;
import com.ua.hiah.model.auth.User;
import com.ua.hiah.model.source.SourceTable;
import com.ua.hiah.security.services.UserDetailsServiceImpl;
import com.ua.hiah.service.etl.ETLService;
import com.ua.hiah.service.source.table.SourceTableService;
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
@RequestMapping("/v1/api/sourceTable")
public class SourceTableController {

    @Autowired
    private SourceTableService tableService;

    private static final Logger logger = LoggerFactory.getLogger(SourceTableController.class);

    @Operation(summary = "Change comment of a table from the EHR database")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Changed comment with success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SourceTable.class)
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
                    description = "SourceTable not found",
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
    public ResponseEntity<?> changeTableComment(
            @Param(value = "table_id") Long table_id,
            @Param(value = "comment") String comment,
            @Param(value = "username") String username,
            @Param(value = "etl_id") Long etl_id) {
        logger.info("SOURCE TABLE CONTROLLER - Change table {} comment", table_id);

        SourceTable response = tableService.changeComment(table_id, comment, etl_id, username);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
