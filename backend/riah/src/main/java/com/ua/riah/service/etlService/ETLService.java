package com.ua.riah.service.etlService;

import com.ua.riah.model.ETL;

public interface ETLService {

    ETL createCDMModel(String omop, String cdmVersion);
}
