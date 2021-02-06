package com.ua.riah.service.target.targetDatabase;

import com.ua.riah.mapping.ConceptsMap;
import com.ua.riah.model.CDMVersion;
import com.ua.riah.model.target.TargetDatabase;
import com.ua.riah.model.target.TargetField;
import com.ua.riah.model.target.TargetTable;
import com.ua.riah.repository.target.TargetDatabaseRepository;
import com.ua.riah.service.target.targetField.TargetFieldService;
import com.ua.riah.service.target.targetTable.TargetTableService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TargetDatabaseServiceImpl implements TargetDatabaseService {

    @Autowired
    private TargetDatabaseRepository repository;

    @Autowired
    private TargetTableService tableService;

    @Autowired
    private TargetFieldService fieldService;

    private final Logger logger = LoggerFactory.getLogger(TargetDatabaseServiceImpl.class);

    private static String CONCEPT_ID_HINTS_FILE_NAME = "src/main/resources/CDMConceptIDHints_v5.0_02-OCT-19.csv";


    @Override
    public void loadCDMDatabases() {
        for (CDMVersion version : CDMVersion.values()) {
            repository.save(loadCDMDatabaseFromCSV(version));
            logger.info("TARGET DATABASE - LOADED database " + version);
        }
    }

    @Override
    public List<TargetDatabase> getAllTargetDatabases() {
        logger.info("TARGET DATABASE - Get all target databases");
        return repository.findAll();
    }

    @Override
    public TargetDatabase getDefaultDatabase() {
        return repository.findTargetDatabaseByVersion(CDMVersion.CDMV60);
    }

    @Override
    public TargetDatabase getDatabaseByCDM(String cdm) {
        return repository.findTargetDatabaseByVersion(CDMVersion.valueOf(cdm));
    }

    /**
     *
     * @param cdmVersion
     * @return
     */
    private TargetDatabase loadCDMDatabaseFromCSV(CDMVersion cdmVersion) {
        // create database instance
        String DBName = cdmVersion.toString();
        TargetDatabase database = new TargetDatabase();
        database.setDatabaseName(DBName);
        database.setVersion(cdmVersion);
        database = repository.save(database);

        List<TargetTable> tables = new ArrayList<>();

        Map<String, TargetTable> nameToTable = new HashMap<>();

        try {
            File file = new File(cdmVersion.fileName);
            FileInputStream fileInputStream = new FileInputStream(file);

            ConceptsMap conceptsMap = new ConceptsMap(CONCEPT_ID_HINTS_FILE_NAME);

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
                String tableName = row.get(tableNameColumn).toLowerCase();

                if (table == null) {
                    // Create table instance
                    table = tableService.createTable(database, tableName);
                    //table = new DBTable(row.get(tableNameColumn).toLowerCase());
                    nameToTable.put(row.get(tableNameColumn).toLowerCase(), table);
                    tables.add(table);
                }

                TargetField field = new TargetField();
                field.setName(row.get(fieldNameColumn).toLowerCase());
                field.setNullable(row.get(isNullableColumn).equals(nullableValue));
                field.setType(row.get(dataTypeColumn));
                field.setDescription(row.get(descriptionColumn));
                field.setTable(table);

                fieldService.createField(field);
                //table.getFields().add(field);
            }

        } catch (FileNotFoundException e) {
            System.err.println("ERROR reading CDM model from CSV: " + e);
        } catch (IOException e) {
            System.err.println("ERROR loading concept hints: " + e);
        }

        database.setTables(tables);
        return database;
    }
}
