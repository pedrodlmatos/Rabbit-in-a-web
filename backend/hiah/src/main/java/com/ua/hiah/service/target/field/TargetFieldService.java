package com.ua.hiah.service.target.field;

import com.ua.hiah.model.target.TargetField;

public interface TargetFieldService {


    /**
     * Retrieves field from OMOP CDM database by id
     *
     * @param target_id field's id
     * @return retrieved field if found, null otherwise
     */

    TargetField getFieldById(Long target_id);


    /**
     * Changes comment of a field from the OMOP CDM database
     *
     * @param field field's id
     * @param comment comment to change to
     * @return altered field
     */

    TargetField changeComment(Long field, String comment);
}
