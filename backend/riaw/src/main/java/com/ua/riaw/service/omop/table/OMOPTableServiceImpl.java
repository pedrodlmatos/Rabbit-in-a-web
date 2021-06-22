package com.ua.riaw.service.omop.table;

import com.ua.riaw.error.exceptions.EntityNotFoundException;
import com.ua.riaw.error.exceptions.UnauthorizedAccessException;
import com.ua.riaw.model.omop.OMOPTable;
import com.ua.riaw.repository.target.OMOPTableRepository;
import com.ua.riaw.security.services.UserDetailsServiceImpl;
import com.ua.riaw.service.etl.ETLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OMOPTableServiceImpl implements OMOPTableService {

    @Autowired
    private OMOPTableRepository repository;

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
    public OMOPTable getTableById(Long target_id) {
        return repository.findById(target_id).orElseThrow(() -> new EntityNotFoundException(OMOPTable.class, "id", target_id.toString()));
    }


    /**
     * Changes comment of a table from the OMOP CDM database
     *
     * @param omopTableId table's id
     * @param comment comment to change tp
     * @param etl_id ETL procedure's id
     * @param username user's username
     * @return altered table
     */

    @Override
    public OMOPTable changeComment(Long omopTableId, String comment, Long etl_id, String username) {
        if (etlService.userHasAccessToEtl(etl_id, username)) {
            // table not found
            OMOPTable table = repository.findById(omopTableId).orElseThrow(() -> new EntityNotFoundException(OMOPTable.class, "id", omopTableId.toString()));

            if (table.getComment() != null && table.getComment().equals(comment)) return table;               // old comment == new comment
            else {                                                                                            // old comment != new comment
                table.setComment(comment);
                etlService.updateModificationDate(etl_id);
                return repository.save(table);
            }
        } else
            throw new UnauthorizedAccessException(OMOPTable.class, username, omopTableId);
    }


    /**
     * Deletes stem table and its mappings
     *
     * @param table stem table on the OMOP CDM database
     */

    @Override
    public void removeStemTable(OMOPTable table) {
        repository.delete(table);
    }
}
