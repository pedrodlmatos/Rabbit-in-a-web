package com.ua.riah.service.source.sourceDatabaseService;

import com.ua.riah.model.ETL;
import com.ua.riah.model.source.SourceDatabase;
import com.ua.riah.model.source.SourceField;
import com.ua.riah.model.source.SourceTable;
import com.ua.riah.repository.source.SourceDatabaseRepository;
import com.ua.riah.service.source.sourceFieldService.SourceFieldService;
import com.ua.riah.service.source.sourceTableService.SourceTableService;
import com.ua.riah.utilities.ScanFieldName;
import com.ua.riah.utilities.ScanSheetName;
import com.ua.riah.utilities.files.xlsx.QuickAndDirtyXlsxReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class SourceDatabaseServiceImpl implements SourceDatabaseService {

    private static String CONCEPT_ID_HINTS_FILE_NAME = "src\\main\\resources\\CDMConceptIDHints_v5.0_02-OCT-19.csv";

    private static final Logger logger = LoggerFactory.getLogger(SourceDatabaseServiceImpl.class);

    @Autowired
    private SourceDatabaseRepository repository;

    @Autowired
    private SourceTableService tableService;

    @Autowired
    private SourceFieldService fieldService;


    @Override
    public SourceDatabase getDefaultDatabase(String name) {
        return repository.findByDatabaseName(name);
    }

    @Override
    public SourceDatabase createDatabaseFromFile(MultipartFile file) {
        try {
            File scanTemp = new File("scan.xlsx");
            scanTemp.createNewFile();
            OutputStream os = new FileOutputStream(scanTemp);
            os.write(file.getBytes());
            os.close();
            SourceDatabase sourceDatabase = loadDatabaseFromScanReport(scanTemp.getAbsolutePath());

            logger.info("SOURCE DATABASE - LOADED " + file.getName());
            scanTemp.delete();
            return sourceDatabase;
        } catch (IOException e) {
            System.err.println("ERROR creating file");
        }
        return null;
    }

    private SourceDatabase loadDatabaseFromScanReport(String filename) {
        SourceDatabase database = new SourceDatabase();
        database.setDatabaseName("MIMIC");
        database = repository.save(database);

        List<SourceTable> tables = new ArrayList<>();

        QuickAndDirtyXlsxReader workbook = new QuickAndDirtyXlsxReader(filename);

        // Create table lookup from tables overview, if it exists
        Map<String, SourceTable> nameToTable = createTablesFromTableOverview(workbook, database);

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
                SourceTable table = nameToTable.get(tableName);

                // If not exists, create table from field overview sheet
                if (table == null) {
                    String description = null;
                    int n_rows = row.getIntByHeaderName(ScanFieldName.N_ROWS);
                    int n_rows_checked = row.getIntByHeaderName(ScanFieldName.N_ROWS_CHECKED);

                    table = tableService.createTable(database, tableName, description, n_rows, n_rows_checked);
                    nameToTable.put(tableName, table);
                    tables.add(table);
                }

                String fieldName = row.getStringByHeaderName(ScanFieldName.FIELD);
                //Field field = new Field(fieldName.toLowerCase(), table);
                SourceField field = new SourceField();
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


    private Map<String, SourceTable> createTablesFromTableOverview(QuickAndDirtyXlsxReader workbook, SourceDatabase database) {
        QuickAndDirtyXlsxReader.Sheet tableOverviewSheet = workbook.getByName(ScanSheetName.TABLE_OVERVIEW);

        List<SourceTable> tables = new ArrayList<>();

        if (tableOverviewSheet == null) { // No table overview sheet, empty nameToTable
            return new HashMap<>();
        }

        Map<String, SourceTable> nameToTable = new HashMap<>();

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

            SourceTable table = tableService.createTable(database, tableName, description, nRows, nRowsChecked);
            // Add to lookup and database
            nameToTable.put(tableName, table);
            tables.add(table);
            //database.tables.add(table);
        }

        database.setTables(tables);
        return nameToTable;
    }
}
