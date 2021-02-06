package com.ua.riah.controller;

import com.ua.riah.model.ETL;
import com.ua.riah.model.TableMapping;
import com.ua.riah.service.etlService.ETLService;
import org.hibernate.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/etl")
public class ETLController {

    @Autowired
    ETLService etlService;

    private static final Logger logger = LoggerFactory.getLogger(ETLController.class);

    @GetMapping("/all")
    public ResponseEntity<?> getAllETLs() {
        logger.info("ETL - Requesting all ETL sessions");

        List<ETL> response = etlService.getAllETL();

        if (response == null)
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getETLById(@PathVariable Long id) {
        logger.info("ETL - Requesting ETL session with id " + id);

        ETL response = etlService.getETLWithId(id);

        if (response == null) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/add", consumes = "multipart/form-data")
    public ResponseEntity<?> createETLSession(@RequestParam("file") MultipartFile file, @Param(value = "cdm") String cdm) {
        ETL etl = etlService.createETLSession(file, cdm);
        logger.info("ETL - Created ETL session with id: " + etl.getId());
        return new ResponseEntity<>(etl, HttpStatus.OK);
    }


    @PutMapping("/changeTrgDB")
    public ResponseEntity<?> changeTargetDatabase(@RequestBody ETL etl, @Param(value = "cdm") String cdm) {
        logger.info("ETL - Change target database of session {} to {}", etl.getId(), cdm);

        ETL response = etlService.changeTargetDatabase(etl.getId(), cdm);
        if (response == null) {
            response = new ETL();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}