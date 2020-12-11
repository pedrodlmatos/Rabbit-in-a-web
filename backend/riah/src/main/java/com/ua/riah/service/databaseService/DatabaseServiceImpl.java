package com.ua.riah.service.databaseService;

import com.ua.riah.mapping.ConceptsMap;
import com.ua.riah.model.Database;
import com.ua.riah.model.CDMVersion;
import com.ua.riah.model.DBTable;
import com.ua.riah.model.Field;
import com.ua.riah.repository.DatabaseRepository;
import com.ua.riah.service.dbTableService.DBTableService;
import com.ua.riah.service.fieldService.FieldService;
import com.ua.riah.utilities.ScanFieldName;
import com.ua.riah.utilities.ScanSheetName;
import com.ua.riah.utilities.files.xlsx.QuickAndDirtyXlsxReader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class DatabaseServiceImpl implements DatabaseService {

    private static String CONCEPT_ID_HINTS_FILE_NAME = "src\\main\\resources\\CDMConceptIDHints_v5.0_02-OCT-19.csv";

    private static final Logger logger = LoggerFactory.getLogger(DatabaseServiceImpl.class);

    @Autowired
    private DatabaseRepository repository;

    @Autowired
    private DBTableService tableService;

    @Autowired
    private FieldService fieldService;


    @Override
    public List<Database> getAllDatabases() {
        return repository.findAll();
    }

    @Override
    public void loadCDMDatabases() {
        for (CDMVersion version : CDMVersion.values()) {
            logger.info("LOADING database " + version);
            repository.save(loadCDMDatabaseFromCSV(version));
        }
    }

    @Override
    public void loadScanReport() {
        String filename = "src\\main\\resources\\Scans\\ScanReport.xlsx";

        logger.info("LOADING Scan report");
        repository.save(loadOMOPDatabaseFromScanReport(filename));
    }

    @Override
    public Database getDatabase(String id) {
        return repository.findById(id).orElse(null);
    }

    /**
     *
     * @param cdmVersion
     * @return
     */
    private Database loadCDMDatabaseFromCSV(CDMVersion cdmVersion) {
        // create database instance
        String DBName = cdmVersion.toString();
        Database database =  new Database();
        database.setId("db-" + DBName.toLowerCase());
        database.setDbName(DBName);
        database = repository.save(database);

        List<DBTable> tables = new ArrayList<>();

        Map<String, DBTable> nameToTable = new HashMap<>();

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

                DBTable table = nameToTable.get(row.get(tableNameColumn).toLowerCase());
                String tableName = row.get(tableNameColumn).toLowerCase();

                if (table == null) {
                    // Create table instance
                    table = tableService.createTable(database, tableName);
                    //table = new DBTable(row.get(tableNameColumn).toLowerCase());
                    nameToTable.put(row.get(tableNameColumn).toLowerCase(), table);
                    tables.add(table);
                }

                Field field = new Field();
                field.setId(String.format("f%s-%s-%s", tableName, DBName.toLowerCase(), row.get(fieldNameColumn).toLowerCase()));
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

    /**
     *
     * @param filename
     * @return
     */
    private Database loadOMOPDatabaseFromScanReport(String filename) {
        Database database = new Database();
        String DBName = "MIMIC";
        database.setId("db-mimic");
        database.setDbName(DBName);
        database = repository.save(database);

        List<DBTable> tables = new ArrayList<>();

        QuickAndDirtyXlsxReader workbook = new QuickAndDirtyXlsxReader(filename);

        // Create table lookup from tables overview, if it exists
        Map<String, DBTable> nameToTable = createTablesFromTableOverview(workbook, database);

        // Field overview is the first sheet
        QuickAndDirtyXlsxReader.Sheet overviewSheet = workbook.getByName(ScanSheetName.FIELD_OVERVIEW);
        if (overviewSheet == null) {
            overviewSheet = workbook.get(0);
        }
        Iterator<QuickAndDirtyXlsxReader.Row> overviewRows = overviewSheet.iterator();
        overviewRows.next();  // Skip header

        while (overviewRows.hasNext()) {
            QuickAndDirtyXlsxReader.Row row = overviewRows.next();
            String tableName = row.getStringByHeaderName(ScanFieldName.TABLE);
            if (tableName.length() != 0) {
                // Get table created from table overview or created before
                DBTable table = nameToTable.get(tableName);

                // If not exists, create table from field overview sheet
                if (table == null) {
                    /*
                        table = tableService.createTable(database, tableName);

                    table = createTable(
                            tableName,
                            "",
                            row.getIntByHeaderName(ScanFieldName.N_ROWS),
                            row.getIntByHeaderName(ScanFieldName.N_ROWS_CHECKED)
                    );*/

                    String description = null;
                    int n_rows = row.getIntByHeaderName(ScanFieldName.N_ROWS);
                    int n_rows_checked = row.getIntByHeaderName(ScanFieldName.N_ROWS_CHECKED);

                    table = tableService.createTable(database, tableName, description, n_rows, n_rows_checked);
                    nameToTable.put(tableName, table);
                    tables.add(table);
                }

                String fieldName = row.getStringByHeaderName(ScanFieldName.FIELD);
                //Field field = new Field(fieldName.toLowerCase(), table);
                Field field = new Field();
                field.setId(String.format("f%s-%s-%s", tableName, DBName.toLowerCase(), fieldName.toLowerCase()));
                field.setName(fieldName.toLowerCase());
                field.setTable(table);
                field.setType(row.getByHeaderName(ScanFieldName.TYPE));
                field.setMaxLength(row.getIntByHeaderName(ScanFieldName.MAX_LENGTH));
                field.setDescription(row.getStringByHeaderName(ScanFieldName.DESCRIPTION));
                field.setFractionEmpty(row.getDoubleByHeaderName(ScanFieldName.FRACTION_EMPTY));
                field.setUniqueCount(row.getIntByHeaderName(ScanFieldName.UNIQUE_COUNT));
                field.setFractionUnique(row.getDoubleByHeaderName(ScanFieldName.FRACTION_UNIQUE));
                //field.setValueCounts(getValueCounts(workbook, tableName, fieldName));
                fieldService.createField(field);

            }
        }

        database.setTables(tables);
        return database;
    }

    /*
    private static DBTable createTable(String name, String description, Integer nRows, Integer nRowsChecked) {
        DBTable table = new DBTable();
        table.setName(name.toLowerCase());
        table.setDescription(description);
        table.setRowCount(nRows == null ? -1 : nRows);
        table.setRowsCheckedCount(nRowsChecked == null ? -1 : nRowsChecked);
        return table;
    }*/

    private Map<String, DBTable> createTablesFromTableOverview(QuickAndDirtyXlsxReader workbook, Database database) {
        QuickAndDirtyXlsxReader.Sheet tableOverviewSheet = workbook.getByName(ScanSheetName.TABLE_OVERVIEW);

        List<DBTable> tables = new ArrayList<>();

        if (tableOverviewSheet == null) { // No table overview sheet, empty nameToTable
            return new HashMap<>();
        }

        Map<String, DBTable> nameToTable = new HashMap<>();

        Iterator<QuickAndDirtyXlsxReader.Row> tableRows = tableOverviewSheet.iterator();
        tableRows.next();  // Skip header
        while (tableRows.hasNext()) {
            QuickAndDirtyXlsxReader.Row row = tableRows.next();
            String tableName = row.getByHeaderName(ScanFieldName.TABLE);
            /*DBTable table = createTable(
                    tableName,
                    row.getByHeaderName(ScanFieldName.DESCRIPTION),
                    row.getIntByHeaderName(ScanFieldName.N_ROWS),
                    row.getIntByHeaderName(ScanFieldName.N_ROWS_CHECKED)
            );*/

            String description = row.getByHeaderName(ScanFieldName.DESCRIPTION);
            int nRows = row.getIntByHeaderName(ScanFieldName.N_ROWS);
            int nRowsChecked = row.getIntByHeaderName(ScanFieldName.N_ROWS_CHECKED);

            DBTable table = tableService.createTable(database, tableName, description, nRows, nRowsChecked);
            // Add to lookup and database
            nameToTable.put(tableName, table);
            tables.add(table);
            //database.tables.add(table);
        }

        database.setTables(tables);
        return nameToTable;
    }
}
