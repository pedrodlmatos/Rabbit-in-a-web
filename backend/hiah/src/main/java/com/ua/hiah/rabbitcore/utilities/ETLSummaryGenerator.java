package com.ua.hiah.rabbitcore.utilities;

import com.ua.hiah.model.ETL;
import com.ua.hiah.model.FieldMapping;
import com.ua.hiah.model.TableMapping;
import com.ua.hiah.model.source.SourceField;
import com.ua.hiah.model.source.SourceTable;
import com.ua.hiah.model.target.TargetField;
import com.ua.hiah.model.target.TargetTable;
import com.ua.hiah.rabbitcore.utilities.files.Row;
import com.ua.hiah.rabbitcore.utilities.files.WriteCSVFileWithHeader;
import org.apache.commons.csv.CSVFormat;

import java.util.ArrayList;
import java.util.List;

public class ETLSummaryGenerator {

    public static byte[] writeCSV(String filename, List<Row> rows) {
        if (!filename.toLowerCase().endsWith(".csv"))
            filename = filename + ".csv";

        WriteCSVFileWithHeader out = new WriteCSVFileWithHeader(filename, CSVFormat.RFC4180);
        for (Row row : rows)
            out.write(row);

        byte[] content = out.closeAndGetContent();
        return content;
    }

    static void generateSourceFieldListCSV(ETL etl, String filename) {
        writeCSV(filename, createSourceFieldList(etl));
    }

    public static List<Row> createSourceFieldList(ETL etl) {
        List<Row> rows = new ArrayList<>();

        for (SourceTable sourceTable : etl.getSourceDatabase().getTables()) {
            for (SourceField sourceField : sourceTable.getFields()) {
                List<String> mappings = sourceField.getMappingsFromSourceField();

                Row row = new Row();
                row.add("Source Table", sourceTable.getName());
                row.add("Source Field", sourceField.getName());
                row.add("Type", sourceField.getType());
                row.add("Comment", sourceField.getComment());
                row.add("Mapped?", mappings.size() > 0 ? "X" : "");
                row.add("Number of mappings", mappings.size() > 0 ? String.valueOf(mappings.size()) : "");
                row.add("Mappings", String.join(",", mappings));

                rows.add(row);
            }
        }
        return rows;
    }

    static void generateTargetFieldListCSV(ETL etl, String filename) {
        writeCSV(filename, createTargetFieldList(etl));
    }

    public static List<Row> createTargetFieldList(ETL etl) {
        List<Row> rows = new ArrayList<>();

        for (TargetTable targetTable : etl.getTargetDatabase().getTables()) {
            for (TargetField targetField : targetTable.getFields()) {
                List<String> mappings = targetField.getMappingsToTargetField();

                Row row = new Row();
                row.add("Target Table", targetTable.getName());
                row.add("Target Field", targetField.getName());
                row.add("Required?", targetField.isNullable() ? "" : "*");
                row.add("Comment", targetField.getComment());
                row.add("Mapped?", mappings.size() > 0 ? "X" : "");
                row.add("Number of mappings", mappings.size() > 0 ? String.valueOf(mappings.size()) : "");
                row.add("Mappings", String.join(",", mappings));

                rows.add(row);
            }
        }

        return rows;
    }

    static void generateTableMappingsCSV(ETL etl, String filename) {
        writeCSV(filename, createTableMappingList(etl));
    }

    public static List<Row> createTableMappingList(ETL etl) {
        List<Row> rows = new ArrayList<>();
        List<TableMapping> mappings = etl.getTableMappings();

        for (TableMapping mapping : mappings) {
            Row row = new Row();
            row.add("Source Table", mapping.getSource().getName());
            row.add("Target Table", mapping.getTarget().getName());
            //row.add("Comment", mapping.comment()); TODO
            //row.add("Logic", mapping.getLogic()); TODO

            List<FieldMapping> fieldMappings = mapping.getFieldMappings();
            row.add("Number of field mappings", fieldMappings.size());

            rows.add(row);
        }

        return rows;
    }
}
