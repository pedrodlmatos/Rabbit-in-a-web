package com.ua.hiah.service.fieldMapping;

import com.ua.hiah.model.FieldMapping;

import java.util.List;

public interface FieldMappingService {

    FieldMapping addFieldMapping(Long source_id, Long target_id, Long tableMapId);

    FieldMapping removeFieldMapping(Long fieldMappingId);

    List<FieldMapping> getFieldMappingsFromTableMapping(Long tableMappingId);
}
