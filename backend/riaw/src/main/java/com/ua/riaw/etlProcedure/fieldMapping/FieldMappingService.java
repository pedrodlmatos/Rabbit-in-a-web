package com.ua.riaw.etlProcedure.fieldMapping;

import java.util.List;

public interface FieldMappingService {

    /**
     * Creates a field mapping
     *
     * @param ehrFieldId source field's id
     * @param omopTableId target field's id
     * @param tableMappingId table mapping's id
     * @param etl_id ETL procedure's id
     * @param username User's username
     * @return created table mapping
     */

    FieldMapping addFieldMapping(Long ehrFieldId, Long omopTableId, Long tableMappingId, Long etl_id, String username);


    /**
     * Deletes a field mapping
     *
     * @param fieldMappingId field mapping id
     * @param etl_id ETL procedure's id
     * @param username User's username
     */

    void removeFieldMapping(Long fieldMappingId, Long etl_id, String username);


    /**
     * Changes the logic of a given field mapping
     *
     * @param fieldMappingId field mapping's id
     * @param logic logic to change to
     * @param etl_id ETL procedure's id
     * @param username user's username
     * @return altered logic
     */

    FieldMapping changeMappingLogic(Long fieldMappingId, String logic, Long etl_id, String username);

























    /**
     * Retrieved all field mappings of a table mapping
     *
     * @param tableMappingId table mapping id
     * @return list of field mappings
     */

    List<FieldMapping> getFieldMappingsFromTableMapping(Long tableMappingId);





}
