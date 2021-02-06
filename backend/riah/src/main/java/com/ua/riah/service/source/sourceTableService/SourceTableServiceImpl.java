package com.ua.riah.service.source.sourceTableService;

import com.ua.riah.model.source.SourceDatabase;
import com.ua.riah.model.source.SourceTable;
import com.ua.riah.repository.source.SourceTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SourceTableServiceImpl implements SourceTableService {

    @Autowired
    private SourceTableRepository repository;

    @Override
    public SourceTable createTable(SourceDatabase database, String tableName, String description, int n_rows, int n_rows_checked) {
        SourceTable table = new SourceTable();
        table.setName(tableName);
        table.setSourceDatabase(database);
        table.setDescription(description);
        table.setRowCount(n_rows);
        table.setRowsCheckedCount(n_rows_checked);

        return repository.save(table);
    }

    @Override
    public SourceTable getTableById(Long id) {
        return repository.findById(id).orElse(null);
    }
}
