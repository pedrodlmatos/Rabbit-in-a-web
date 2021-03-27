package com.ua.hiah.controller.target;

import com.ua.hiah.model.target.TargetTable;
import com.ua.hiah.service.target.table.TargetTableService;
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
@RequestMapping("/v1/api/targetTable")
public class TargetTableController {

    @Autowired
    private TargetTableService tableService;

    private static final Logger logger = LoggerFactory.getLogger(TargetTableController.class);

    /**
     * Change table comment
     *
     * @param table Table id
     * @param comment comment to change to
     * @return altered ETL session
     */

    @Operation(summary = "Change table comment")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Changed table comment",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TargetTable.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found",
                    content = @Content
            )
    })
    @PutMapping("/comment")
    public ResponseEntity<?> changeTableComment(@Param(value = "table") Long table, @Param(value = "comment") String comment) {
        logger.info("TARGET TABLE CONTROLLER - Change table {} comment", table);

        TargetTable response = tableService.changeComment(table, comment);
        if (response == null) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
