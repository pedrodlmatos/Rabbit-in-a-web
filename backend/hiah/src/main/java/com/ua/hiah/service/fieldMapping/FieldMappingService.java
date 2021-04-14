package com.ua.hiah.service.fieldMapping;

import com.ua.hiah.model.FieldMapping;

import java.util.List;

public interface FieldMappingService {

    /**
     * Creates a field mapping
     *
     * @param sourceFieldId source field's id
     * @param targetTableId target field's id
     * @param tableMappingId table mapping's id
     * @return created table mapping
     */

    FieldMapping addFieldMapping(Long sourceFieldId, Long targetTableId, Long tableMappingId);


    /**
     * Deletes a field mapping
     *
     * @param fieldMappingId field mapping id
     * @return deleted mapping
     */

    FieldMapping removeFieldMapping(Long fieldMappingId);


    /**
     * Retrieved all field mappings of a table mapping
     *
     * @param tableMappingId table mapping id
     * @return list of field mappings
     */

    List<FieldMapping> getFieldMappingsFromTableMapping(Long tableMappingId);


    /**
     * Changes the logic of a given field mapping
     *
     * @param fieldMappingId field mapping's id
     * @param logic logic to change to
     * @return altered logic
     */

    FieldMapping changeMappingLogic(Long fieldMappingId, String logic);
}
