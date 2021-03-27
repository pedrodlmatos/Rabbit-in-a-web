package com.ua.hiah.service.etl;

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
import com.ua.hiah.service.target.database.TargetDatabaseService;
import com.ua.hiah.service.target.table.TargetTableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

    private static final Logger logger = LoggerFactory.getLogger(ETLServiceImpl.class);

    @Override
    public List<ETL> getAllETL() {
        return etlRepository.findAll();
    }


    @Override
    public ETL createETLSession(String name, MultipartFile file, String cdm) {
        if (targetDatabaseService.CDMExists(cdm)) {
            ETL etl = new ETL();
            etl.setName("ETL session " + etlRepository.count());
            etl.setTargetDatabase(targetDatabaseService.generateModelFromCSV(CDMVersion.valueOf(cdm)));
            logger.info("ETL SERVICE - Loaded OMOP CDM database " + cdm);

            etl.setSourceDatabase(sourceDatabaseService.createDatabaseFromScanReport(name, file));
            logger.info("ETL SERVICE - Loaded EHR database");
            return etlRepository.save(etl);
        }
        return null;
    }

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

    @Override
    public ETL changeTargetDatabase(Long etl_id, String cdm) {
        ETL etl = etlRepository.findById(etl_id).orElse(null);

        if (etl != null) {
            TargetDatabase previous = etl.getTargetDatabase();

            // create an OMOP CDM from a different version
            etl.setTargetDatabase(targetDatabaseService.generateModelFromCSV(CDMVersion.valueOf(cdm)));
            //System.out.println(etl.toString());

            // remove previous
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
    public ETL changeComment(Long id, Long tableId, String comment) {
        ETL etl = etlRepository.findById(id).orElse(null);

        if (etl != null) {
            if (sourceTableService.changeComment(tableId, comment) != null) {
                List<SourceTable> tables = etl.getSourceDatabase().getTables();
                Collections.sort(tables, Comparator.comparingLong(SourceTable::getId));
                return etlRepository.save(etl);
            } else if (targetTableService.changeComment(tableId, comment) != null) {
                List<TargetTable> tables = etl.getTargetDatabase().getTables();
                Collections.sort(tables, Comparator.comparingLong(TargetTable::getId));
                return etlRepository.save(etl);
            }
        }

        return etl;
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
    public void createDocumentationFile(Long id) {
        /*
        ETL etl = etlRepository.findById(id).orElse(null);

        if (etl != null) {
            WordDocumentGenerator generator = new WordDocumentGenerator(etl);
            generator.generateWordDocument(etl);
        }
         */
    }
}
