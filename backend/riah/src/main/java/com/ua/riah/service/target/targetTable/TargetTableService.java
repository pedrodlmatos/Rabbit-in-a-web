package com.ua.riah.service.target.targetTable;

import com.ua.riah.model.target.TargetDatabase;
import com.ua.riah.model.target.TargetTable;

public interface TargetTableService {

    TargetTable createTable(TargetDatabase database, String tableName);

    TargetTable getTableById(Long target_id);
}
