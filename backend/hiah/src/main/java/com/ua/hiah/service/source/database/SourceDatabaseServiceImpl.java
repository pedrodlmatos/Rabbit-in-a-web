package com.ua.hiah.service.source.database;

import com.ua.hiah.model.source.SourceDatabase;
import com.ua.hiah.model.source.SourceField;
import com.ua.hiah.model.source.SourceTable;
import com.ua.hiah.rabbitcore.utilities.ScanFieldName;
import com.ua.hiah.rabbitcore.utilities.ScanSheetName;
import com.ua.hiah.rabbitcore.utilities.files.QuickAndDirtyXlsxReader;
import com.ua.hiah.rabbitcore.utilities.files.QuickAndDirtyXlsxReader.Row;
import com.ua.hiah.rabbitcore.utilities.files.QuickAndDirtyXlsxReader.Sheet;
import com.ua.hiah.repository.source.SourceDatabaseRepository;
import com.ua.hiah.service.source.field.SourceFieldService;
import com.ua.hiah.service.source.table.SourceTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

@Service
public class SourceDatabaseServiceImpl implements SourceDatabaseService {

    @Autowired
    private SourceDatabaseRepository databaseRepository;

    @Autowired
    SourceTableService tableService;

    @Autowired
    SourceFieldService fieldService;

    @Override
    public SourceDatabase createDatabaseFromScanReport(String name, MultipartFile file) {
        SourceDatabase database = new SourceDatabase();
        database.setDatabaseName(name);
        database = databaseRepository.save(database);

        try {
            // write content in a file
            File scanTemp = new File("scanTemp.xlsx");
            if(scanTemp.createNewFile()) {
                OutputStream os = new FileOutputStream(scanTemp);
                os.write(file.getBytes());
                os.close();
            }


            List<SourceTable> tables = new ArrayList<>();

            QuickAndDirtyXlsxReader workbook = new QuickAndDirtyXlsxReader(scanTemp.getAbsolutePath());

            // Create table lookup from tables overview, if it exists
            Map<String, SourceTable> nameToTable = createTablesFromTableOverview(workbook, database);

            // Field overview is the first sheet
            Sheet overviewSheet = workbook.getByName(ScanSheetName.FIELD_OVERVIEW);
            if (overviewSheet == null) {
                overviewSheet = workbook.get(0);
            }
            Iterator<Row> overviewRows = overviewSheet.iterator();

            // skip header
            overviewRows.next();

            while(overviewRows.hasNext()) {
                Row row = overviewRows.next();
                String tableName = row.getStringByHeaderName(ScanFieldName.TABLE);
                if (tableName.length() != 0) {
                    // Get table created from table overview or created before
                    SourceTable table = nameToTable.get(tableName);

                    if (table == null) {
                        table = new SourceTable();
                        table.setSourceDatabase(database);
                        table.setName(tableName);
                        //table.setDescription("");
                        table.setRowCount(row.getIntByHeaderName(ScanFieldName.N_ROWS));
                        table.setRowsCheckedCount(row.getIntByHeaderName(ScanFieldName.N_ROWS_CHECKED));
                        table = tableService.createTable(table);

                        // Add to lookup and database
                        nameToTable.put(tableName, table);
                        tables.add(table);
                    }

                    SourceField field = new SourceField();
                    field.setName(row.getStringByHeaderName(ScanFieldName.FIELD).toLowerCase());
                    field.setTable(table);
                    field.setType(row.getByHeaderName(ScanFieldName.TYPE));
                    field.setMaxLength(row.getIntByHeaderName(ScanFieldName.MAX_LENGTH));
                    //field.setDescription(row.getStringByHeaderName(ScanFieldName.DESCRIPTION));
                    field.setFractionEmpty(row.getDoubleByHeaderName(ScanFieldName.FRACTION_EMPTY));
                    field.setUniqueCount(row.getIntByHeaderName(ScanFieldName.UNIQUE_COUNT));
                    field.setFractionUnique(row.getDoubleByHeaderName(ScanFieldName.FRACTION_UNIQUE));
                    //field.setValueCounts(getValueCounts(workbook, tableName, fieldName));
                    field = fieldService.createField(field);
                }
            }
            database.setTables(tables);
            return database;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

            String description = row.getByHeaderName(ScanFieldName.DESCRIPTION);
            int nRows = row.getIntByHeaderName(ScanFieldName.N_ROWS);
            int nRowsChecked = row.getIntByHeaderName(ScanFieldName.N_ROWS_CHECKED);

            SourceTable table = new SourceTable();
            table.setSourceDatabase(database);
            table.setName(tableName);
            //table.setDescription(description);
            table.setRowCount(nRows);
            table.setRowsCheckedCount(nRowsChecked);
            table = tableService.createTable(table);

            // Add to lookup and database
            nameToTable.put(tableName, table);
            tables.add(table);
        }

        database.setTables(tables);
        return nameToTable;
    }
}
