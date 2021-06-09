package com.ua.hiah.service.ehr.field;

import com.ua.hiah.model.ehr.EHRField;

public interface EHRFieldService {


    /**
     * Retrieves a field from the EHR database given its id
     *
     * @param source_id field's id
     * @return retrieve field if found, null otherwise
     */

    EHRField getFieldById(Long source_id);


    /**
     * Changes the comment a field from the EHR database
     *
     * @param field field's id
     * @param comment comment to change to
     * @param etl_id ETL procedure's id
     * @param username User's username
     * @return changed field
     */

    EHRField changeComment(Long field, String comment, Long etl_id, String username);
}
