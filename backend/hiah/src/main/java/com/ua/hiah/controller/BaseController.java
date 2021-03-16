package com.ua.hiah.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class BaseController {

    @GetMapping("/")
    public ResponseEntity<?> homeMessage() {
        return new ResponseEntity<>("Hare-in-a-hat home message", HttpStatus.OK);
    }
}
