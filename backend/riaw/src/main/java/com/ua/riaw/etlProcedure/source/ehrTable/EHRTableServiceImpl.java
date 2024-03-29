package com.ua.riaw.etlProcedure.source.ehrTable;

import com.ua.riaw.etlProcedure.source.ehrField.EHRField;
import com.ua.riaw.etlProcedure.source.ehrField.EHRFieldRepository;
import com.ua.riaw.etlProcedure.source.valueCounts.ValueCountRepository;
import com.ua.riaw.utils.error.exceptions.EntityNotFoundException;
import com.ua.riaw.utils.error.exceptions.UnauthorizedAccessException;
import com.ua.riaw.etlProcedure.ETLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EHRTableServiceImpl implements EHRTableService {

    @Autowired
    private EHRTableRepository repository;

    @Autowired
    private EHRFieldRepository fieldRepository;

    @Autowired
    private ValueCountRepository valueCountRepository;

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
    @Transactional
    public void removeStemTable(EHRTable table) {
        for (EHRField field : table.getFields()) {
            // delete all value count from field
            valueCountRepository.deleteAll(field.getValueCounts());
            // delete field
            fieldRepository.delete(field);
        }
        // delete table
        repository.delete(table);
    }
}
