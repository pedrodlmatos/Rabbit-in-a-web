package com.ua.hiah.service.target.database;

import com.ua.hiah.model.CDMVersion;
import com.ua.hiah.model.target.TargetDatabase;
import com.ua.hiah.model.target.TargetField;
import com.ua.hiah.model.target.TargetTable;
import com.ua.hiah.rabbitcore.riah_datamodel.ConceptsMap;
import com.ua.hiah.repository.target.TargetDatabaseRepository;
import com.ua.hiah.service.target.field.TargetFieldService;
import com.ua.hiah.service.target.table.TargetTableService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TargetDatabaseServiceImp implements TargetDatabaseService{

    @Autowired
    private TargetDatabaseRepository repository;

    @Autowired
    private TargetTableService tableService;

    @Autowired
    private TargetFieldService fieldService;

    private static String CONCEPT_ID_HINTS_FILE_NAME = "CDMConceptIDHints_v5.0_02-OCT-19.csv";

    @Override
    public boolean CDMExists(String cdm) {
        for (CDMVersion version : CDMVersion.values()) {
            if (version.toString().equals(cdm))
                return true;
        }
        return false;
    }

    @Override
    public TargetDatabase generateModelFromCSV(CDMVersion version) {
        TargetDatabase database = new TargetDatabase();
        database.setDatabaseName(version.toString());
        database.setVersion(version);
        database = repository.save(database);

        Map<String, TargetTable> nameToTable = new HashMap<>();
        List<TargetTable> tables = new ArrayList<>();

        try {
            File file = new File(version.fileName);
            FileInputStream fileInputStream = new FileInputStream(file);

            ConceptsMap conceptIdHintsMap = new ConceptsMap(CONCEPT_ID_HINTS_FILE_NAME);
            database.setConceptIdHintsVocabularyVersion(conceptIdHintsMap.vocabularyVersion);

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
                    table = new TargetTable();
                    table.setTargetDatabase(database);
                    table.setName(row.get(tableNameColumn).toLowerCase());
                    table = tableService.createTargetTable(table);

                    nameToTable.put(row.get(tableNameColumn).toLowerCase(), table);
                    tables.add(table);
                }


                TargetField field = new TargetField();
                field.setName(row.get(fieldNameColumn).toLowerCase());
                field.setNullable(row.get(isNullableColumn).equals(nullableValue));
                field.setType(row.get(dataTypeColumn));
                field.setDescription(row.get(descriptionColumn));
                field.setTable(table);
                // TODO
                // field.setConceptIdHints(conceptIdHintsMap.get(table.getName(), field.getName()));
                field = fieldService.createField(field);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        database.setTables(tables);
        return repository.save(database);
    }

    @Override
    public void removeDatabase(TargetDatabase targetDatabase) {
        repository.delete(targetDatabase);
    }
}
