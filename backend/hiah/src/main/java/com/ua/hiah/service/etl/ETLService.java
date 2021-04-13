package com.ua.hiah.service.etl;

import com.ua.hiah.model.ETL;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ETLService {

    /**
     * Creates a new ETL session
     *
     * @param name EHR database name
     * @param file EHR Scan report
     * @param cdm OMOP CDM version
     * @return created ETL session
     */

    ETL createETLSession(String name, MultipartFile file, String cdm);


    ETL createETLSessionFromFile(MultipartFile saveFile);

    /**
     * Retrieve all ETL sessions
     *
     * @return list with ETL sessions
     */

    List<ETL> getAllETL();


    /**
     * Retrieves an ETL session by its id
     *
     * @param id ETL session's id
     * @return ETL session
     */

    ETL getETLWithId(Long id);


    /**
     * Changes the OMOP CDM version of an ETL session and removes the previous OMOP CDM used
     *
     * @param etl_id ETL session's id
     * @param cdm OMOP CDM version to change to
     * @return modified ETL session
     */

    ETL changeTargetDatabase(Long etl_id, String cdm);

    byte[] createSourceFieldListCSV(Long id);

    byte[] createTargetFieldListCSV(Long etl);

    void createDocumentationFile(Long id);


    byte[] save(String filename, Long id);
}
