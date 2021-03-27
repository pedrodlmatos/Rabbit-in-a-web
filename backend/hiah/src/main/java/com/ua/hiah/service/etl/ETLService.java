package com.ua.hiah.service.etl;

import com.ua.hiah.model.ETL;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ETLService {

    ETL createETLSession(String name, MultipartFile file, String cdm);

    List<ETL> getAllETL();

    ETL getETLWithId(Long id);

    ETL changeTargetDatabase(Long etl_id, String cdm);

    ETL changeComment(Long id, Long tableId, String comment);

    byte[] createSourceFieldListCSV(Long id);

    void createDocumentationFile(Long id);
}
