package com.ua.hiah.service.target.database;

import com.ua.hiah.model.CDMVersion;
import com.ua.hiah.model.target.TargetDatabase;

public interface TargetDatabaseService {


    /**
     * Verifies if a given OMOP CDM version exists
     *
     * @param cdm OMOP CDM version
     * @return true if exists, false otherwise
     */

    boolean CDMExists(String cdm);


    /**
     * Persists OMOP CDM database (and tables, fields, concepts) from a file
     *
     * @param version OMOP CDM version
     * @return persisted database
     */

    TargetDatabase generateModelFromCSV(CDMVersion version);


    /**
     * Removes a database given its id
     *
     * @param id database's id
     */

    void removeDatabase(Long id);
}
