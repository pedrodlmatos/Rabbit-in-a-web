package com.ua.hiah.service.fieldMapping;

import com.ua.hiah.model.FieldMapping;
import com.ua.hiah.model.TableMapping;
import com.ua.hiah.model.source.SourceField;
import com.ua.hiah.model.target.TargetField;
import com.ua.hiah.repository.FieldMappingRepository;
import com.ua.hiah.service.source.field.SourceFieldService;
import com.ua.hiah.service.tableMapping.TableMappingService;
import com.ua.hiah.service.target.field.TargetFieldService;
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


    /**
     * Creates a field mapping
     *
     * @param sourceFieldId source field's id
     * @param targetTableId target field's id
     * @param tableMappingId table mapping's id
     * @return created table mapping
     */

    @Override
    public FieldMapping addFieldMapping(Long sourceFieldId, Long targetTableId, Long tableMappingId) {
        SourceField sourceField = sourceFieldService.getFieldById(sourceFieldId);
        TargetField targetField = targetFieldService.getFieldById(targetTableId);
        TableMapping tableMapping = tableMappingService.getTableMappingById(tableMappingId);

        // validate
        if (sourceField != null && targetField != null && tableMapping != null) {
            FieldMapping mapping = new FieldMapping(
                    sourceFieldService.getFieldById(sourceFieldId),
                    targetFieldService.getFieldById(targetTableId),
                    tableMappingService.getTableMappingById(tableMappingId)
            );
            return repository.save(mapping);
        }

        return null;
    }


    /**
     * Deletes a field mapping
     *
     * @param fieldMappingId field mapping id
     * @return deleted mapping
     */

    @Override
    public FieldMapping removeFieldMapping(Long fieldMappingId) {
        FieldMapping mapping = repository.findById(fieldMappingId).orElse(null);
        if (mapping == null) return null;

        repository.delete(mapping);
        return mapping;
    }


    /**
     * Retrieved all field mappings of a table mapping
     *
     * @param tableMappingId table mapping id
     * @return list of field mappings
     */

    @Override
    public List<FieldMapping> getFieldMappingsFromTableMapping(Long tableMappingId) {
        return repository.findAllByTableMapping_Id(tableMappingId);
    }


    /**
     * Changes the logic of a given field mapping
     *
     * @param fieldMappingId field mapping's id
     * @param logic logic to change to
     * @return altered logic
     */

    @Override
    public FieldMapping changeMappingLogic(Long fieldMappingId, String logic) {
        FieldMapping mapping = repository.findById(fieldMappingId).orElse(null);
        if (mapping == null) return null;                               // field mapping not found
        else if (mapping.getLogic().equals(logic)) return mapping;      // old logic == new logic
        else {                                                          // old logic != new logic
            mapping.setLogic(logic);
            return repository.save(mapping);
        }
    }
}
