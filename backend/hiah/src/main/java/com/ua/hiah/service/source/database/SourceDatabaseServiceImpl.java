package com.ua.hiah.service.source.database;

import com.ua.hiah.model.source.SourceDatabase;
import com.ua.hiah.model.source.SourceField;
import com.ua.hiah.model.source.SourceTable;
import com.ua.hiah.model.source.ValueCount;
import com.ua.hiah.rabbitcore.utilities.ScanFieldName;
import com.ua.hiah.rabbitcore.utilities.ScanSheetName;
import com.ua.hiah.rabbitcore.utilities.files.QuickAndDirtyXlsxReader;
import com.ua.hiah.rabbitcore.utilities.files.QuickAndDirtyXlsxReader.Row;
import com.ua.hiah.rabbitcore.utilities.files.QuickAndDirtyXlsxReader.Sheet;
import com.ua.hiah.repository.source.SourceDatabaseRepository;
import com.ua.hiah.service.source.field.SourceFieldService;
import com.ua.hiah.service.source.table.SourceTableService;
import com.ua.hiah.service.source.valueCounts.ValueCountService;
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

    @Autowired
    ValueCountService valueCountService;

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
                    field.setFractionEmpty(row.getDoubleByHeaderName(ScanFieldName.FRACTION_EMPTY));
                    field.setUniqueCount(row.getIntByHeaderName(ScanFieldName.UNIQUE_COUNT) == null ? -1 : row.getIntByHeaderName(ScanFieldName.UNIQUE_COUNT));
                    field.setFractionUnique(row.getDoubleByHeaderName(ScanFieldName.FRACTION_UNIQUE) == null ? -1 : row.getDoubleByHeaderName(ScanFieldName.FRACTION_UNIQUE));
                    //field.setValueCounts(getValueCounts(workbook, tableName, fieldName));
                    field = fieldService.createField(field);

                    Map<String, Integer> valueCounts = getValueCounts(workbook, table, row.getStringByHeaderName(ScanFieldName.FIELD));
                    if (valueCounts != null) {
                        int totalCount = valueCounts.values().stream().reduce(0, Integer::sum);
                        //List<ValueCount> valueCountsList = new ArrayList<>();

                        for (Map.Entry<String, Integer> entry : valueCounts.entrySet()) {
                            System.out.println(entry.getKey());
                            ValueCount valueCount = new ValueCount();
                            valueCount.setValue(entry.getKey());
                            valueCount.setFrequency(entry.getValue());
                            valueCount.setPercentage(entry.getValue() / (double) totalCount);
                            valueCount.setField(field);
                            //valueCountsList.add(valueCount);
                            valueCount = valueCountService.createValueCount(valueCount);
                        }
                        //valueCountService.createAll(valueCountsList);
                    }
                }
            }
            database.setTables(tables);

            scanTemp.delete();
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

    /* Adapted from Database (rabbit-core) */
    private static Map<String, Integer> getValueCounts(QuickAndDirtyXlsxReader workbook, SourceTable table, String fieldName) {
        String targetSheetName = table.createSheetNameFromTableName(table.getName());
        System.out.println(targetSheetName);
        Sheet tableSheet = workbook.getByName(targetSheetName);

        if (tableSheet == null) {
            return null;
        }

        Map<String, Integer> valueCounts = new HashMap<>();
        Iterator<Row> iterator = tableSheet.iterator();
        Row header = iterator.next();
        System.out.println(header.toString());
        int index = header.indexOf(fieldName);
        System.out.println(index);

        if (index != -1) {
            while (iterator.hasNext()) {
                Row row = iterator.next();
                if (row.size() > index) {
                    String value = row.get(index);
                    System.out.println("Val " + value);
                    String count;

                    if (row.size() > index + 1) {
                        count = row.get(index + 1);
                    } else {
                        count = "";
                    }

                    if (value.equals("") && count.equals("")){
                        break;
                    }

                    try {
                        valueCounts.put(value, (int) Double.parseDouble(count));
                    } catch (NumberFormatException e) {

                    }
                }
            }
        }

        return valueCounts;
    }
}
