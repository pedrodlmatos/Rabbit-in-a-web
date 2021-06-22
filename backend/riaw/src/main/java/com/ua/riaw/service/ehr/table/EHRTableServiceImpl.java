package com.ua.riaw.service.ehr.table;

import com.ua.riaw.error.exceptions.EntityNotFoundException;
import com.ua.riaw.error.exceptions.UnauthorizedAccessException;
import com.ua.riaw.model.ehr.EHRTable;
import com.ua.riaw.repository.ehr.EHRTableRepository;
import com.ua.riaw.service.etl.ETLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EHRTableServiceImpl implements EHRTableService {

    @Autowired
    private EHRTableRepository repository;

    @Autowired
    private ETLService etlService;


    /**
     * Retrieves a table from the EHR database given its id
     *
     * @param id table's id
     * @return retrieved table if found, null otherwise
     */

    @Override
    public EHRTable getTableById(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(EHRTable.class, "id", id.toString()));
    }


    /**
     * Changes the comment of a table of the EHR database
     *
     * @param ehrTableId table's id
     * @param comment comment to change to
     * @param etl_id ETL procedure's id
     * @param username User's username
     * @return changed table
     */

    @Override
    public EHRTable changeComment(Long ehrTableId, String comment, Long etl_id, String username) {

        if (etlService.userHasAccessToEtl(etl_id, username)) {
            // table not found
            EHRTable table = repository.findById(ehrTableId).orElseThrow(() -> new EntityNotFoundException(EHRTable.class, "id", ehrTableId.toString()));

            if (table.getComment() != null && table.getComment().equals(comment)) return table;             // old comment is equals to new
            else {                                                                                          // new comment is different
                table.setComment(comment);
                etlService.updateModificationDate(etl_id);
                return repository.save(table);
            }
        } else
            throw new UnauthorizedAccessException(EHRTable.class, username, ehrTableId);
    }


    /**
     * Removes stem table and its mappings
     *
     * @param table stem table on the EHR database
     */

    @Override
    public void removeStemTable(EHRTable table) {
        repository.delete(table);
    }
}
