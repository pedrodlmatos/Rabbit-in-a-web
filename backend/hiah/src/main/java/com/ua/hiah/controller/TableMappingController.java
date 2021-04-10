package com.ua.hiah.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.ua.hiah.model.ETL;
import com.ua.hiah.model.TableMapping;
import com.ua.hiah.service.tableMapping.TableMappingService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/v1/api/tableMap")
public class TableMappingController {

    private static final Logger logger = LoggerFactory.getLogger(TableMappingController.class);

    @Autowired
    private TableMappingService service;


    /**
     * Retrieves data from a table mapping given its id
     *
     * @param id table mapping id
     * @return table mapping
     */

    @Operation(summary = "Retrieve a table mapping")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "table mapping found",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TableMapping.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Table mapping not found",
                    content = @Content
            )

    })
    @GetMapping("/map/{id}")
    @JsonView(Views.TableMapping.class)
    public ResponseEntity<?> getTableMapping(@PathVariable Long id) {
        logger.info("TABLE MAPPING CONTROLLER - Requesting table mapping with id " + id);

        TableMapping response = service.getTableMappingById(id);

        if (response == null) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /**
     * Create a table mapping
     *
     * @param etl_id ETL session id
     * @param source_id Source table id
     * @param target_id Target table id
     * @return created table map
     */

    @Operation(summary = "Creates a table mapping")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Table mapping created",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TableMapping.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "ETL session not found",
                    content = @Content
            )
    })
    @PostMapping("/map")
    public ResponseEntity<?> createTableMapping(@Param(value = "elt_id") Long etl_id, @Param(value = "source_id") Long source_id, @Param(value = "target_id") Long target_id) {
        TableMapping response = service.addTableMapping(source_id, target_id, etl_id);
        if (response == null) {
            response = new TableMapping();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        logger.info("TABLE MAPPING CONTROLLER - Add table mapping between {} and {} in session {}", source_id, target_id, etl_id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /**
     * Deletes a table mapping
     *
     * @param map_id table mapping id
     * @param etl_id ETL session id
     * @return ETL session (with other table mappings)
     */

    @Operation(summary = "Deletes a table mapping")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Table mapping deleted",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ETL.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Table mapping not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "ETL session not found",
                    content = @Content
            )
    })
    @DeleteMapping("/map")
    public ResponseEntity<?> removeTableMapping(@Param(value="map_id") Long map_id, @Param(value="etl_id") Long etl_id) {
        TableMapping response = service.removeTableMapping(map_id);

        if (response == null) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        logger.info("TABLE MAPPING - Removed table mapping with id " + map_id);

        List<TableMapping> res = service.getTableMappingFromETL(etl_id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }


    /**
     * Changes the completion status of a table mapping
     *
     * @param id table mapping id
     * @param completion completion status to change to
     * @return table mapping altered
     */

    @Operation(summary = "Change table mapping completion status")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Changed completion status",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TableMapping.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Table mapping not found",
                    content = @Content
            )
    })
    @PutMapping("/map/{id}/complete")
    public ResponseEntity<?> editCompleteMapping(@PathVariable Long id, @Param(value = "completion") boolean completion) {
        logger.info("TABLE MAPPING - Change completion status of mapping " + id);
        TableMapping response = service.changeCompletionStatus(id, completion);

        if (response == null) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /**
     * Changes the logic of a table mapping
     *
     * @param id table mapping id
     * @param logic table mapping logic
     * @return table mapping altered
     */

    @Operation(summary = "Change table mapping logic")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Logic changed",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TableMapping.class)
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
        logger.info("TABLE MAPPING - Change mapping logic of mapping " + id);
        TableMapping response = service.changeMappingLogic(id, logic);

        if (response == null) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
