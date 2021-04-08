package com.ua.hiah.service.source.database;

import com.ua.hiah.model.source.SourceDatabase;
import org.springframework.web.multipart.MultipartFile;

public interface SourceDatabaseService {

    /**
     * Reads Scan report generated by White Rabbit and persists its information
     *
     * @param name EHR database name
     * @param file Scan report file
     * @return Source Database (with its tables, fields and value counts)
     */

    SourceDatabase createDatabaseFromScanReport(String name, MultipartFile file);
}
