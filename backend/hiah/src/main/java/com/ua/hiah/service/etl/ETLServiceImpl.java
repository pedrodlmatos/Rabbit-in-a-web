package com.ua.hiah.service.etl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.ua.hiah.model.CDMVersion;
import com.ua.hiah.model.ETL;
import com.ua.hiah.model.TableMapping;
import com.ua.hiah.model.source.SourceDatabase;
import com.ua.hiah.model.source.SourceTable;
import com.ua.hiah.model.target.TargetDatabase;
import com.ua.hiah.model.target.TargetTable;
import rabbitcore.utilities.ETLSummaryGenerator;
import rabbitcore.utilities.files.Row;
import com.ua.hiah.repository.ETLRepository;
import com.ua.hiah.service.source.database.SourceDatabaseService;
import com.ua.hiah.service.tableMapping.TableMappingService;
import com.ua.hiah.service.target.database.TargetDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rabbitinahat.ETLWordDocumentGenerator;
import rabbitinahat.model.ETL_RIAH;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;


@Service
public class ETLServiceImpl implements ETLService {

    @Autowired
    ETLRepository etlRepository;

    @Autowired
    TargetDatabaseService targetDatabaseService;

    @Autowired
    SourceDatabaseService sourceDatabaseService;

    @Autowired
    TableMappingService mappingService;

    private static final Logger logger = LoggerFactory.getLogger(ETLServiceImpl.class);


    /**
     * Creates a new ETL procedure
     *
     * @param ehrName EHR database name
     * @param ehrScan EHR Scan report
     * @param cdm OMOP CDM version
     * @return created ETL procedure
     */

    @Override
    public ETL createETLProcedure(String ehrName, MultipartFile ehrScan, String cdm) {
        if (targetDatabaseService.CDMExists(cdm)) {
            ETL etl = new ETL();
            etl.setName("ETL procedure " + etlRepository.count());


            Instant start = Instant.now();
            etl.setTargetDatabase(targetDatabaseService.generateModelFromCSV(CDMVersion.valueOf(cdm)));
            Instant end = Instant.now();
            Duration duration = Duration.between(start, end);
            logger.info("ETL SERVICE - Loaded OMOP CDM database {} in {} seconds", cdm, duration.getSeconds());

            start = Instant.now();
            etl.setSourceDatabase(sourceDatabaseService.createDatabaseFromScanReport(ehrName, ehrScan));
            end = Instant.now();
            duration = Duration.between(start, end);
            logger.info("ETL SERVICE - Loaded EHR database in {} seconds", duration.getSeconds());
            return etlRepository.save(etl);
        }
        return null;
    }


    /**
     * Creates an ETL procedure from the save file created
     *
     * @param saveFile JSON file containing info about an ETL procedure
     * @return created ETL procedure
     */

    @Override
    public ETL createETLSessionFromFile(MultipartFile saveFile) {
        try {
            // write content in a file object
            File tempSaveFile = new File("tempSave.json");
            if(tempSaveFile.createNewFile()) {
                OutputStream os = new FileOutputStream(tempSaveFile);
                os.write(saveFile.getBytes());
                os.close();
            }

            // read file as a json object
            FileInputStream inputStream = new FileInputStream(tempSaveFile);
            InputStreamReader reader = new InputStreamReader(inputStream);
            JsonReader jsonReader = new JsonReader(reader);
            Gson gson = new Gson();
            ETL request = gson.fromJson(jsonReader, ETL.class);

            ETL response = new ETL();
            response.setName("ETL procedure " + etlRepository.count());

            // response = etlRepository.save(response);

            // create source database from json
            SourceDatabase source = sourceDatabaseService.createDatabaseFromJSON(request.getSourceDatabase());
            response.setSourceDatabase(source);

            // create target database from json
            TargetDatabase target = targetDatabaseService.createDatabaseFromJSON(request.getTargetDatabase());
            response.setTargetDatabase(target);

            // create mappings from json
            List<TableMapping> mappings = mappingService.getTableMappingsFromJSON(response, request.getTableMappings(), source, target);
            response.setTableMappings(mappings);

            tempSaveFile.delete();
            return etlRepository.save(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    /**
     * Retrieve all ETL procedure
     *
     * @return list with ETL procedures
     */

    @Override
    public List<ETL> getAllETL() {
        return etlRepository.findAll();
    }


    /**
     * Retrieves an ETL procedure by its id
     *
     * @param id ETL procedure's id
     * @return ETL session
     */

    @Override
    public ETL getETLWithId(Long id) {
        ETL etl = etlRepository.findById(id).orElse(null);

        if (etl != null) {
            List<SourceTable> sourceTables = etl.getSourceDatabase().getTables();
            sourceTables.sort(Comparator.comparingLong(SourceTable::getId));

            List<TargetTable> targetTables = etl.getTargetDatabase().getTables();
            targetTables.sort(Comparator.comparingLong(TargetTable::getId));

            return etl;
        }

        return null;
    }


    /**
     * Changes the OMOP CDM version of an ETL procedure and removes the previous OMOP CDM used
     *
     * @param etl_id ETL procedure's id
     * @param cdm OMOP CDM version to change to
     * @return modified ETL procedure
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


    /**
     * Creates the file with source fields summary
     *
     * @param etl_id ETL procedure's id
     * @return source field summary
     */

    @Override
    public byte[] createSourceFieldListCSV(Long etl_id) {
        ETL etl = etlRepository.findById(etl_id).orElse(null);

        if (etl != null) {
            List<Row> rows = ETLSummaryGenerator.createSourceFieldList(etl);
            return ETLSummaryGenerator.writeCSV("sourceList.csv", rows);
        }
        return null;
    }


    /**
     * Creates the file with target fields summary
     *
     * @param etl_id ETL procedure's id
     * @return target field summary
     */

    @Override
    public byte[] createTargetFieldListCSV(Long etl_id) {
        ETL etl = etlRepository.findById(etl_id).orElse(null);

        if (etl != null) {
            List<Row> rows = ETLSummaryGenerator.createTargetFieldList(etl);
            return ETLSummaryGenerator.writeCSV("targetList.csv", rows);
        }
        return null;
    }


    @Override
    public File createWordSummaryFile(Long id) {

        ETL etl = etlRepository.findById(id).orElse(null);

        if (etl != null) {
            ETL_RIAH etlRiah = new ETL_RIAH(etl);

            File documentData = ETLWordDocumentGenerator.generate(etlRiah);
            if (documentData != null) {
                return documentData;
            }
        }

        return null;

        /*
        if (etl != null) {
            WordDocumentGenerator generator = new WordDocumentGenerator(etl);
            generator.generateWordDocument(etl);
        }

         */

    }


    /**
     * Creates a JSON file with the current state of an ETL procedure given its id
     * (can be used later to create an ETL procedure using a file)
     *
     * @param filename filename to store data
     * @param etl_id ETL procedure's id
     * @return ETL content as JSON object
     */

    @Override
    public byte[] createSavingFile(String filename, Long etl_id) {
        ETL etl = etlRepository.findById(etl_id).orElse(null);

        if (etl != null) {
            try {
                //String etlJsonStr = JsonWriter.formatJson(JsonWriter.objectToJson(etl));
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

                // sort tables and fields by its (to keep original order)
                List<SourceTable> sourceTables = etl.getSourceDatabase().getTables();
                sourceTables.sort(Comparator.comparingLong(SourceTable::getId));

                List<TargetTable> targetTables = etl.getTargetDatabase().getTables();
                targetTables.sort(Comparator.comparingLong(TargetTable::getId));

                // transform ETL object to json object (keeping only needed attributes)
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
