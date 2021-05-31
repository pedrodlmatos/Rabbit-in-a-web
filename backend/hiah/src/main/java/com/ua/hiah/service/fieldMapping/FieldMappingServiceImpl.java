package com.ua.hiah.service.fieldMapping;

import com.ua.hiah.error.exceptions.EntityNotFoundException;
import com.ua.hiah.error.exceptions.UnauthorizedAccessException;
import com.ua.hiah.model.FieldMapping;
import com.ua.hiah.model.TableMapping;
import com.ua.hiah.model.source.SourceField;
import com.ua.hiah.model.target.TargetField;
import com.ua.hiah.repository.FieldMappingRepository;
import com.ua.hiah.service.etl.ETLService;
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

    @Autowired
    private ETLService etlService;


    /**
     * Creates a field mapping
     *
     * @param sourceFieldId source field's id
     * @param targetTableId target field's id
     * @param tableMappingId table mapping's id
     * @param etl_id ETL procedure's id
     * @param username User's username
     * @return created table mapping
     */

    @Override
    public FieldMapping addFieldMapping(Long sourceFieldId, Long targetTableId, Long tableMappingId, Long etl_id, String username) {
        if (etlService.userHasAccessToEtl(etl_id, username)) {
            SourceField sourceField = sourceFieldService.getFieldById(sourceFieldId);
            TargetField targetField = targetFieldService.getFieldById(targetTableId);
            TableMapping tableMapping = tableMappingService.getTableMappingById(tableMappingId);

            FieldMapping mapping = new FieldMapping(
                    sourceField,
                    targetField,
                    tableMapping
            );

            // update modification date
            etlService.updateModificationDate(etl_id);
            return repository.save(mapping);
        } else
            throw new UnauthorizedAccessException(FieldMapping.class, username, etl_id);
    }


    /**
     * Deletes a field mapping
     *
     * @param fieldMappingId field mapping id
     * @param etl_id ETL procedure's id
     * @param username User's username
     */

    @Override
    public void removeFieldMapping(Long fieldMappingId, Long etl_id, String username) {
        if (etlService.userHasAccessToEtl(etl_id, username)) {
            FieldMapping fieldMapping = repository.findById(fieldMappingId).orElseThrow(() -> new EntityNotFoundException(FieldMapping.class, "id", fieldMappingId.toString()));
            repository.delete(fieldMapping);

            // update modification date
            etlService.updateModificationDate(etl_id);
        } else
            throw new UnauthorizedAccessException(FieldMapping.class, username, fieldMappingId);
    }


    /**
     * Changes the logic of a given field mapping
     *
     * @param fieldMappingId field mapping's id
     * @param logic logic to change to
     * @param etl_id ETL procedure's id
     * @param username user's username
     * @return altered logic
     */

    @Override
    public FieldMapping changeMappingLogic(Long fieldMappingId, String logic, Long etl_id, String username) {
        if (etlService.userHasAccessToEtl(etl_id, username)) {
            FieldMapping mapping = repository.findById(fieldMappingId).orElseThrow(() -> new EntityNotFoundException(FieldMapping.class, "id", fieldMappingId.toString()));

            if (mapping.getLogic() != null && mapping.getLogic().equals(logic)) return mapping;      // old logic == new logic
            else {                                                                                   // old logic != new logic
                mapping.setLogic(logic);
                // update modification date
                etlService.updateModificationDate(etl_id);
                return repository.save(mapping);
            }
        } else
            throw new UnauthorizedAccessException(FieldMapping.class, username, fieldMappingId);
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
}
