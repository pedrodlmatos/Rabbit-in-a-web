package com.ua.riaw.etlProcedure.target.omopField;

public interface OMOPFieldService {


    /**
     * Retrieves field from OMOP CDM database by id
     *
     * @param target_id field's id
     * @return retrieved field if found, null otherwise
     */

    OMOPField getFieldById(Long target_id);


    /**
     * Changes comment of a field from the OMOP CDM database
     *
     * @param field field's id
     * @param comment comment to change to
     * @param etl_id ETL procedure's id
     * @param username User's username
     * @return altered field
     */

    OMOPField changeComment(Long field, String comment, Long etl_id, String username);
}
