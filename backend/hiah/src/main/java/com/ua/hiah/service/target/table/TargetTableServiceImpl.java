package com.ua.hiah.service.target.table;

import com.ua.hiah.error.exceptions.EntityNotFoundException;
import com.ua.hiah.error.exceptions.UnauthorizedAccessException;
import com.ua.hiah.model.target.TargetTable;
import com.ua.hiah.repository.target.TargetTableRepository;
import com.ua.hiah.security.services.UserDetailsServiceImpl;
import com.ua.hiah.service.etl.ETLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TargetTableServiceImpl implements TargetTableService {

    @Autowired
    private TargetTableRepository repository;

    @Autowired
    private ETLService etlService;

    @Autowired
    private UserDetailsServiceImpl userService;


    /**
     * Retrieves table from OMOP CDM database given its id
     *
     * @param target_id table's id
     * @return retrieved table if found, null otherwise
     */

    @Override
    public TargetTable getTableById(Long target_id) {
        return repository.findById(target_id).orElseThrow(() -> new EntityNotFoundException(TargetTable.class, "id", target_id.toString()));
    }


    /**
     * Changes comment of a table from the OMOP CDM database
     *
     * @param tableId table's id
     * @param comment comment to change tp
     * @param etl_id ETL procedure's id
     * @param username user's username
     * @return altered table
     */

    @Override
    public TargetTable changeComment(Long tableId, String comment, Long etl_id, String username) {
        if (etlService.userHasAccessToEtl(etl_id, username)) {
            // table not found
            TargetTable table = repository.findById(tableId).orElseThrow(() -> new EntityNotFoundException(TargetTable.class, "id", tableId.toString()));

            if (table.getComment() != null && table.getComment().equals(comment)) return table;               // old comment == new comment
            else {                                                                                            // old comment != new comment
                table.setComment(comment);
                etlService.updateModificationDate(etl_id);
                return repository.save(table);
            }
        } else
            throw new UnauthorizedAccessException(TargetTable.class, username, tableId);
    }


    /**
     * Deletes stem table and its mappings
     *
     * @param table stem table on the OMOP CDM database
     */

    @Override
    public void removeStemTable(TargetTable table) {
        repository.delete(table);
    }
}
