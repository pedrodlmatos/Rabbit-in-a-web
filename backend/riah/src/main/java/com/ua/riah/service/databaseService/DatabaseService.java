package com.ua.riah.service.databaseService;

import com.ua.riah.model.Database;

import java.util.List;

public interface DatabaseService {
    
    List<Database> getAllDatabases();

    void loadCDMDatabases();

    void loadScanReport();

    Database getDatabase(String id);
}
