package com.ua.hiah.service.source.sourceTableService;

import com.ua.hiah.model.source.SourceDatabase;
import com.ua.hiah.model.source.SourceTable;

public interface SourceTableService {

    SourceTable createTable(SourceDatabase database, String tableName, String description, int n_rows, int n_rows_checked);

    SourceTable getTableById(Long id);

    SourceTable changeComment(Long tableId, String comment);
}
