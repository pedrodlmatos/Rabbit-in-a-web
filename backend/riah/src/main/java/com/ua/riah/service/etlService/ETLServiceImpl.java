package com.ua.riah.service.etlService;

import com.ua.riah.model.ETL;
import com.ua.riah.model.source.SourceDatabase;
import com.ua.riah.model.target.TargetDatabase;
import com.ua.riah.repository.ETLRepository;
import com.ua.riah.service.source.sourceDatabaseService.SourceDatabaseService;
import com.ua.riah.service.source.sourceTableService.SourceTableService;
import com.ua.riah.service.tableMapping.TableMappingService;
import com.ua.riah.service.target.targetDatabase.TargetDatabaseService;
import com.ua.riah.service.target.targetTable.TargetTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
        return etlRepository.findById(id).orElse(null);
    }

    @Override
    public ETL changeTargetDatabase(Long etl_id, String cdm) {
        ETL etl = etlRepository.findById(etl_id).orElse(null);

        if (etl != null) {
            TargetDatabase database = targetDatabaseService.getDatabaseByCDM(cdm);
            etl.setTargetDatabase(database);
            tableMappingService.removeFromETL(etl_id);
            return etlRepository.save(etl);
        }
        return null;
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
