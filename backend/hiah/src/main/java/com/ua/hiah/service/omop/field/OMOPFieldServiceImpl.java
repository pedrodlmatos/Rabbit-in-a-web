package com.ua.hiah.service.omop.field;

import com.ua.hiah.error.exceptions.EntityNotFoundException;
import com.ua.hiah.error.exceptions.UnauthorizedAccessException;
import com.ua.hiah.model.omop.OMOPField;
import com.ua.hiah.repository.target.OMOPFieldRepository;
import com.ua.hiah.service.etl.ETLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OMOPFieldServiceImpl implements OMOPFieldService {

    @Autowired
    private OMOPFieldRepository repository;

    @Autowired
    private ETLService etlService;


    /**
     * Retrieves field from OMOP CDM database by id
     *
     * @param targetFieldId field's id
     * @return retrieved field if found, null otherwise
     */
    @Override
    public OMOPField getFieldById(Long targetFieldId) {
        return repository.findById(targetFieldId).orElseThrow(() -> new EntityNotFoundException(OMOPField.class, "id", targetFieldId.toString()));
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
    public OMOPField changeComment(Long targetFieldId, String comment, Long etl_id, String username) {
        if (etlService.userHasAccessToEtl(etl_id, username)) {
            OMOPField field = repository.findById(targetFieldId).orElseThrow(() -> new EntityNotFoundException(OMOPField.class, "id", targetFieldId.toString()));
            if (field.getComment() != null && field.getComment().equals(comment)) return field;      // old comment is equals to new
            else {                                                                                   // new comment is different
                field.setComment(comment);
                // update modification date
                etlService.updateModificationDate(etl_id);
                return repository.save(field);
            }
        } else
            throw new UnauthorizedAccessException(OMOPField.class, username, targetFieldId);
    }
}
