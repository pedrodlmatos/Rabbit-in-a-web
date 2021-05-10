package com.ua.hiah.service.target.database;

import com.ua.hiah.model.CDMVersion;
import com.ua.hiah.model.target.TargetDatabase;
import com.ua.hiah.model.target.TargetTable;

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

    /**
     * Creates a OMOP CDM database from data contained in JSON
     *
     * @param targetDatabase database stored in JSON object
     * @return database altered for specific model
     */

    TargetDatabase createDatabaseFromJSON(TargetDatabase targetDatabase);


    /**
     * Creates stem table on OMOP CDM database
     *
     * @param version OMOP CDM version
     * @param targetDatabase target database object
     * @return created table
     */

    TargetTable createTargetStemTable(CDMVersion version, TargetDatabase targetDatabase);


    /**
     * Removes stem table from OMOP CDM database
     *
     * @param table stem table
     */

    void removeTable(TargetTable table);
}
