package com.ua.riaw.etlProcedure.tableMapping;

import com.ua.riaw.utils.error.exceptions.EntityNotFoundException;
import com.ua.riaw.utils.error.exceptions.UnauthorizedAccessException;
import com.ua.riaw.etlProcedure.ETL;
import com.ua.riaw.etlProcedure.target.omopDatabase.CDMVersion;
import com.ua.riaw.etlProcedure.source.ehrDatabase.EHRDatabase;
import com.ua.riaw.etlProcedure.source.ehrField.EHRField;
import com.ua.riaw.etlProcedure.source.ehrTable.EHRTable;
import com.ua.riaw.etlProcedure.target.omopDatabase.OMOPDatabase;
import com.ua.riaw.etlProcedure.target.omopField.OMOPField;
import com.ua.riaw.etlProcedure.target.omopTable.OMOPTable;
import com.ua.riaw.etlProcedure.ETLService;
import com.ua.riaw.etlProcedure.source.ehrTable.EHRTableService;
import com.ua.riaw.etlProcedure.target.omopTable.OMOPTableService;
import com.ua.riaw.etlProcedure.fieldMapping.FieldMapping;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TableMappingServiceImpl implements TableMappingService {

    @Autowired
    private TableMappingRepository repository;

    @Autowired
    private ETLService etlService;

    @Autowired
    private EHRTableService ehrTableService;

    @Autowired
    private OMOPTableService omopTableService;


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
        if (etlService.userHasAccessToEtl(etl_id, username)) {
            TableMapping tableMapping = repository.findById(map_id).orElseThrow(() -> new EntityNotFoundException(TableMapping.class, "id", map_id.toString()));

            List<EHRField> ehrField = tableMapping.getEhrTable().getFields();
            ehrField.sort(Comparator.comparingLong(EHRField::getId));

            // order OMOP CDM tables by id
            List<OMOPField> omopFields = tableMapping.getOmopTable().getFields();
            omopFields.sort(Comparator.comparingLong(OMOPField::getId));

            return tableMapping;
        }
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
     * @param ehrTableId source table's id
     * @param omopTableId target table's id
     * @param etl_id ETL procedure's id
     * @param username User's username
     * @return created table mapping
     */

    @Override
    public TableMapping addTableMapping(Long ehrTableId, Long omopTableId, Long etl_id, String username) {
        if (etlService.userHasAccessToEtl(etl_id, username)) {
            EHRTable ehrTable = ehrTableService.getTableById(ehrTableId);
            OMOPTable omopTable = omopTableService.getTableById(omopTableId);
            ETL etl = etlService.getETLWithId(etl_id);

            // validate
            TableMapping mapping = new TableMapping(
                    ehrTable,
                    omopTable,
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
     * @param tableMappingId table mapping id
     * @param etl_id ETL procedure's id
     * @param username User's username
     */

    @Override
    public void removeTableMapping(Long tableMappingId, Long etl_id, String username) {
        if (etlService.userHasAccessToEtl(etl_id, username)) {
            TableMapping tableMapping = repository.findById(tableMappingId).orElseThrow(() -> new EntityNotFoundException(TableMapping.class, "id", tableMappingId.toString()));
            //update modification date
            etlService.updateModificationDate(etl_id);
            repository.delete(tableMapping);
        } else
            throw new UnauthorizedAccessException(TableMapping.class, username, tableMappingId);
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
     * @param ehrDatabase source database
     * @param omopDatabase target database
     * @return table mappings created
     */

    @Override
    public List<TableMapping> getTableMappingsFromJSON(ETL etl, List<TableMapping> tableMappings, EHRDatabase ehrDatabase, OMOPDatabase omopDatabase) {
        List<TableMapping> responseMappings = new ArrayList<>();
        for (TableMapping mapping : tableMappings) {
            EHRTable ehrTable = ehrDatabase.getTables().stream().filter(src -> src.getName().equals(mapping.getEhrTable().getName())).findFirst().orElse(null);
            OMOPTable omopTable = omopDatabase.getTables().stream().filter(trg -> trg.getName().equals(mapping.getOmopTable().getName())).findFirst().orElse(null);

            if (ehrTable != null && omopTable != null) {
                TableMapping responseMapping = new TableMapping(etl, ehrTable, omopTable, mapping.getLogic());

                for (FieldMapping fieldMapping : mapping.getFieldMappings()) {
                    EHRField ehrField = ehrTable.getFields().stream().filter(srcField -> srcField.getName().equals(fieldMapping.getEhrField().getName())).findFirst().orElse(null);
                    OMOPField omopField = omopTable.getFields().stream().filter(trgField -> trgField.getName().equals(fieldMapping.getOmopField().getName())).findFirst().orElse(null);

                    if (ehrField != null && omopField != null) {
                        FieldMapping responseFieldMapping = new FieldMapping(
                                ehrField,
                                omopField,
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
     * @param omopDatabase target database
     * @param sourceStemTable stem table on EHR database
     * @param etl ETL procedure object
     * @return list of created table mappings
     */

    @Override
    public List<TableMapping> createMappingsWithStemTable(CDMVersion version, OMOPDatabase omopDatabase, EHRTable sourceStemTable, ETL etl) {
        try {
            StemTableFile stemTableFile = StemTableFile.valueOf(version.name());
            FileInputStream fileInputStream = new FileInputStream(stemTableFile.defaultMappings);
            List<TableMapping> mappings = new ArrayList<>();
            Map<String, TableMapping> mappingMap = new HashMap<>();

            for (CSVRecord row : CSVFormat.RFC4180.withHeader().parse(new InputStreamReader(fileInputStream))) {
                String targetTableName = row.get("TARGET_TABLE").toLowerCase();
                OMOPTable omopTable = omopDatabase.getTables().stream().filter(target -> target.getName().equals(targetTableName)).findFirst().orElse(null);
                if (omopTable != null) {
                    if (mappingMap.get(targetTableName) == null) {
                        TableMapping tableMapping = new TableMapping(etl, sourceStemTable, omopTable);
                        mappingMap.put(targetTableName, tableMapping);
                        mappings.add(tableMapping);
                    }
                    EHRField ehrField = sourceStemTable.getFields().stream().filter(field -> field.getName().equals(row.get("SOURCE_FIELD").toLowerCase())).findFirst().orElse(null);
                    OMOPField omopField = omopTable.getFields().stream().filter(field -> field.getName().equals(row.get("TARGET_FIELD").toLowerCase())).findFirst().orElse(null);

                    if (ehrField != null && omopField != null) {
                        FieldMapping fieldMapping = new FieldMapping(
                                ehrField,
                                omopField,
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
    public void removeTableMappingsFromTable(Long etl_id, EHRTable table) {
        for (TableMapping tableMapping : repository.findAllByEtl_Id(etl_id)) {
            if (tableMapping.getEhrTable() == table)
                repository.delete(tableMapping);
        }
    }

    @Override
    public void removeTableMappingsToTable(Long etl_id, OMOPTable table) {
        for (TableMapping tableMapping : repository.findAllByEtl_Id(etl_id)) {
            if (tableMapping.getOmopTable() == table)
                repository.delete(tableMapping);
        }
    }
}
