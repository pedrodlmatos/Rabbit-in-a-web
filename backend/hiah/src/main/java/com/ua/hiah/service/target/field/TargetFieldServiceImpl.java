package com.ua.hiah.service.target.field;

import com.ua.hiah.model.target.TargetField;
import com.ua.hiah.repository.target.TargetFieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TargetFieldServiceImpl implements TargetFieldService {

    @Autowired
    private TargetFieldRepository repository;


    /**
     * Retrieves field from OMOP CDM database by id
     *
     * @param target_id field's id
     * @return retrieved field if found, null otherwise
     */
    @Override
    public TargetField getFieldById(Long target_id) {
        return repository.findById(target_id).orElse(null);
    }


    /**
     * Changes comment of a field from the OMOP CDM database
     *
     * @param fieldId field's id
     * @param comment comment to change to
     * @return altered field
     */

    @Override
    public TargetField changeComment(Long fieldId, String comment) {
        TargetField field = repository.findById(fieldId).orElse(null);
        if (field == null) return null;                                 // field not found
        else if (field.getComment().equals(comment)) return field;      // old comment is equals to new
        else {                                                          // new comment is different
            field.setComment(comment);
            return repository.save(field);
        }
    }
}
