package com.ua.riah.controller;

import com.ua.riah.model.DBTable;
import com.ua.riah.service.dbTableService.DBTableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/table")
public class DBTableController {

    private static final Logger logger = LoggerFactory.getLogger(DBTableController.class);

    @Autowired
    private DBTableService service;

    @GetMapping("/all")
    public ResponseEntity<List<DBTable>> getAllTables() {
        logger.info("TABLE - Requesting all tables");

        List<DBTable> tableResponse = service.getAllTables();

        if (tableResponse == null) {
            tableResponse = new ArrayList<>();
        }

        return new ResponseEntity<>(tableResponse, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<DBTable> getDatabase(@PathVariable String id) {
        logger.info("TABLE - Requesting table with id: " + id);

        DBTable tableResponse = service.getTable(id);

        if (tableResponse == null) {
            return new ResponseEntity<>(tableResponse, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(tableResponse, HttpStatus.OK);
    }
}
