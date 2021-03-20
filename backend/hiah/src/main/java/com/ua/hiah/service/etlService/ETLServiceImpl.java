package com.ua.hiah.service.etlService;

import com.ua.hiah.model.ETL;
import com.ua.hiah.model.source.SourceDatabase;
import com.ua.hiah.model.source.SourceTable;
import com.ua.hiah.model.target.TargetDatabase;
import com.ua.hiah.model.target.TargetTable;
import com.ua.hiah.rabbitcore.utilities.files.Row;
import com.ua.hiah.repository.ETLRepository;
import com.ua.hiah.service.source.sourceDatabaseService.SourceDatabaseService;
import com.ua.hiah.service.source.sourceTableService.SourceTableService;
import com.ua.hiah.service.tableMapping.TableMappingService;
import com.ua.hiah.service.target.targetDatabase.TargetDatabaseService;
import com.ua.hiah.service.target.targetTable.TargetTableService;
import com.ua.hiah.utilities.ETLSummaryGenerator;
import com.ua.hiah.utilities.WordDocumentGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class ETLServiceImpl implements ETLService {

    @Autowired
    ETLRepository etlRepository;

    @Autowired
    SourceDatabaseService sourceDatabaseService;

    @Autowired
    SourceTableService sourceTableService;

    @Autowired
    TargetDatabaseService targetDatabaseService;

    @Autowired
    TargetTableService targetTableService;

    @Autowired
    TableMappingService tableMappingService;


    @Override
    public List<ETL> getAllETL() {
        return etlRepository.findAll();
    }

    @Override
    public ETL createDefaultELTSession() {
        ETL etl = new ETL();
        etl.setName("ETL session " + etlRepository.count());
        etl.setTargetDatabase(targetDatabaseService.getDefaultDatabase());
        etl.setSourceDatabase(sourceDatabaseService.getDefaultDatabase("MIMIC"));
        return etlRepository.save(etl);
    }

    @Override
    public ETL createETLSession(MultipartFile file, String cdm) {
        ETL etl = new ETL();
        etl.setName("ETL session " + etlRepository.count());
        etl.setTargetDatabase(targetDatabaseService.getDatabaseByCDM(cdm));
        SourceDatabase sourceDatabase = sourceDatabaseService.createDatabaseFromFile(file);
        etl.setSourceDatabase(sourceDatabase);
        return etlRepository.save(etl);
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
            TargetDatabase database = targetDatabaseService.getDatabaseByCDM(cdm);
            etl.setTargetDatabase(database);

            tableMappingService.removeFromETL(etl_id);

            List<SourceTable> sourceTables = etl.getSourceDatabase().getTables();
            Collections.sort(sourceTables, Comparator.comparingLong(SourceTable::getId));

            List<TargetTable> targetTables = etl.getTargetDatabase().getTables();
            Collections.sort(targetTables, Comparator.comparingLong(TargetTable::getId));

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
    public ByteArrayInputStream createSourceFieldListCSV(Long id) {
        ETL etl = etlRepository.findById(id).orElse(null);

        if (etl != null) {
            List<Row> rows = ETLSummaryGenerator.createSourceFieldList(etl);
            ByteArrayInputStream inputStream = ETLSummaryGenerator.writeCSV("sourceList.csv", rows);

            return inputStream;
        }
        return null;
    }


    @Override
    public void createDocumentationFile(Long id) {
        ETL etl = etlRepository.findById(id).orElse(null);

        if (etl != null) {
            WordDocumentGenerator generator = new WordDocumentGenerator(etl);
            generator.generateWordDocument(etl);
        }
    }

    /*
    @Override
    public ETL createTableMapping(Long etl_id, Long source_id, Long target_id) {
        ETL etl = etlRepository.findById(etl_id).orElse(null);

        if (etl != null) {
            SourceTable source = sourceTableService.getTableById(source_id);
            TargetTable target = targetTableService.getTableById(target_id);

            if (source != null && target != null) {
                TableMapping mapping = tableMappingService.addTableMapping(source, target, etl);

                return etlRepository.save(etl);
            }
        }

        return null;
    }*/


    /*
    @Override
    public ETL createCDMModel(String omop, String cdmVersion) {
        ETL etl = new ETL();
        etl.setId("elt-" + RandomStringUtils.randomAlphanumeric(4));
        etl.setSourceDB(databaseService.getDatabase(omop));
        etl.setTargetDB(databaseService.getDatabase(cdmVersion));

        return etlRepository.save(etl);
    }

    @Override
    public ETL getETLById(String etlId) {
        Optional<ETL> etl = etlRepository.findById(etlId);
        return etl.orElse(null);
    }

    @Override
    public ETL changeTargetBD(String etlId, String cdmId) {
        ETL etl = getETLById(etlId);

        if (etl != null) {
            etl.setTargetDB(databaseService.getDatabase(cdmId));
            return etlRepository.save(etl);
        }
        return null;
    }*/
}
