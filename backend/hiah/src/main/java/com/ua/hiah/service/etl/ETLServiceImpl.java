package com.ua.hiah.service.etl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.ua.hiah.model.CDMVersion;
import com.ua.hiah.model.ETL;
import com.ua.hiah.model.source.SourceTable;
import com.ua.hiah.model.target.TargetDatabase;
import com.ua.hiah.model.target.TargetTable;
import com.ua.hiah.rabbitcore.utilities.ETLSummaryGenerator;
import com.ua.hiah.rabbitcore.utilities.files.Row;
import com.ua.hiah.repository.ETLRepository;
import com.ua.hiah.service.source.database.SourceDatabaseService;
import com.ua.hiah.service.source.table.SourceTableService;
import com.ua.hiah.service.tableMapping.TableMappingService;
import com.ua.hiah.service.target.database.TargetDatabaseService;
import com.ua.hiah.service.target.table.TargetTableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.cedarsoftware.util.io.JsonWriter;

@Service
public class ETLServiceImpl implements ETLService {

    @Autowired
    ETLRepository etlRepository;

    @Autowired
    TargetDatabaseService targetDatabaseService;

    @Autowired
    TargetTableService targetTableService;

    @Autowired
    SourceDatabaseService sourceDatabaseService;

    @Autowired
    SourceTableService sourceTableService;

    @Autowired
    TableMappingService mappingService;

    private static final Logger logger = LoggerFactory.getLogger(ETLServiceImpl.class);




    /**
     * Creates a new ETL session
     *
     * @param name EHR database name
     * @param file EHR Scan report
     * @param cdm OMOP CDM version
     * @return created ETL session
     */

    @Override
    public ETL createETLSession(String name, MultipartFile file, String cdm) {
        if (targetDatabaseService.CDMExists(cdm)) {
            ETL etl = new ETL();
            etl.setName("ETL session " + etlRepository.count());
            etl = etlRepository.save(etl);

            Instant start = Instant.now();
            etl.setTargetDatabase(targetDatabaseService.generateModelFromCSV(CDMVersion.valueOf(cdm)));
            Instant end = Instant.now();
            Duration duration = Duration.between(start, end);
            logger.info("ETL SERVICE - Loaded OMOP CDM database {} in {} seconds", cdm, duration.getSeconds());

            start = Instant.now();
            etl.setSourceDatabase(sourceDatabaseService.createDatabaseFromScanReport(name, file));
            end = Instant.now();
            duration = Duration.between(start, end);
            logger.info("ETL SERVICE - Loaded EHR database in {} seconds", duration.getSeconds());
            return etlRepository.save(etl);
        }
        return null;
    }
    

    @Override
    public ETL createETLSessionFromFile(MultipartFile saveFile) {
        try {
            // write content in a file
            File scanTemp = new File("scanTemp.xlsx");
            if(scanTemp.createNewFile()) {
                OutputStream os = new FileOutputStream(scanTemp);
                os.write(saveFile.getBytes());
                os.close();
            }

            FileInputStream inputStream = new FileInputStream(scanTemp);
            InputStreamReader reader = new InputStreamReader(inputStream);

            JsonReader jsonReader = new JsonReader(reader);

            scanTemp.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    /**
     * Retrieve all ETL sessions
     *
     * @return list with ETL sessions
     */

    @Override
    public List<ETL> getAllETL() {
        return etlRepository.findAll();
    }


    /**
     * Retrieves an ETL session by its id
     *
     * @param id ETL session's id
     * @return ETL session
     */

    @Override
    public ETL getETLWithId(Long id) {
        ETL etl = etlRepository.findById(id).orElse(null);

        if (etl != null) {
            List<SourceTable> sourceTables = etl.getSourceDatabase().getTables();
            Collections.sort(sourceTables, Comparator.comparingLong(SourceTable::getId));

            List<TargetTable> targetTables = etl.getTargetDatabase().getTables();
            Collections.sort(targetTables, Comparator.comparingLong(TargetTable::getId));

            return etl;
        }

        return null;
    }


    /**
     * Changes the OMOP CDM version of an ETL session and removes the previous OMOP CDM used
     *
     * @param etl_id ETL session's id
     * @param cdm OMOP CDM version to change to
     * @return modified ETL session
     */

    @Override
    public ETL changeTargetDatabase(Long etl_id, String cdm) {
        ETL etl = etlRepository.findById(etl_id).orElse(null);

        if (etl != null) {
            TargetDatabase previous = etl.getTargetDatabase();
            // create an OMOP CDM from a different version
            etl.setTargetDatabase(targetDatabaseService.generateModelFromCSV(CDMVersion.valueOf(cdm)));
            // remove previous cdm and mappings
            mappingService.removeTableMappingsFromETL(etl.getId());
            targetDatabaseService.removeDatabase(previous.getId());

            //targetDatabaseService.removeDatabase(previous);
            // order tables by id
            /*
            List<SourceTable> sourceTables = etl.getSourceDatabase().getTables();
            Collections.sort(sourceTables, Comparator.comparingLong(SourceTable::getId));

            List<TargetTable> targetTables = etl.getTargetDatabase().getTables();
            Collections.sort(targetTables, Comparator.comparingLong(TargetTable::getId));
             */
            return etlRepository.save(etl);
        }
        return null;
    }


    @Override
    public byte[] createSourceFieldListCSV(Long id) {
        ETL etl = etlRepository.findById(id).orElse(null);

        if (etl != null) {
            List<Row> rows = ETLSummaryGenerator.createSourceFieldList(etl);
            byte[] outputStream = ETLSummaryGenerator.writeCSV("sourceList.csv", rows);

            return outputStream;
        }
        return null;
    }

    @Override
    public byte[] createTargetFieldListCSV(Long id) {
        ETL etl = etlRepository.findById(id).orElse(null);

        if (etl != null) {
            List<Row> rows = ETLSummaryGenerator.createTargetFieldList(etl);
            byte[] outputStream = ETLSummaryGenerator.writeCSV("targetList.csv", rows);

            return outputStream;
        }
        return null;
    }


    @Override
    public void createDocumentationFile(Long id) {
        /*
        ETL etl = etlRepository.findById(id).orElse(null);

        if (etl != null) {
            WordDocumentGenerator generator = new WordDocumentGenerator(etl);
            generator.generateWordDocument(etl);
        }
         */
    }

    @Override
    public byte[] save(String filename, Long id) {
        ETL etl = etlRepository.findById(id).orElse(null);

        if (etl != null) {
            try {
                //String etlJsonStr = JsonWriter.formatJson(JsonWriter.objectToJson(etl));
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                String etlJsonStr = gson.toJson(etl);

                FileOutputStream fileOutputStream = new FileOutputStream(filename);
                //GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream);
                OutputStreamWriter out = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
                out.write(etlJsonStr);

                return etlJsonStr.getBytes(StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
