package com.ua.riaw.service.omop.database;

import com.ua.riaw.model.CDMVersion;
import com.ua.riaw.model.StemTableFile;
import com.ua.riaw.model.omop.Concept;
import com.ua.riaw.model.omop.OMOPDatabase;
import com.ua.riaw.model.omop.OMOPField;
import com.ua.riaw.model.omop.OMOPTable;
import com.ua.riaw.service.omop.table.OMOPTableService;
import rabbitcore.riah_datamodel.ConceptsMap;
import com.ua.riaw.repository.target.OMOPDatabaseRepository;
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
public class OMOPDatabaseServiceImp implements OMOPDatabaseService {

    @Autowired
    private OMOPDatabaseRepository repository;

    @Autowired
    private OMOPTableService omopTableService;


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
    public OMOPDatabase generateModelFromCSV(CDMVersion version) {
        /* Adapted from Database (rabbit-core) */
        ConceptsMap conceptIdHintsMap = new ConceptsMap(CONCEPT_ID_HINTS_FILE_NAME);
        OMOPDatabase database = new OMOPDatabase(
            version.toString(),
            version,
            conceptIdHintsMap.vocabularyVersion
        );
        Map<String, OMOPTable> nameToTable = new HashMap<>();
        List<OMOPTable> tables = new ArrayList<>();

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
                OMOPTable table = nameToTable.get(row.get(tableNameColumn).toLowerCase());

                if (table == null) {
                    table = new OMOPTable(
                        row.get(tableNameColumn).toLowerCase(),
                        database
                    );
                    nameToTable.put(row.get(tableNameColumn).toLowerCase(), table);
                    tables.add(table);
                }

                OMOPField field = new OMOPField(
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
        //return repository.save(database);
        return database;
    }


    /**
     * Creates a OMOP CDM database from data contained in JSON
     *
     * @param omopDatabase database stored in JSON object
     * @return database altered for specific model
     */

    @Override
    public OMOPDatabase createDatabaseFromJSON(OMOPDatabase omopDatabase) {
        OMOPDatabase responseDatabase = new OMOPDatabase(
            omopDatabase.getDatabaseName(),
            omopDatabase.getVersion(),
            omopDatabase.getConceptIdHintsVocabularyVersion()
        );
        List<OMOPTable> tables = new ArrayList<>();
        for (OMOPTable table : omopDatabase.getTables()) {
            OMOPTable responseTable = new OMOPTable(
                table.getName(),
                table.isStem(),
                table.getComment(),
                responseDatabase
            );
            tables.add(responseTable);

            for (OMOPField field : table.getFields()) {
                OMOPField responseField = new OMOPField(
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
        //return repository.save(responseDatabase);
        return responseDatabase;
    }


    /**
     * Creates stem table on OMOP CDM database
     *
     * @param version OMOP CDM version
     * @param omopDatabase target database object
     * @return created table
     */

    @Override
    public OMOPTable createTargetStemTable(CDMVersion version, OMOPDatabase omopDatabase) {
        OMOPTable stemOMOPTable = new OMOPTable(
            "stem_table",
            true,
                omopDatabase
        );

        try {
            StemTableFile stemTableFile = StemTableFile.valueOf(version.name());
            FileInputStream fileInputStream = new FileInputStream(stemTableFile.fileName);

            for (CSVRecord row : CSVFormat.RFC4180.withHeader().parse(new InputStreamReader(fileInputStream))) {
                OMOPField field = new OMOPField(
                    row.get("COLUMN_NAME").toLowerCase(),
                    row.get("IS_NULLABLE").equals("YES"),
                    row.get("DATA_TYPE"),
                    row.get("DESCRIPTION"),
                    true,
                        stemOMOPTable
                );
                stemOMOPTable.getFields().add(field);
            }
            return stemOMOPTable;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Removes stem table from OMOP CDM database
     *
     * @param table stem table
     */

    @Override
    public void removeTable(OMOPTable table) {
        omopTableService.removeStemTable(table);
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


}
