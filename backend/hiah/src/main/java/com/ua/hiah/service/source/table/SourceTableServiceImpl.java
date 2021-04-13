package com.ua.hiah.service.source.table;

import com.ua.hiah.model.source.SourceTable;
import com.ua.hiah.repository.source.SourceTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SourceTableServiceImpl implements SourceTableService {

    @Autowired
    private SourceTableRepository repository;


    /**
     * Retrieves a table from the EHR database given its id
     *
     * @param id table's id
     * @return retrieved table if found, null otherwise
     */

    @Override
    public SourceTable getTableById(Long id) {
        return repository.findById(id).orElse(null);
    }


    /**
     * Changes the comment of a table of the EHR database
     *
     * @param tableId table's id
     * @param comment comment to change to
     * @return changed table
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
