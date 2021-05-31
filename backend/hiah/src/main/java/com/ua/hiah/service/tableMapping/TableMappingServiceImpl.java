package com.ua.hiah.service.tableMapping;

import com.ua.hiah.error.exceptions.EntityNotFoundException;
import com.ua.hiah.error.exceptions.UnauthorizedAccessException;
import com.ua.hiah.model.*;
import com.ua.hiah.model.source.SourceDatabase;
import com.ua.hiah.model.source.SourceField;
import com.ua.hiah.model.source.SourceTable;
import com.ua.hiah.model.target.TargetDatabase;
import com.ua.hiah.model.target.TargetField;
import com.ua.hiah.model.target.TargetTable;
import com.ua.hiah.repository.TableMappingRepository;
import com.ua.hiah.service.etl.ETLService;
import com.ua.hiah.service.source.table.SourceTableService;
import com.ua.hiah.service.target.table.TargetTableService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
@Transactional
public class TableMappingServiceImpl implements TableMappingService {

    @Autowired
    private TableMappingRepository repository;

    @Autowired
    private ETLService etlService;

    @Autowired
    private SourceTableService sourceTableService;

    @Autowired
    private TargetTableService targetTableService;


    /**
     * Gets a table mapping given its id
     *
     * @param map_id table mapping id
     * @param etl_id ETL procedure's id
     * @param username User's username
     * @return table mapping if found, null otherwise
     */

    @Override
    public TableMapping getTableMappingById(Long map_id, Long etl_id, String username) {
        if (etlService.userHasAccessToEtl(etl_id, username))
            return repository.findById(map_id).orElseThrow(() -> new EntityNotFoundException(TableMapping.class, "id", map_id.toString()));
        else
            throw new UnauthorizedAccessException(TableMapping.class, username, etl_id);
    }


    /**
     * Gets a table mapping given its id
     *
     * @param map_id table mapping id
     * @return table mapping if found, null otherwise
     */

    @Override
    public TableMapping getTableMappingById(Long map_id) {
        return repository.findById(map_id).orElseThrow(() -> new EntityNotFoundException(TableMapping.class, "id", map_id.toString()));
    }


    /**
     * Creates a table mapping between a table from the EHR database and a table from the OMOP CDM
     *
     * @param source_id source table's id
     * @param target_id target table's id
     * @param etl_id ETL procedure's id
     * @param username User's username
     * @return created table mapping
     */

    @Override
    public TableMapping addTableMapping(Long source_id, Long target_id, Long etl_id, String username) {
        if (etlService.userHasAccessToEtl(etl_id, username)) {
            SourceTable sourceTable = sourceTableService.getTableById(source_id);
            TargetTable targetTable = targetTableService.getTableById(target_id);
            ETL etl = etlService.getETLWithId(etl_id);

            // validate
            TableMapping mapping = new TableMapping(
                    sourceTable,
                    targetTable,
                    false,
                    etl
            );

            // update modification date
            etlService.updateModificationDate(etl_id);
            return repository.save(mapping);
        } else
            throw new UnauthorizedAccessException(TableMapping.class, username, etl_id);
    }


    /**
     * Deletes a table mapping given its id
     *
     * @param map_id table mapping id
     * @param etl_id ETL procedure's id
     * @param username User's username
     */

    @Override
    public void removeTableMapping(Long map_id, Long etl_id, String username) {
        if (etlService.userHasAccessToEtl(etl_id, username)) {
            TableMapping tableMapping = repository.findById(map_id).orElseThrow(() -> new EntityNotFoundException(TableMapping.class, "id", map_id.toString()));
            //update modification date
            etlService.updateModificationDate(etl_id);
            repository.delete(tableMapping);
        } else
            throw new UnauthorizedAccessException(TableMapping.class, username, map_id);
    }


    /**
     * Changes the completion state of a table mapping
     *
     * @param tableMappingId table mapping id
     * @param completion state to change to
     * @param etl_id ETL procedure's id
     * @param username User's username
     * @return altered table mapping
     */

    @Override
    public TableMapping changeCompletionStatus(Long tableMappingId, boolean completion, Long etl_id, String username) {
        if (etlService.userHasAccessToEtl(etl_id, username)) {
            TableMapping mapping = repository.findById(tableMappingId).orElseThrow(() -> new EntityNotFoundException(TableMapping.class, "id", tableMappingId.toString()));

            if (mapping.isComplete() == completion) return mapping;         // completion status don't change
            else {                                                          // completion status change
                mapping.setComplete(completion);
                // update modification date
                etlService.updateModificationDate(etl_id);
                return repository.save(mapping);
            }
        } else
            throw new UnauthorizedAccessException(TableMapping.class, username, tableMappingId);
    }


    /**
     * Changes the table mapping logic
     *
     * @param tableMappingId table mapping id
     * @param logic logic to change to
     * @param etl_id ETL procedure's id
     * @param username User's username
     * @return altered table mapping
     */

    @Override
    public TableMapping changeMappingLogic(Long tableMappingId, String logic, Long etl_id, String username) {
        if (etlService.userHasAccessToEtl(etl_id, username)) {
            TableMapping mapping = repository.findById(tableMappingId).orElseThrow(() -> new EntityNotFoundException(TableMapping.class, "id", tableMappingId.toString()));

            if (mapping.getLogic() != null && mapping.getLogic().equals(logic)) return mapping;
            else {
                mapping.setLogic(logic);
                // update modification date
                etlService.updateModificationDate(etl_id);
                return repository.save(mapping);
            }

        } else
            throw new UnauthorizedAccessException(TableMapping.class, username, tableMappingId);
    }


    /**
     * Creates the table mapping contained in a JSON file
     *
     * @param etl ETL procedure object
     * @param tableMappings table mapping in JSON
     * @param sourceDatabase source database
     * @param targetDatabase target database
     * @return table mappings created
     */

    @Override
    public List<TableMapping> getTableMappingsFromJSON(ETL etl, List<TableMapping> tableMappings, SourceDatabase sourceDatabase, TargetDatabase targetDatabase) {
        List<TableMapping> responseMappings = new ArrayList<>();
        for (TableMapping mapping : tableMappings) {
            SourceTable sourceTable = sourceDatabase.getTables().stream().filter(src -> src.getName().equals(mapping.getSource().getName())).findFirst().orElse(null);
            TargetTable targetTable = targetDatabase.getTables().stream().filter(trg -> trg.getName().equals(mapping.getTarget().getName())).findFirst().orElse(null);

            if (sourceTable != null && targetTable != null) {
                TableMapping responseMapping = new TableMapping(etl, sourceTable, targetTable, mapping.getLogic());

                for (FieldMapping fieldMapping : mapping.getFieldMappings()) {
                    SourceField sourceField = sourceTable.getFields().stream().filter(srcField -> srcField.getName().equals(fieldMapping.getSource().getName())).findFirst().orElse(null);
                    TargetField targetField = targetTable.getFields().stream().filter(trgField -> trgField.getName().equals(fieldMapping.getTarget().getName())).findFirst().orElse(null);

                    if (sourceField != null && targetField != null) {
                        FieldMapping responseFieldMapping = new FieldMapping(
                                sourceField,
                                targetField,
                                fieldMapping.getLogic(),
                                responseMapping
                        );
                        responseMapping.getFieldMappings().add(responseFieldMapping);
                    }
                }
                responseMappings.add(responseMapping);
            }
        }
        return responseMappings;
    }


    /**
     * Removes all table mappings of a given ETL procedures
     *
     * @param etl_id ETL procedure's id
     */

    @Override
    public void removeTableMappingsFromETL(long etl_id) {
        repository.deleteAllByEtl_Id(etl_id);
    }


    /**
     * Creates mapping to or from a stem table (stored in file)
     *
     * @param version OMOP CDM version
     * @param targetDatabase target database
     * @param sourceStemTable stem table on EHR database
     * @param etl ETL procedure object
     * @return list of created table mappings
     */

    @Override
    public List<TableMapping> createMappingsWithStemTable(CDMVersion version, TargetDatabase targetDatabase, SourceTable sourceStemTable, ETL etl) {
        try {
            StemTableFile stemTableFile = StemTableFile.valueOf(version.name());
            FileInputStream fileInputStream = new FileInputStream(stemTableFile.defaultMappings);
            List<TableMapping> mappings = new ArrayList<>();
            Map<String, TableMapping> mappingMap = new HashMap<>();

            for (CSVRecord row : CSVFormat.RFC4180.withHeader().parse(new InputStreamReader(fileInputStream))) {
                String targetTableName = row.get("TARGET_TABLE").toLowerCase();
                TargetTable targetTable = targetDatabase.getTables().stream().filter(target -> target.getName().equals(targetTableName)).findFirst().orElse(null);
                if (targetTable != null) {
                    if (mappingMap.get(targetTableName) == null) {
                        TableMapping tableMapping = new TableMapping(etl, sourceStemTable, targetTable);
                        mappingMap.put(targetTableName, tableMapping);
                        mappings.add(tableMapping);
                    }
                    SourceField sourceField = sourceStemTable.getFields().stream().filter(field -> field.getName().equals(row.get("SOURCE_FIELD").toLowerCase())).findFirst().orElse(null);
                    TargetField targetField = targetTable.getFields().stream().filter(field -> field.getName().equals(row.get("TARGET_FIELD").toLowerCase())).findFirst().orElse(null);

                    if (sourceField != null && targetField != null) {
                        FieldMapping fieldMapping = new FieldMapping(
                                sourceField,
                                targetField,
                                mappingMap.get(targetTableName)
                        );
                        mappingMap.get(targetTableName).getFieldMappings().add(fieldMapping);
                    }
                }
            }
            mappingMap.clear();
            return mappings;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }





































    /**
     * Gets all table mappings from a given ETL procedure
     *
     * @param etl_id ETL procedure's id
     * @return list with table mappings
     */

    @Override
    public List<TableMapping> getTableMappingFromETL(Long etl_id) {
        return repository.findAllByEtl_Id(etl_id);
    }



















    @Override
    public void removeTableMappingsFromTable(Long etl_id, SourceTable table) {
        for (TableMapping tableMapping : repository.findAllByEtl_Id(etl_id)) {
            if (tableMapping.getSource() == table)
                repository.delete(tableMapping);
        }
    }

    @Override
    public void removeTableMappingsToTable(Long etl_id, TargetTable table) {
        for (TableMapping tableMapping : repository.findAllByEtl_Id(etl_id)) {
            if (tableMapping.getTarget() == table)
                repository.delete(tableMapping);
        }
    }
}
