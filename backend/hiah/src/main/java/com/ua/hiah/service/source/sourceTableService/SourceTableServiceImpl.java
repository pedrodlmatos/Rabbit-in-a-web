package com.ua.hiah.service.source.sourceTableService;

import com.ua.hiah.model.source.SourceDatabase;
import com.ua.hiah.model.source.SourceTable;
import com.ua.hiah.repository.source.SourceTableRepository;
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
        table.setComment(tableName);
        table.setRowCount(n_rows);
        table.setRowsCheckedCount(n_rows_checked);

        return repository.save(table);
    }

    @Override
    public SourceTable getTableById(Long id) {
        return repository.findById(id).orElse(null);
    }


    /**
     *
     * @param tableId
     * @param comment
     * @return
     */
    @Override
    public SourceTable changeComment(Long tableId, String comment) {
        SourceTable table = repository.findById(tableId).orElse(null);

        if (table != null) {
            table.setComment(comment);
            return repository.save(table);
        }

        return null;
    }
}
