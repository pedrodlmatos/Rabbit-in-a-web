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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * @param etl_id table mapping id
     * @return table mapping
     */

    @Operation(summary = "Retrieves a table mapping")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Table mapping found",
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
    @GetMapping("/map/{etl_id}")
    @JsonView(Views.TableMapping.class)
    public ResponseEntity<?> getTableMapping(@PathVariable Long etl_id) {
        logger.info("TABLE MAPPING CONTROLLER - Requesting table mapping with id " + etl_id);
        TableMapping response = service.getTableMappingById(etl_id);

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

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
        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

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
        logger.info("TABLE MAPPING CONTROLLER - Removed table mapping with id " + map_id);
        TableMapping response = service.removeTableMapping(map_id);

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        List<TableMapping> res = service.getTableMappingFromETL(etl_id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }


    /**
     * Changes the completion status of a table mapping
     *
     * @param map_id table mapping id
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
    @PutMapping("/map/{map_id}/complete")
    public ResponseEntity<?> editCompleteMapping(@PathVariable Long map_id, @Param(value = "completion") boolean completion) {
        logger.info("TABLE MAPPING CONTROLLER - Change completion status of mapping " + map_id);
        TableMapping response = service.changeCompletionStatus(map_id, completion);

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /**
     * Changes the logic of a table mapping
     *
     * @param map_id table mapping id
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
    @PutMapping("/map/{map_id}/logic")
    public ResponseEntity<?> editMappingLogic(@PathVariable Long map_id, @Param(value = "logic") String logic) {
        logger.info("TABLE MAPPING - Change mapping logic of mapping " + map_id);
        TableMapping response = service.changeMappingLogic(map_id, logic);

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
