package com.ua.riaw.etlProcedure.source.ehrField;

import com.ua.riaw.utils.error.exceptions.EntityNotFoundException;
import com.ua.riaw.utils.error.exceptions.UnauthorizedAccessException;
import com.ua.riaw.etlProcedure.ETLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EHRFieldServiceImpl implements EHRFieldService {

    @Autowired
    private EHRFieldRepository repository;

    @Autowired
    private ETLService etlService;


    /**
     * Retrieves a field from the EHR database given its id
     *
     * @param sourceFieldId field's id
     * @return retrieve field if found, null otherwise
     */

    @Override
    public EHRField getFieldById(Long sourceFieldId) {
        return repository.findById(sourceFieldId).orElseThrow(() -> new EntityNotFoundException(EHRField.class, "id", sourceFieldId.toString()));
    }


    /**
     * Changes the comment a field from the EHR database
     *
     * @param ehrFieldId field's id
     * @param comment comment to change to
     * @param etl_id ETL procedure's id
     * @param username User's username
     * @return changed field
     */

    @Override
    public EHRField changeComment(Long ehrFieldId, String comment, Long etl_id, String username) {
        if (etlService.userHasAccessToEtl(etl_id, username)) {
            EHRField field = repository.findById(ehrFieldId).orElseThrow(() -> new EntityNotFoundException(EHRField.class, "id", ehrFieldId.toString()));

            if (field.getComment() != null && field.getComment().equals(comment)) return field;      // old comment is equal to new comment
            else {                                                                                   // new comment is different
                field.setComment(comment);
                // update modification date
                etlService.updateModificationDate(etl_id);
                return repository.save(field);
            }
        } else
            throw new UnauthorizedAccessException(EHRField.class, username, ehrFieldId);
    }
}
