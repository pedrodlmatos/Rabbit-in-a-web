package com.ua.riah.service.dbTableService;

import com.ua.riah.model.Database;
import com.ua.riah.model.DBTable;

import java.util.List;

public interface DBTableService {

    List<DBTable> getAllTables();

    DBTable getTable(String id);

    DBTable createTable(Database trgDB, String name);

    DBTable createTable(Database database, String tableName, String description, int n_rows, int n_rows_checked);
}
