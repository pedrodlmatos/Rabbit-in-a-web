package com.ua.hiah.service.source.database;

import com.ua.hiah.model.source.SourceDatabase;
import org.springframework.web.multipart.MultipartFile;

public interface SourceDatabaseService {

    SourceDatabase createDatabaseFromScanReport(String name, MultipartFile file);
}
