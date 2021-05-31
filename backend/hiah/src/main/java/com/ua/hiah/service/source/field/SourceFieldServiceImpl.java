package com.ua.hiah.service.source.field;

import com.ua.hiah.error.exceptions.EntityNotFoundException;
import com.ua.hiah.error.exceptions.UnauthorizedAccessException;
import com.ua.hiah.model.source.SourceField;
import com.ua.hiah.repository.source.SourceFieldRepository;
import com.ua.hiah.service.etl.ETLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SourceFieldServiceImpl implements SourceFieldService{

    @Autowired
    private SourceFieldRepository repository;

    @Autowired
    private ETLService etlService;


    /**
     * Retrieves a field from the EHR database given its id
     *
     * @param sourceFieldId field's id
     * @return retrieve field if found, null otherwise
     */

    @Override
    public SourceField getFieldById(Long sourceFieldId) {
        return repository.findById(sourceFieldId).orElseThrow(() -> new EntityNotFoundException(SourceField.class, "id", sourceFieldId.toString()));
    }


    /**
     * Changes the comment a field from the EHR database
     *
     * @param sourceFieldId field's id
     * @param comment comment to change to
     * @param etl_id ETL procedure's id
     * @param username User's username
     * @return changed field
     */

    @Override
    public SourceField changeComment(Long sourceFieldId, String comment, Long etl_id, String username) {
        if (etlService.userHasAccessToEtl(etl_id, username)) {
            SourceField field = repository.findById(sourceFieldId).orElseThrow(() -> new EntityNotFoundException(SourceField.class, "id", sourceFieldId.toString()));

            if (field.getComment() != null && field.getComment().equals(comment)) return field;      // old comment is equal to new comment
            else {                                                                                   // new comment is different
                field.setComment(comment);
                // update modification date
                etlService.updateModificationDate(etl_id);
                return repository.save(field);
            }
        } else
            throw new UnauthorizedAccessException(SourceField.class, username, sourceFieldId);
    }
}
