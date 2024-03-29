package com.ua.riaw.etlProcedure.source.ehrDatabase;

import com.ua.riaw.etlProcedure.target.omopDatabase.CDMVersion;
import com.ua.riaw.etlProcedure.source.ehrTable.EHRTable;
import org.springframework.web.multipart.MultipartFile;

public interface EHRDatabaseService {

    /**
     * Reads Scan report generated by White Rabbit and persists its information
     *
     * @param name EHR database name
     * @param file Scan report file
     * @return Source Database (with its tables, fields and value counts)
     */

    EHRDatabase createDatabaseFromScanReport(String name, MultipartFile file);


    /**
     * Creates and persists the content of an EHR database contained in a JSON file
     *
     * @param ehrDatabase EHR database stored in JSON file
     * @return created source database
     */

    EHRDatabase createDatabaseFromJSON(EHRDatabase ehrDatabase);


    /**
     * Adds stem table to EHR database and its mappings (contained in file)
     *
     * @param version OMOP CDM version
     * @param ehrDatabase EHR database object
     * @return altered source database
     */

    EHRTable createEHRStemTable(CDMVersion version, EHRDatabase ehrDatabase);


    /**
     * Remove stem table from EHR database
     *
     * @param table stem table
     */

    void removeTable(EHRTable table);
}
