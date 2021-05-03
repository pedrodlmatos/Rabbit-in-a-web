package com.ua.hiah.service.tableMapping;

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
     * @return table mapping if found, null otherwise
     */

    @Override
    public TableMapping getTableMappingById(Long map_id) {
        return repository.findById(map_id).orElse(null);
    }


    /**
     * Deletes a table mapping given its id
     *
     * @param map_id table mapping id
     * @return deleted mapping
     */

    @Override
    public TableMapping removeTableMapping(Long map_id) {
        TableMapping mapping = repository.findById(map_id).orElse(null);

        if (mapping == null)
            return null;

        repository.delete(mapping);
        return mapping;
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


    /**
     * Creates a table mapping between a table from the EHR database and a table from the OMOP CDM
     *
     * @param source_id source table's id
     * @param target_id target table's id
     * @param etl_id ETL procedure's id
     * @return created table mapping
     */

    @Override
    public TableMapping addTableMapping(Long source_id, Long target_id, Long etl_id) {
        TableMapping mapping = new TableMapping();
        mapping.setSource(sourceTableService.getTableById(source_id));
        mapping.setTarget(targetTableService.getTableById(target_id));
        mapping.setComplete(false);
        mapping.setEtl(etlService.getETLWithId(etl_id));
        return repository.save(mapping);
    }


    /**
     * Changes the completion state of a table mapping
     *
     * @param tableMappingId table mapping id
     * @param completion state to change to
     * @return altered table mapping
     */

    @Override
    public TableMapping changeCompletionStatus(Long tableMappingId, boolean completion) {
        TableMapping mapping = repository.findById(tableMappingId).orElse(null);

        if (mapping != null) {
            mapping.setComplete(completion);
            return repository.save(mapping);
        }
        return null;
    }


    /**
     * Changes the table mapping logic
     *
     * @param tableMappingId table mapping id
     * @param logic logic to change to
     * @return altered table mapping
     */

    @Override
    public TableMapping changeMappingLogic(Long tableMappingId, String logic) {
        TableMapping mapping = repository.findById(tableMappingId).orElse(null);

        if (mapping != null) {
            mapping.setLogic(logic);
            return repository.save(mapping);
        }
        return null;
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
            return mappings;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
