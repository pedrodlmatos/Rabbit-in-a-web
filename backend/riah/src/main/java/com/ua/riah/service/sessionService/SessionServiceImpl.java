package com.ua.riah.service.sessionService;

import com.ua.riah.model.ETL;
import com.ua.riah.model.Session;
import com.ua.riah.repository.SessionRepository;
import com.ua.riah.service.etlService.ETLService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SessionServiceImpl implements SessionService{

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    ETLService etlService;


    @Override
    public List<Session> getAllSessions() {
        return sessionRepository.findAll();
    }


    @Override
    public Session getSession(String id) {
        Optional<Session> session = sessionRepository.findById(id);
        return session.orElse(null);
    }

    @Override
    public Session createSession(String cdmVersion) {
        // create session instance
        Session session = new Session();
        session.setId("session-" + RandomStringUtils.randomAlphanumeric(4));

        // create etl
        ETL etl = etlService.createCDMModel("db-mimic", cdmVersion);
        session.setEtl(etl);

        return sessionRepository.save(session);
    }

}
