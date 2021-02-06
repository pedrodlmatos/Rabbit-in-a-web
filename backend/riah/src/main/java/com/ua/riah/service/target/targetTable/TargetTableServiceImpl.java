package com.ua.riah.service.target.targetTable;

import com.ua.riah.model.target.TargetDatabase;
import com.ua.riah.model.target.TargetTable;
import com.ua.riah.repository.target.TargetTableRepository;
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

        return repository.save(table);
    }

    @Override
    public TargetTable getTableById(Long target_id) {
        return repository.findById(target_id).orElse(null);
    }
}