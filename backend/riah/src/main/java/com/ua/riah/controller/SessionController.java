package com.ua.riah.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.ua.riah.model.Session;
import com.ua.riah.model.SummaryViews;
import com.ua.riah.service.sessionService.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/sessions")
public class SessionController {

    @Autowired
    SessionService sessionService;

    private static final Logger logger = LoggerFactory.getLogger(SessionController.class);

    @JsonView(SummaryViews.SessionSummary.class)
    @GetMapping("/all")
    public ResponseEntity<List<Session>> getAllSessions() {
        logger.info("SESSION - Requesting all sessions");

        List<Session> sessionsResponse = sessionService.getAllSessions();

        if (sessionsResponse == null)
            sessionsResponse = new ArrayList<>();

        return new ResponseEntity<>(sessionsResponse, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Session> getSession(@PathVariable String id) {
        logger.info("SESSION - Requesting session with id: " + id);

        Session sessionResponse = sessionService.getSession(id);

        if (sessionResponse == null) {
            return new ResponseEntity<>(sessionResponse, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(sessionResponse, HttpStatus.OK);
    }


    @PostMapping("/create")
    public ResponseEntity<Session> createSession(@RequestBody Map<String, String> cdmVersion) {
        logger.info("SESSION - Creating session");

        Session session = sessionService.createSession(cdmVersion.get("cdm"));

        logger.info("SESSION - Created session with id: " + session.getId());

        return new ResponseEntity<>(session, HttpStatus.OK);
    }
}
