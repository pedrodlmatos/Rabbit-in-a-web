package com.ua.riah.controller;

import com.ua.riah.model.FieldMapping;
import com.ua.riah.model.TableMapping;
import com.ua.riah.service.fieldMapping.FieldMappingService;
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
@RequestMapping("/api/fieldMap")
public class FieldMappingController {

    private Logger logger = LoggerFactory.getLogger(FieldMappingController.class);

    @Autowired
    private FieldMappingService service;

    @PostMapping("/create")
    public ResponseEntity<?> createFieldMapping(@Param(value = "tableMap") Long tableMap, @Param(value = "source_id") Long source_id, @Param(value = "target_id") Long target_id) {
        FieldMapping response = service.addFieldMapping(source_id, target_id, tableMap);
        if (response == null) {
            response = new FieldMapping();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        logger.info("FIELD MAPPING - Add field mapping between {} and {} in table mapping {}", source_id, target_id, tableMap);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFieldMapping(@Param(value="tableMappingId") Long tableMappingId, @Param(value="fieldMappingId") Long fieldMappingId) {
        FieldMapping response = service.removeFieldMapping(fieldMappingId);

        if (response == null) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        logger.info("FIELD MAPPING - Removed field mapping with id " + fieldMappingId);

        List<FieldMapping> res = service.getFieldMappingsFromTableMapping(tableMappingId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
