package com.ua.hiah.service.etl;

import com.ua.hiah.model.ETL;
import com.ua.hiah.model.auth.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ETLService {

    /**
     * Creates a new ETL procedure
     *
     * @param ehrName EHR database name
     * @param ehrScan EHR Scan report
     * @param cdm OMOP CDM version
     * @param username
     * @return created ETL procedure
     */

    ETL createETLProcedure(String ehrName, MultipartFile ehrScan, String cdm, User username);


    /**
     * Creates an ETL procedure from the save file created
     *
     * @param saveFile JSON file containing info about an ETL procedure
     * @param user
     * @return created ETL procedure
     */

    ETL createETLProcedureFromFile(MultipartFile saveFile, User user);


    /**
     * Retrieve all ETL procedure
     *
     * @return list with ETL procedures
     */

    List<ETL> getAllETL();


    /**
     * Retrieves list of ETL procedures managed by user
     *
     * @param user user
     * @return list of ETL procedures
     */

    List<ETL> getETLByUsername(User user);


    /**
     * Deletes ETL procedure given its id
     *
     * @param etl_id ETL procedure's id
     * @return ETL object or null if not found
     */

    ETL deleteETLProcedure(Long etl_id);


    /**
     * Retrieves an ETL procedure by its id
     *
     * @param id ETL procedure's id
     * @return ETL session
     */

    ETL getETLWithId(Long id);


    /**
     * Changes the OMOP CDM version of an ETL procedure and removes the previous OMOP CDM used
     *
     * @param etl_id ETL procedure's id
     * @param cdm OMOP CDM version to change to
     * @return modified ETL procedure
     */

    ETL changeTargetDatabase(Long etl_id, String cdm);


    /**
     * Adds stem table on EHR and on OMOP CDM database
     *
     * @param etl_id ETL procedure's id
     * @return altered ETL procedure
     */

    ETL addStemTable(Long etl_id);


    /**
     * Removes stem table from EHR and OMOP CDM database and their respective mapping
     *
     * @param etl_id ETL procedure's id
     * @return altered ETL procedure
     */

    ETL removeStemTable(Long etl_id);


    /**
     * Creates the file with source fields summary
     *
     * @param etl_id ETL procedure's id
     * @return source field summary
     */

    byte[] createSourceFieldListCSV(Long etl_id);


    /**
     * Creates the file with target fields summary
     *
     * @param etl_id ETL procedure's id
     * @return target field summary
     */

    byte[] createTargetFieldListCSV(Long etl_id);


    /**
     * Creates the ETL procedure summary file
     *
     * @param id ETL procedure's id
     * @return file content or null
     */

    byte[] createWordSummaryFile(Long id);


    /**
     * Creates a JSON file with the current state of an ETL procedure given its id
     * (can be used later to create an ETL procedure using a file)
     *
     * @param filename filename to store data
     * @param etl_id ETL procedure's id
     * @return ETL content as JSON object
     */

    byte[] createSavingFile(String filename, Long etl_id);


}
