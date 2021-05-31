package com.ua.hiah.service.target.field;

import com.ua.hiah.error.exceptions.EntityNotFoundException;
import com.ua.hiah.error.exceptions.UnauthorizedAccessException;
import com.ua.hiah.model.target.TargetField;
import com.ua.hiah.repository.target.TargetFieldRepository;
import com.ua.hiah.service.etl.ETLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TargetFieldServiceImpl implements TargetFieldService {

    @Autowired
    private TargetFieldRepository repository;

    @Autowired
    private ETLService etlService;


    /**
     * Retrieves field from OMOP CDM database by id
     *
     * @param targetFieldId field's id
     * @return retrieved field if found, null otherwise
     */
    @Override
    public TargetField getFieldById(Long targetFieldId) {
        return repository.findById(targetFieldId).orElseThrow(() -> new EntityNotFoundException(TargetField.class, "id", targetFieldId.toString()));
    }


    /**
     * Changes comment of a field from the OMOP CDM database
     *
     * @param targetFieldId field's id
     * @param comment comment to change to
     * @param etl_id ETL procedure's id
     * @param username User's username
     * @return altered field
     */

    @Override
    public TargetField changeComment(Long targetFieldId, String comment, Long etl_id, String username) {
        if (etlService.userHasAccessToEtl(etl_id, username)) {
            TargetField field = repository.findById(targetFieldId).orElseThrow(() -> new EntityNotFoundException(TargetField.class, "id", targetFieldId.toString()));
            if (field.getComment() != null && field.getComment().equals(comment)) return field;      // old comment is equals to new
            else {                                                                                   // new comment is different
                field.setComment(comment);
                // update modification date
                etlService.updateModificationDate(etl_id);
                return repository.save(field);
            }
        } else
            throw new UnauthorizedAccessException(TargetField.class, username, targetFieldId);
    }
}
