package com.ua.hiah.service.ehr.field;

import com.ua.hiah.error.exceptions.EntityNotFoundException;
import com.ua.hiah.error.exceptions.UnauthorizedAccessException;
import com.ua.hiah.model.ehr.EHRField;
import com.ua.hiah.repository.ehr.EHRFieldRepository;
import com.ua.hiah.service.etl.ETLService;
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
