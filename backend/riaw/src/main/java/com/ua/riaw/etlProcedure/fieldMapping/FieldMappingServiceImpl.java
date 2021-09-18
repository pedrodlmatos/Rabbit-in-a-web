package com.ua.riaw.etlProcedure.fieldMapping;

import com.ua.riaw.utils.error.exceptions.EntityNotFoundException;
import com.ua.riaw.utils.error.exceptions.UnauthorizedAccessException;
import com.ua.riaw.etlProcedure.tableMapping.TableMapping;
import com.ua.riaw.etlProcedure.source.ehrField.EHRField;
import com.ua.riaw.etlProcedure.target.omopField.OMOPField;
import com.ua.riaw.etlProcedure.ETLService;
import com.ua.riaw.etlProcedure.source.ehrField.EHRFieldService;
import com.ua.riaw.etlProcedure.tableMapping.TableMappingService;
import com.ua.riaw.etlProcedure.target.omopField.OMOPFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FieldMappingServiceImpl implements FieldMappingService {

    @Autowired
    private FieldMappingRepository repository;

    @Autowired
    private EHRFieldService ehrFieldService;

    @Autowired
    private OMOPFieldService omopFieldService;

    @Autowired
    private TableMappingService tableMappingService;

    @Autowired
    private ETLService etlService;


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

    @Override
    public FieldMapping addFieldMapping(Long ehrFieldId, Long omopTableId, Long tableMappingId, Long etl_id, String username) {
        if (etlService.userHasAccessToEtl(etl_id, username)) {
            EHRField ehrField = ehrFieldService.getFieldById(ehrFieldId);
            OMOPField omopField = omopFieldService.getFieldById(omopTableId);
            TableMapping tableMapping = tableMappingService.getTableMappingById(tableMappingId);

            FieldMapping mapping = new FieldMapping(
                    ehrField,
                    omopField,
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
