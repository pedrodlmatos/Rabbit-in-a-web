package com.ua.hiah.controller.source;

import com.ua.hiah.model.source.SourceTable;
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

    /**
     * Change table comment
     *
     * @param table table's id
     * @param comment comment to change to
     * @return altered source table
     */

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
                    responseCode = "404",
                    description = "Table not found",
                    content = @Content
            )
    })
    @PutMapping("/comment")
    public ResponseEntity<?> changeTableComment(@Param(value = "table") Long table, @Param(value = "comment") String comment) {
        logger.info("SOURCE TABLE CONTROLLER - Change table {} comment", table);

        SourceTable response = tableService.changeComment(table, comment);
        if (response == null) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
