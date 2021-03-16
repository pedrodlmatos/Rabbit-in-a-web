package com.ua.hiah.service.fieldMapping;

import com.ua.hiah.model.FieldMapping;
import com.ua.hiah.repository.FieldMappingRepository;
import com.ua.hiah.service.source.sourceFieldService.SourceFieldService;
import com.ua.hiah.service.tableMapping.TableMappingService;
import com.ua.hiah.service.target.targetField.TargetFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FieldMappingServiceImpl implements FieldMappingService {

    @Autowired
    private FieldMappingRepository repository;

    @Autowired
    private SourceFieldService sourceFieldService;

    @Autowired
    private TargetFieldService targetFieldService;

    @Autowired
    private TableMappingService tableMappingService;


    @Override
    public FieldMapping addFieldMapping(Long source_id, Long target_id, Long tableMapId) {
        FieldMapping mapping = new FieldMapping();
        mapping.setSource(sourceFieldService.getFieldById(source_id));
        mapping.setTarget(targetFieldService.getFieldById(target_id));
        mapping.setTableMapping(tableMappingService.getTableMappingById(tableMapId));
        return repository.save(mapping);
    }

    @Override
    public FieldMapping removeFieldMapping(Long fieldMappingId) {
        FieldMapping mapping = repository.findById(fieldMappingId).orElse(null);
        if (mapping == null)
            return null;

        repository.delete(mapping);
        return mapping;
    }

    @Override
    public List<FieldMapping> getFieldMappingsFromTableMapping(Long tableMappingId) {
        return repository.findAllByTableMapping_Id(tableMappingId);
    }
}
