package com.ua.hiah.service.source.field;

import com.ua.hiah.model.source.SourceField;
import com.ua.hiah.repository.source.SourceFieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SourceFieldServiceImpl implements SourceFieldService{

    @Autowired
    private SourceFieldRepository repository;


    /**
     * Retrieves a field from the EHR database given its id
     *
     * @param source_id field's id
     * @return retrieve field if found, null otherwise
     */

    @Override
    public SourceField getFieldById(Long source_id) {
        return repository.findById(source_id).orElse(null);
    }

    /**
     * Changes the comment a field from the EHR database
     *
     * @param fieldId field's id
     * @param comment comment to change to
     * @return changed field
     */

    @Override
    public SourceField changeComment(Long fieldId, String comment) {
        SourceField field = repository.findById(fieldId).orElse(null);
        if (field != null) {
            field.setComment(comment);
            return repository.save(field);
        }
        return null;
    }
}
