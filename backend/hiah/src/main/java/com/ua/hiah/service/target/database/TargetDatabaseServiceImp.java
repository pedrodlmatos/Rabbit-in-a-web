package com.ua.hiah.service.target.database;

import com.ua.hiah.model.CDMVersion;
import com.ua.hiah.model.source.SourceField;
import com.ua.hiah.model.source.SourceTable;
import com.ua.hiah.model.source.ValueCount;
import com.ua.hiah.model.target.Concept;
import com.ua.hiah.model.target.TargetDatabase;
import com.ua.hiah.model.target.TargetField;
import com.ua.hiah.model.target.TargetTable;
import com.ua.hiah.rabbitcore.riah_datamodel.ConceptsMap;
import com.ua.hiah.repository.target.TargetDatabaseRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TargetDatabaseServiceImp implements TargetDatabaseService{

    @Autowired
    private TargetDatabaseRepository repository;

    private static String CONCEPT_ID_HINTS_FILE_NAME = "CDMConceptIDHints_v5.0_02-OCT-19.csv";


    /**
     * Verifies if a given OMOP CDM version exists
     *
     * @param cdm OMOP CDM version
     * @return true if exists, false otherwise
     */

    @Override
    public boolean CDMExists(String cdm) {
        for (CDMVersion version : CDMVersion.values()) {
            if (version.toString().equals(cdm))
                return true;
        }
        return false;
    }


    /**
     * Persists OMOP CDM database (and tables, fields, concepts) from a file
     *
     * @param version OMOP CDM version
     * @return persisted database
     */

    @Override
    public TargetDatabase generateModelFromCSV(CDMVersion version) {
        /* Adapted from Database (rabbit-core) */
        ConceptsMap conceptIdHintsMap = new ConceptsMap(CONCEPT_ID_HINTS_FILE_NAME);
        TargetDatabase database = new TargetDatabase(
            version.toString(),
            version,
            conceptIdHintsMap.vocabularyVersion
        );
        Map<String, TargetTable> nameToTable = new HashMap<>();
        List<TargetTable> tables = new ArrayList<>();

        try {
            FileInputStream fileInputStream = new FileInputStream(version.fileName);
            for (CSVRecord row : CSVFormat.RFC4180.withHeader().parse(new InputStreamReader(fileInputStream))) {
                String tableNameColumn;
                String fieldNameColumn;
                String isNullableColumn;
                String nullableValue;
                String dataTypeColumn;
                String descriptionColumn;
                if (row.isSet("TABLE_NAME")) {
                    tableNameColumn = "TABLE_NAME";
                    fieldNameColumn = "COLUMN_NAME";
                    isNullableColumn = "IS_NULLABLE";
                    nullableValue = "YES";
                    dataTypeColumn = "DATA_TYPE";
                    descriptionColumn = "DESCRIPTION";
                } else {
                    tableNameColumn = "table";
                    fieldNameColumn = "field";
                    isNullableColumn = "required";
                    nullableValue = "No";
                    dataTypeColumn = "type";
                    descriptionColumn = "description";
                }
                TargetTable table = nameToTable.get(row.get(tableNameColumn).toLowerCase());

                if (table == null) {
                    table = new TargetTable(
                        row.get(tableNameColumn).toLowerCase(),
                        database
                    );
                    nameToTable.put(row.get(tableNameColumn).toLowerCase(), table);
                    tables.add(table);
                }

                TargetField field = new TargetField(
                    row.get(fieldNameColumn).toLowerCase(),
                    row.get(isNullableColumn).equals(nullableValue),
                    row.get(dataTypeColumn),
                    row.get(descriptionColumn),
                    table
                );
                table.getFields().add(field);

                if (conceptIdHintsMap.get(table.getName(), field.getName()) != null) {
                    for (ConceptsMap.TempConcept tempConcept : conceptIdHintsMap.get(table.getName(), field.getName())) {
                        Concept concept = new Concept(
                            Long.valueOf(tempConcept.getConceptId()),
                            tempConcept.getConceptName(),
                            tempConcept.getStandardConcept(),
                            tempConcept.getDomainId(),
                            tempConcept.getVocabularyId(),
                            tempConcept.getConceptClassId(),
                            field
                        );
                        field.getConcepts().add(concept);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        database.setTables(tables);
        return repository.save(database);
    }


    /**
     * Removes a database given its id
     *
     * @param id database's id
     */

    @Override
    public void removeDatabase(Long id) {
        repository.deleteById(id);
    }

    @Override
    public TargetDatabase createDatabaseFromJSON(TargetDatabase targetDatabase) {
        TargetDatabase responseDatabase = new TargetDatabase(
            targetDatabase.getDatabaseName(),
            targetDatabase.getVersion(),
            targetDatabase.getConceptIdHintsVocabularyVersion()
        );
        List<TargetTable> tables = new ArrayList<>();
        for (TargetTable table : targetDatabase.getTables()) {
            TargetTable responseTable = new TargetTable(
                table.getName(),
                table.getComment(),
                responseDatabase
            );
            tables.add(responseTable);

            for (TargetField field : table.getFields()) {
                TargetField responseField = new TargetField(
                    field.getName(),
                    field.isNullable(),
                    field.getType(),
                    field.getDescription(),
                    field.getComment(),
                    responseTable
                );
                responseTable.getFields().add(responseField);

                for (Concept concept : field.getConcepts()) {
                    Concept responseConcept = new Concept(
                        concept.getConceptId(),
                        concept.getConceptName(),
                        concept.getStandardConcept(),
                        concept.getDomainId(),
                        concept.getVocabularyId(),
                        concept.getConceptClassId(),
                        responseField
                    );
                    responseField.getConcepts().add(responseConcept);
                }
            }
        }

        responseDatabase.setTables(tables);
        return repository.save(responseDatabase);
    }
}
