package com.ua.hiah.service.source.table;

import com.ua.hiah.error.exceptions.EntityNotFoundException;
import com.ua.hiah.error.exceptions.UnauthorizedAccessException;
import com.ua.hiah.model.source.SourceTable;
import com.ua.hiah.repository.source.SourceTableRepository;
import com.ua.hiah.service.etl.ETLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SourceTableServiceImpl implements SourceTableService {

    @Autowired
    private SourceTableRepository repository;

    @Autowired
    private ETLService etlService;


    /**
     * Retrieves a table from the EHR database given its id
     *
     * @param id table's id
     * @return retrieved table if found, null otherwise
     */

    @Override
    public SourceTable getTableById(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(SourceTable.class, "id", id.toString()));
    }


    /**
     * Changes the comment of a table of the EHR database
     *
     * @param table_id table's id
     * @param comment comment to change to
     * @param etl_id ETL procedure's id
     * @param username User's username
     * @return changed table
     */

    @Override
    public SourceTable changeComment(Long table_id, String comment, Long etl_id, String username) {

        if (etlService.userHasAccessToEtl(etl_id, username)) {
            // table not found
            SourceTable table = repository.findById(table_id).orElseThrow(() -> new EntityNotFoundException(SourceTable.class, "id", table_id.toString()));

            if (table.getComment() != null && table.getComment().equals(comment)) return table;             // old comment is equals to new
            else {                                                                                          // new comment is different
                table.setComment(comment);
                etlService.updateModificationDate(etl_id);
                return repository.save(table);
            }
        } else
            throw new UnauthorizedAccessException(SourceTable.class, username, table_id);
    }


    /**
     * Removes stem table and its mappings
     *
     * @param table stem table on the EHR database
     */

    @Override
    public void removeStemTable(SourceTable table) {
        repository.delete(table);
    }
}
