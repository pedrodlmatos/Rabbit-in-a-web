package com.ua.riah.service.source.sourceTableService;

import com.ua.riah.model.source.SourceDatabase;
import com.ua.riah.model.source.SourceTable;

public interface SourceTableService {

    SourceTable createTable(SourceDatabase database, String tableName, String description, int n_rows, int n_rows_checked);

    SourceTable getTableById(Long id);
}
