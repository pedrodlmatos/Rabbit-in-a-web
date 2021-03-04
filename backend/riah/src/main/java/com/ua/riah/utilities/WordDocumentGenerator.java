/***
 * Adapted from rabbit-in-a-hat original code
 */
package com.ua.riah.utilities;

import com.ua.riah.model.ETL;
import com.ua.riah.model.target.TargetTable;
import com.ua.riah.utilities.files.customXWPF.CustomXWPFDocument;
import com.ua.riah.utilities.mapping.MappingPanel;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class WordDocumentGenerator {

    private ETL etl;

    public WordDocumentGenerator(ETL etl) {
        this.etl = etl;
    }

    public void generateWordDocument(ETL etl) {
        CustomXWPFDocument document = new CustomXWPFDocument();

        // add table level section
        /*
        XWPFParagraph tempParagraph = document.createParagraph();
        XWPFRun tempRun = tempParagraph.createRun();

        MappingPanel mappingPanel = new MappingPanel(etl.getTableMappings());

        tempRun.setText(etl.getSourceDatabase().getDatabaseName() + " Data Mapping Approach to " + etl.getTargetDatabase().getDatabaseName());
        tempRun.setFontSize(18);
         */

        for (TargetTable table : etl.getTargetDatabase().getTables()) {
            addTargetTableSection(document, etl, table);
        }

    }

    /**
     *
     * @param document
     * @param etl
     * @param table
     */
    private static void addTargetTableSection(CustomXWPFDocument document, ETL etl, TargetTable table) {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.addBreak(BreakType.PAGE);

        run.setText("Table name: " + table.getName());
        run.setFontSize(18);

        //createDocumentParagraph(document, )
    }
}
