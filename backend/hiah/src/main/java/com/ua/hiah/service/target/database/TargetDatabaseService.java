package com.ua.hiah.service.target.database;

import com.ua.hiah.model.CDMVersion;
import com.ua.hiah.model.target.TargetDatabase;

public interface TargetDatabaseService {

    boolean CDMExists(String cdm);

    TargetDatabase generateModelFromCSV(CDMVersion version);

    void removeDatabase(Long id);
}
