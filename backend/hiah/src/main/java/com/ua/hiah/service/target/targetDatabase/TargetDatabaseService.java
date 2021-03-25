package com.ua.hiah.service.target.targetDatabase;

import com.ua.hiah.model.target.TargetDatabase;

import java.util.List;

public interface TargetDatabaseService {

    List<TargetDatabase> getAllTargetDatabases();

    TargetDatabase getDefaultDatabase();

    TargetDatabase createDatabaseByCDM(String cdm);
}
