package com.ua.hiah.service.source.table;

import com.ua.hiah.model.source.SourceTable;

public interface SourceTableService {
    SourceTable createTable(SourceTable table);

    SourceTable getTableById(Long id);

    SourceTable changeComment(Long tableId, String comment);
}
