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

    /**
     * Change field comment
     *
     * @param field field's id
     * @param comment comment to change to
     * @return altered field
     */

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
                    responseCode = "404",
                    description = "Field not found",
                    content = @Content
            )
    })
    @PutMapping("/comment")
    public ResponseEntity<?> changeFieldComment(@Param(value = "field") Long field, @Param(value = "comment") String comment) {
        logger.info("TARGET FIELD CONTROLLER - Change field {} comment", field);

        TargetField response = fieldService.changeComment(field, comment);
        if (response == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
