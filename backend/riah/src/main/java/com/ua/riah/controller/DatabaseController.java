package com.ua.riah.controller;

import com.ua.riah.model.Database;
import com.ua.riah.service.databaseService.DatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/database")
public class DatabaseController {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseController.class);

    @Autowired
    private DatabaseService DatabaseService;

    @GetMapping("/all")
    public ResponseEntity<List<Database>> getAllDatabases() {
        logger.info("DATABASE - Requesting all databases");

        List<Database> DatabaseResponse = DatabaseService.getAllDatabases();

        if (DatabaseResponse == null) {
            DatabaseResponse = new ArrayList<>();
        }

        return new ResponseEntity<>(DatabaseResponse, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<Database> getDatabase(@PathVariable String id) {
        logger.info("DATABASE - Requesting database with id: " + id);

        Database DatabaseResponse = DatabaseService.getDatabase(id);

        if (DatabaseResponse == null) {
            return new ResponseEntity<>(DatabaseResponse, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(DatabaseResponse, HttpStatus.OK);
    }

}
