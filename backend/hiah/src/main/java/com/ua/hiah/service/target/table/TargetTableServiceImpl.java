package com.ua.hiah.service.target.table;

import com.ua.hiah.model.target.TargetField;
import com.ua.hiah.model.target.TargetTable;
import com.ua.hiah.repository.target.TargetTableRepository;
import com.ua.hiah.service.target.field.TargetFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TargetTableServiceImpl implements TargetTableService {

    @Autowired
    private TargetTableRepository repository;

    @Autowired
    private TargetFieldService fieldService;

    @Override
    public TargetTable createTargetTable(TargetTable table) {
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

    @Override
    public void deleteTablesByDatabaseId(Long database_id) {
        for (TargetTable table : repository.findAllByTargetDatabaseId(database_id)) {
            for (TargetField field : table.getFields()) {
                //fieldService.removeField
            }
        }

    }
}
