package com.ua.riah.service.source.sourceDatabaseService;

import com.ua.riah.model.ETL;
import com.ua.riah.model.source.SourceDatabase;
import org.springframework.web.multipart.MultipartFile;

public interface SourceDatabaseService {

    //void loadScanReport();

    SourceDatabase getDefaultDatabase(String name);

    SourceDatabase createDatabaseFromFile(MultipartFile file);
}
