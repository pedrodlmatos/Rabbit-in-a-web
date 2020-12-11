package com.ua.riah.service.dbTableService;

import com.ua.riah.model.Database;
import com.ua.riah.model.DBTable;
import com.ua.riah.repository.DBTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DBTableServiceImpl implements DBTableService {

    @Autowired
    private DBTableRepository repository;

    @Override
    public List<DBTable> getAllTables() {
        return repository.findAll();
    }

    @Override
    public DBTable getTable(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public DBTable createTable(Database trgDB, String name) {
        DBTable table = new DBTable();
        table.setId(String.format("t%s-%s", trgDB.getDbName().toLowerCase(), name));
        table.setName(name);
        table.setDatabase(trgDB);

        return repository.save(table);
    }

    @Override
    public DBTable createTable(Database database, String tableName, String description, int n_rows, int n_rows_checked) {
        DBTable table = new DBTable();
        table.setId(String.format("t%s-%s", database.getDbName().toLowerCase(), tableName));
        table.setName(tableName);
        table.setDatabase(database);
        table.setDescription(description);
        table.setRowCount(n_rows);
        table.setRowsCheckedCount(n_rows_checked);

        return repository.save(table);
    }

}
