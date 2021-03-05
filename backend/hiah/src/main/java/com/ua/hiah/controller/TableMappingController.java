package com.ua.hiah.controller;

import com.ua.hiah.model.TableMapping;
import com.ua.hiah.service.tableMapping.TableMappingService;
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
@RequestMapping("/api/tableMap")
public class TableMappingController {

    private static final Logger logger = LoggerFactory.getLogger(TableMappingController.class);

    @Autowired
    private TableMappingService service;

    @GetMapping("/map/{id}")
    public ResponseEntity<?> getTableMapping(@PathVariable Long id) {
        logger.info("TABLE MAPPING - Requesting table mapping with id " + id);

        TableMapping response = service.getTableMappingById(id);

        if (response == null) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTableMapping(@Param(value = "elt_id") Long etl_id, @Param(value = "source_id") Long source_id, @Param(value = "target_id") Long target_id) {
        TableMapping response = service.addTableMapping(source_id, target_id, etl_id);
        if (response == null) {
            response = new TableMapping();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        logger.info("TABLE MAPPING - Add table mapping between {} and {} in session {}", source_id, target_id, etl_id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeTableMapping(@Param(value="map_id") Long map_id, @Param(value="etl_id") Long etl_id) {
        TableMapping response = service.removeTableMapping(map_id);

        if (response == null) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        logger.info("TABLE MAPPING - Removed table mapping with id " + map_id);

        List<TableMapping> res = service.getTableMappingFromETL(etl_id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
