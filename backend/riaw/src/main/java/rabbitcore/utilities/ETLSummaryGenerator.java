package rabbitcore.utilities;

import com.ua.riaw.etlProcedure.ETL;
import com.ua.riaw.etlProcedure.fieldMapping.FieldMapping;
import com.ua.riaw.etlProcedure.tableMapping.TableMapping;
import com.ua.riaw.etlProcedure.source.ehrField.EHRField;
import com.ua.riaw.etlProcedure.source.ehrTable.EHRTable;
import com.ua.riaw.etlProcedure.target.omopField.OMOPField;
import com.ua.riaw.etlProcedure.target.omopTable.OMOPTable;
import rabbitcore.utilities.files.Row;
import rabbitcore.utilities.files.WriteCSVFileWithHeader;
import org.apache.commons.csv.CSVFormat;

import java.util.ArrayList;
import java.util.List;

public class ETLSummaryGenerator {


    /**
     * Writes on CSV file a list of Row objects
     *
     * @param filename file name
     * @param rows list of rows
     * @return file content
     */

    public static byte[] writeCSV(String filename, List<Row> rows) {
        if (!filename.toLowerCase().endsWith(".csv"))
            filename = filename + ".csv";

        WriteCSVFileWithHeader out = new WriteCSVFileWithHeader(filename, CSVFormat.RFC4180);
        for (Row row : rows)
            out.write(row);

        byte[] content = out.closeAndGetContent();
        return content;
    }


    /**
     * Create a list of Row objects that will be used to write on the source fields summary file
     *
     * @param etl ETL procedure
     * @return List of Row objects
     */

    public static List<Row> createSourceFieldList(ETL etl) {
        List<Row> rows = new ArrayList<>();

        for (EHRTable ehrTable : etl.getSourceDatabase().getTables()) {
            for (EHRField ehrField : ehrTable.getFields()) {
                List<String> mappings = ehrField.getMappingsFromEHRField();
                // create Row object
                Row row = new Row();
                row.add("Source Table", ehrTable.getName());
                row.add("Source Field", ehrField.getName());
                row.add("Type", ehrField.getType());
                row.add("Comment", ehrField.getComment());
                row.add("Mapped?", mappings.size() > 0 ? "X" : "");
                row.add("Number of mappings", mappings.size() > 0 ? String.valueOf(mappings.size()) : "");
                row.add("Mappings", String.join(",", mappings));
                // add to list
                rows.add(row);
            }
        }
        return rows;
    }


    /**
     * Create a list of Row objects that will be used to write on the target fields summary file
     *
     * @param etl ETL procedure
     * @return List of Row objects
     */

    public static List<Row> createTargetFieldList(ETL etl) {
        List<Row> rows = new ArrayList<>();

        for (OMOPTable omopTable : etl.getTargetDatabase().getTables()) {
            for (OMOPField omopField : omopTable.getFields()) {
                List<String> mappings = omopField.getMappingsToTargetField();
                // create row object
                Row row = new Row();
                row.add("Target Table", omopTable.getName());
                row.add("Target Field", omopField.getName());
                row.add("Required?", omopField.isNullable() ? "" : "*");
                row.add("Comment", omopField.getComment());
                row.add("Mapped?", mappings.size() > 0 ? "X" : "");
                row.add("Number of mappings", mappings.size() > 0 ? String.valueOf(mappings.size()) : "");
                row.add("Mappings", String.join(",", mappings));
                // add to list
                rows.add(row);
            }
        }
        return rows;
    }


    public static List<Row> createTableMappingList(ETL etl) {
        List<Row> rows = new ArrayList<>();
        List<TableMapping> mappings = etl.getTableMappings();

        for (TableMapping mapping : mappings) {
            Row row = new Row();
            row.add("Source Table", mapping.getEhrTable().getName());
            row.add("Target Table", mapping.getOmopTable().getName());
            //row.add("Comment", mapping.comment()); TODO
            //row.add("Logic", mapping.getLogic()); TODO

            List<FieldMapping> fieldMappings = mapping.getFieldMappings();
            row.add("Number of field mappings", fieldMappings.size());

            rows.add(row);
        }

        return rows;
    }
}
