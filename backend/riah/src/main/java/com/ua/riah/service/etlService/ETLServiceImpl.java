package com.ua.riah.service.etlService;

import com.ua.riah.model.Database;
import com.ua.riah.model.CDMVersion;
import com.ua.riah.model.ETL;
import com.ua.riah.repository.ETLRepository;
import com.ua.riah.service.databaseService.DatabaseService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ETLServiceImpl implements ETLService {

    @Autowired
    ETLRepository etlRepository;

    @Autowired
    DatabaseService databaseService;

    @Override
    public ETL createCDMModel(String omop, String cdmVersion) {
        ETL etl = new ETL();
        etl.setId("elt-" + RandomStringUtils.randomAlphanumeric(4));
        etl.setSourceDB(databaseService.getDatabase(omop));
        etl.setTargetDB(databaseService.getDatabase(cdmVersion));

        return etlRepository.save(etl);
    }
}
