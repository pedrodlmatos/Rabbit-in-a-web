package com.ua.hiah.service.target.table;

import com.ua.hiah.model.target.TargetTable;

public interface TargetTableService {

    TargetTable createTargetTable(TargetTable table);

    TargetTable getTableById(Long target_id);

    TargetTable changeComment(Long tableId, String comment);
}
