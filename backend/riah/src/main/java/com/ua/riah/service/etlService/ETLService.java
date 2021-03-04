package com.ua.riah.service.etlService;

import com.ua.riah.model.ETL;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ETLService {

    List<ETL> getAllETL();

    ETL createDefaultELTSession();

    ETL createETLSession(MultipartFile file, String cdm);

    ETL getETLWithId(Long id);

    ETL changeTargetDatabase(Long etl_id, String cdm);

    void createDocumentationFile(Long id);

    ETL changeComment(Long id, Long tableId, String comment);
}
