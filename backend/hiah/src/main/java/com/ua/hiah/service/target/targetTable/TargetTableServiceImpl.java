package com.ua.hiah.service.target.targetTable;

import com.ua.hiah.model.target.TargetDatabase;
import com.ua.hiah.model.target.TargetTable;
import com.ua.hiah.repository.target.TargetTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TargetTableServiceImpl implements TargetTableService {

    @Autowired
    private TargetTableRepository repository;

    @Override
    public TargetTable createTable(TargetDatabase database, String tableName) {
        TargetTable table = new TargetTable();
        table.setName(tableName);
        table.setTargetDatabase(database);
        table.setComment(tableName);

        return repository.save(table);
    }

    @Override
    public TargetTable getTableById(Long target_id) {
        return repository.findById(target_id).orElse(null);
    }

    @Override
    public TargetTable changeComment(Long tableId, String comment) {
        TargetTable table = repository.findById(tableId).orElse(null);

        if (table != null) {
            table.setComment(comment);
            return repository.save(table);
        }

        return null;
    }
}
