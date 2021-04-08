package com.ua.hiah.service.source.field;

import com.ua.hiah.model.source.SourceField;

public interface SourceFieldService {


    /**
     * Retrieves a field from the EHR database given its id
     *
     * @param source_id field's id
     * @return retrieve field if found, null otherwise
     */

    SourceField getFieldById(Long source_id);


    /**
     * Changes the comment a field from the EHR database
     *
     * @param field field's id
     * @param comment comment to change to
     * @return changed field
     */

    SourceField changeComment(Long field, String comment);
}
