package com.ua.hiah.service.target.targetTable;

import com.ua.hiah.model.target.TargetDatabase;
import com.ua.hiah.model.target.TargetTable;

public interface TargetTableService {

    TargetTable createTable(TargetDatabase database, String tableName);

    TargetTable getTableById(Long target_id);

    TargetTable changeComment(Long tableId, String comment);
}
