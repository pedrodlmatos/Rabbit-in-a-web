package com.ua.riah.controller;

import com.ua.riah.model.target.TargetDatabase;
import com.ua.riah.service.target.targetDatabase.TargetDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/targetDatabases")
public class TargetDatabaseController {

    @Autowired
    TargetDatabaseService targetDatabaseService;

    private static final Logger logger = LoggerFactory.getLogger(TargetDatabaseController.class);

    @GetMapping("/all")
    public ResponseEntity<?> getAllTargetDatabases() {
        logger.info("TARGET DATABASES - Requesting all target databases");

        List<TargetDatabase> response = targetDatabaseService.getAllTargetDatabases();

        if (response == null)
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
