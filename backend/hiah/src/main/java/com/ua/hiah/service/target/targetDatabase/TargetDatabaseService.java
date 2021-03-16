package com.ua.hiah.service.target.targetDatabase;

import com.ua.hiah.model.target.TargetDatabase;

import java.util.List;

public interface TargetDatabaseService {

    void loadCDMDatabases();

    List<TargetDatabase> getAllTargetDatabases();

    TargetDatabase getDefaultDatabase();

    TargetDatabase getDatabaseByCDM(String cdm);
}
