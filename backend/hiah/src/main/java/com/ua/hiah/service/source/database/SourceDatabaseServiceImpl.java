package com.ua.hiah.service.source.database;

import com.ua.hiah.model.CDMVersion;
import com.ua.hiah.model.StemTableFile;
import com.ua.hiah.model.source.SourceDatabase;
import com.ua.hiah.model.source.SourceField;
import com.ua.hiah.model.source.SourceTable;
import com.ua.hiah.model.source.ValueCount;
import com.ua.hiah.service.source.table.SourceTableService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import rabbitcore.utilities.ScanFieldName;
import rabbitcore.utilities.ScanSheetName;
import rabbitcore.utilities.files.QuickAndDirtyXlsxReader;
import rabbitcore.utilities.files.QuickAndDirtyXlsxReader.Row;
import rabbitcore.utilities.files.QuickAndDirtyXlsxReader.Sheet;
import com.ua.hiah.repository.source.SourceDatabaseRepository;
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

    @Autowired
    private SourceDatabaseRepository databaseRepository;

    @Autowired
    private SourceTableService sourceTableService;


    /**
     * Reads Scan report generated by White Rabbit and persists its information
     *
     * @param name EHR database name
     * @param file Scan report file
     * @return Source Database (with its tables, fields and value counts)
     */

    @Override
    public SourceDatabase createDatabaseFromScanReport(String name, MultipartFile file) {
        /* Adapted from Database (rabbit-core) */
        SourceDatabase database = new SourceDatabase(name);
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
                        table = new SourceTable(
                            tableName,
                            row.getIntByHeaderName(ScanFieldName.N_ROWS),
                            row.getIntByHeaderName(ScanFieldName.N_ROWS_CHECKED),
                            database
                        );
                        // Add to lookup and database
                        nameToTable.put(tableName, table);
                    }
                    tables.add(table);

                    // Get field
                    SourceField field = new SourceField(
                        row.getStringByHeaderName(ScanFieldName.FIELD).toLowerCase(),
                        row.getByHeaderName(ScanFieldName.TYPE),
                        row.getIntByHeaderName(ScanFieldName.MAX_LENGTH),
                        row.getDoubleByHeaderName(ScanFieldName.FRACTION_EMPTY),
                        row.getIntByHeaderName(ScanFieldName.UNIQUE_COUNT) == null ? -1 : row.getIntByHeaderName(ScanFieldName.UNIQUE_COUNT),
                        row.getDoubleByHeaderName(ScanFieldName.FRACTION_UNIQUE) == null ? -1 : row.getDoubleByHeaderName(ScanFieldName.FRACTION_UNIQUE),
                        table
                    );
                    table.getFields().add(field);

                    // Get value counts of the field
                    Map<String, Integer> valueCounts = getValueCounts(workbook, table, row.getStringByHeaderName(ScanFieldName.FIELD));
                    if (valueCounts != null) {
                        int totalCount = valueCounts.values().stream().reduce(0, Integer::sum);
                        for (Map.Entry<String, Integer> entry : valueCounts.entrySet()) {
                            ValueCount valueCount = new ValueCount(
                                entry.getKey(),
                                entry.getValue(),
                                entry.getValue() / (double) totalCount,
                                field
                            );
                            field.getValueCounts().add(valueCount);
                        }
                    }
                }
            }
            database.setTables(tables);
            if (scanTemp.delete()) { };
            //return databaseRepository.save(database);
            return database;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Creates and persists the content of an EHR database contained in a JSON file
     *
     * @param sourceDatabase EHR database stored in JSON file
     * @return created source database
     */

    @Override
    public SourceDatabase createDatabaseFromJSON(SourceDatabase sourceDatabase) {
        SourceDatabase response = new SourceDatabase(sourceDatabase.getDatabaseName());
        List<SourceTable> tables = new ArrayList<>();
        for (SourceTable table : sourceDatabase.getTables()) {
            // Get table
            SourceTable responseTable = new SourceTable(
                table.getName(),
                table.isStem(),
                table.getRowCount(),
                table.getRowsCheckedCount(),
                table.getComment(),
                response
            );
            tables.add(responseTable);
            // Get field of the table
            for (SourceField field : table.getFields()) {
                SourceField responseField = new SourceField(
                    field.getName(),
                    field.getType(),
                    field.getMaxLength(),
                    field.getFractionEmpty(),
                    field.getUniqueCount(),
                    field.getFractionUnique(),
                    field.getComment(),
                    responseTable
                );
                responseTable.getFields().add(responseField);
                // Get value counts of a field
                for (ValueCount valueCount : field.getValueCounts()) {
                    ValueCount responseValue = new ValueCount(
                        valueCount.getValue(),
                        valueCount.getFrequency(),
                        valueCount.getPercentage(),
                        responseField
                    );
                    responseField.getValueCounts().add(responseValue);
                }
            }
        }
        response.setTables(tables);
        return response;
    }


    /**
     * Adds stem table to EHR database and its mappings (contained in file)
     *
     * @param version OMOP CDM version
     * @param sourceDatabase EHR database object
     * @return altered source database
     */

    @Override
    public SourceTable createSourceStemTable(CDMVersion version, SourceDatabase sourceDatabase) {
        SourceTable stemSourceTable = new SourceTable(
            "stem_table",
            true,
            sourceDatabase
        );

        try {
            StemTableFile stemTableFile = StemTableFile.valueOf(version.name());
            FileInputStream fileInputStream = new FileInputStream(stemTableFile.fileName);

            for (CSVRecord row : CSVFormat.RFC4180.withHeader().parse(new InputStreamReader(fileInputStream))) {
                // get source fields
                SourceField field = new SourceField(
                    row.get("COLUMN_NAME").toLowerCase(),
                    row.get("IS_NULLABLE").equals("YES"),
                    row.get("DATA_TYPE"),
                    row.get("DESCRIPTION"),
                    true,
                    stemSourceTable
                );
                stemSourceTable.getFields().add(field);
            }
            return stemSourceTable;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Remove stem table from EHR database
     *
     * @param table stem table
     */

    @Override
    public void removeTable(SourceTable table) {
        sourceTableService.removeStemTable(table);
    }


    /**
     * Create source database tables
     *
     * @param workbook Xlsx file
     * @param database Source database
     * @return map with tables
     */

    private Map<String, SourceTable> createTablesFromTableOverview(QuickAndDirtyXlsxReader workbook, SourceDatabase database) {
        /* Adapted from Database (rabbit-core) */
        QuickAndDirtyXlsxReader.Sheet tableOverviewSheet = workbook.getByName(ScanSheetName.TABLE_OVERVIEW);
        List<SourceTable> tables = new ArrayList<>();
        if (tableOverviewSheet == null)      // No table overview sheet, empty nameToTable
            return new HashMap<>();

        Map<String, SourceTable> nameToTable = new HashMap<>();
        Iterator<QuickAndDirtyXlsxReader.Row> tableRows = tableOverviewSheet.iterator();
        tableRows.next();  // Skip header
        while (tableRows.hasNext()) {
            QuickAndDirtyXlsxReader.Row row = tableRows.next();
            String tableName = row.getByHeaderName(ScanFieldName.TABLE);
            String description = row.getByHeaderName(ScanFieldName.DESCRIPTION);
            int nRows = row.getIntByHeaderName(ScanFieldName.N_ROWS);
            int nRowsChecked = row.getIntByHeaderName(ScanFieldName.N_ROWS_CHECKED);
            SourceTable table = new SourceTable(tableName, nRows, nRowsChecked, database);
            // Add to lookup and database
            nameToTable.put(tableName, table);
            tables.add(table);
        }
        database.setTables(tables);
        return nameToTable;
    }


    /**
     * Get the value counts for field
     *
     * @param workbook xlsx reader
     * @param table table
     * @param fieldName field
     * @return map with each value and its count
     */

    private static Map<String, Integer> getValueCounts(QuickAndDirtyXlsxReader workbook, SourceTable table, String fieldName) {
        /* Adapted from Database (rabbit-core) */
        String targetSheetName = table.createSheetNameFromTableName(table.getName());
        Sheet tableSheet = workbook.getByName(targetSheetName);

        if (tableSheet == null) {
            return null;
        }

        Map<String, Integer> valueCounts = new HashMap<>();
        Iterator<Row> iterator = tableSheet.iterator();
        Row header = iterator.next();
        int index = header.indexOf(fieldName);
        if (index != -1) {
            while (iterator.hasNext()) {
                Row row = iterator.next();
                if (row.size() > index) {
                    String value = row.get(index);
                    String count;

                    if (row.size() > index + 1)
                        count = row.get(index + 1);
                    else
                        count = "";

                    if (value.equals("") && count.equals(""))
                        break;

                    try {
                        valueCounts.put(value, (int) Double.parseDouble(count));
                    } catch (NumberFormatException e) { }
                }
            }
        }
        return valueCounts;
    }
}
