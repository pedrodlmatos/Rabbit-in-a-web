package com.ua.hiah.service.omop.database;

import com.ua.hiah.model.CDMVersion;
import com.ua.hiah.model.omop.OMOPDatabase;
import com.ua.hiah.model.omop.OMOPTable;

public interface OMOPDatabaseService {


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

    OMOPDatabase generateModelFromCSV(CDMVersion version);


    /**
     * Removes a database given its id
     *
     * @param id database's id
     */

    void removeDatabase(Long id);

    /**
     * Creates a OMOP CDM database from data contained in JSON
     *
     * @param omopDatabase database stored in JSON object
     * @return database altered for specific model
     */

    OMOPDatabase createDatabaseFromJSON(OMOPDatabase omopDatabase);


    /**
     * Creates stem table on OMOP CDM database
     *
     * @param version OMOP CDM version
     * @param omopDatabase target database object
     * @return created table
     */

    OMOPTable createTargetStemTable(CDMVersion version, OMOPDatabase omopDatabase);


    /**
     * Removes stem table from OMOP CDM database
     *
     * @param table stem table
     */

    void removeTable(OMOPTable table);
}
