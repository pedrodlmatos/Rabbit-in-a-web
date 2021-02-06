package com.ua.riah.service.fieldMapping;

import com.ua.riah.model.FieldMapping;

import java.util.List;

public interface FieldMappingService {

    FieldMapping addFieldMapping(Long source_id, Long target_id, Long tableMapId);

    FieldMapping removeFieldMapping(Long fieldMappingId);

    List<FieldMapping> getFieldMappingsFromTableMapping(Long tableMappingId);
}
