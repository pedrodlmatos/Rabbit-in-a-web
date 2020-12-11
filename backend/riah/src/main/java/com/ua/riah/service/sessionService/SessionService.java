package com.ua.riah.service.sessionService;

import com.ua.riah.model.Session;

import java.util.List;

public interface SessionService {

    public List<Session> getAllSessions();
    public Session getSession(String id);
    public Session createSession(String session);
}
