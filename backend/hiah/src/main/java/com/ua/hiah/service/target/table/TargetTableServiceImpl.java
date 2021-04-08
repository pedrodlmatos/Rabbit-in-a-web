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


    /**
     * Retrieves table from OMOP CDM database given its id
     *
     * @param target_id table's id
     * @return retrieved table if found, null otherwise
     */

    @Override
    public TargetTable getTableById(Long target_id) {
        return repository.findById(target_id).orElse(null);
    }


    /**
     * Changes comment of a table from the OMOP CDM database
     *
     * @param tableId table's id
     * @param comment comment to change tp
     * @return altered table
     */

    @Override
    public TargetTable changeComment(Long tableId, String comment) {
        TargetTable table = repository.findById(tableId).orElse(null);
        if (table != null) {
            table.setComment(comment);
            return repository.save(table);
        }
        return null;
    }


    /**
     * Deletes
     *
     * @param database_id database's id
     */

    @Override
    public void deleteTablesByDatabaseId(Long database_id) {
        for (TargetTable table : repository.findAllByTargetDatabaseId(database_id)) {
            for (TargetField field : table.getFields()) {
                //fieldService.removeField
            }
        }

    }
}
