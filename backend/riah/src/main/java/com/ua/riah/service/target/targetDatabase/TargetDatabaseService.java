package com.ua.riah.service.target.targetDatabase;

import com.ua.riah.model.target.TargetDatabase;

import java.util.List;

public interface TargetDatabaseService {

    void loadCDMDatabases();

    List<TargetDatabase> getAllTargetDatabases();

    TargetDatabase getDefaultDatabase();

    TargetDatabase getDatabaseByCDM(String cdm);
}
