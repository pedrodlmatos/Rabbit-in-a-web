package com.ua.hiah.service.source.sourceDatabaseService;

import com.ua.hiah.model.source.SourceDatabase;
import org.springframework.web.multipart.MultipartFile;

public interface SourceDatabaseService {

    //void loadScanReport();

    SourceDatabase getDefaultDatabase(String name);

    SourceDatabase createDatabaseFromFile(MultipartFile file);
}
