/* Adapted from ETLWordDocumentGenerator (rabbit-in-a-hat) */
package summaryGenerator;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import summaryGenerator.ooxml.CustomXWPFDocument;
import summaryGenerator.model.ETL_RIAH;
import summaryGenerator.model.Field;
import summaryGenerator.model.ItemToItemMap;
import summaryGenerator.model.MappableItem;
import summaryGenerator.model.Mapping;
import summaryGenerator.model.Table;
import summaryGenerator.panel.MappingPanel;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ETLWordDocumentGenerator {

    /**
     * Creates the Word document with all images of table and field mappings
     *
     * @param etl ETL procedure
     * @return file document
     */

    public static byte[] generate(ETL_RIAH etl) {
        try {
            CustomXWPFDocument document = new CustomXWPFDocument();
            // add table mapping image
            addTableLevelSection(document, etl);

            // add field mapping image for each target table
            for (Table targetTable : etl.getTargetDB().getTables())
                addTargetTableSection(document, etl, targetTable);

            // add appendix
            addSourceTablesToAppendix(document, etl);

            // create the file
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.write(outputStream);
            outputStream.close();
            document.close();
            return outputStream.toByteArray();
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Add the table mappings image and title
     *
     * @param document XWPFDocument object
     * @param etl ETL procedure
     * @throws FileNotFoundException
     * @throws InvalidFormatException
     */

    private static void addTableLevelSection(CustomXWPFDocument document, ETL_RIAH etl) throws FileNotFoundException, InvalidFormatException {
        // add title
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(String.format("%s Data mapping approach to %s\n", etl.getSourceDB().getName(), etl.getTargetDB().getName()));
        run.setFontSize(18);

        // creates panel with table mappings
        MappingPanel mappingPanel = new MappingPanel(etl.getTableToTableMapping());
        int height = mappingPanel.getMinimumSize().height;
        mappingPanel.setSize(800, height);

        // add panel to image and add image to object
        BufferedImage image = new BufferedImage(800, height, BufferedImage.TYPE_INT_ARGB);
        image.getGraphics().setColor(Color.WHITE);
        image.getGraphics().fillRect(0, 0, image.getWidth(), image.getHeight());
        mappingPanel.paint(image);
        document.addPicture(image, 600, height * 6/8);
    }


    /**
     * Add the information of a target table (mappings with source tables and respective field mappings)
     *
     * @param document XWPFDocument object
     * @param etl ETL procedure
     * @param targetTable OMOP CDM table
     */

    private static void addTargetTableSection(CustomXWPFDocument document, ETL_RIAH etl, Table targetTable) {
        // add title and table info
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.addBreak(BreakType.PAGE);
        run.setText("Table name: " + targetTable.getName());
        run.setFontSize(18);

        // add target table comment
        if (targetTable.getComment() != null)
            createDocumentParagraph(document, "Target table comment: " + targetTable.getComment());
        addDocumentBreak(document);

        // add image of mappings to table
        createMappingToTable(document, etl, targetTable);
        addDocumentBreak(document);

        addTableMappingSection(document, etl, targetTable);
        addDocumentBreak(document);

        // add field mappings
        for (ItemToItemMap tableToTableMap : etl.getTableToTableMapping().getSourceToTargetMaps()) {
            if (tableToTableMap.getTargetItem() == targetTable) {
                Table sourceTable = (Table) tableToTableMap.getSourceItem();
                Mapping<Field> fieldToFieldMapping = etl.getFieldToFieldMapping(sourceTable, targetTable);

                paragraph = document.createParagraph();
                run = paragraph.createRun();
                run.setText("Reading from " + tableToTableMap.getSourceItem().getName());
                run.setFontSize(14);

                createDocumentParagraph(document, tableToTableMap.getLogic());

                // add image of field mapping
                if (fieldToFieldMapping.size() > 0) {
                    MappingPanel mappingPanel = new MappingPanel(fieldToFieldMapping);
                    int height = mappingPanel.getMinimumSize().height;
                    mappingPanel.setSize(800, height);

                    try {
                        BufferedImage image = new BufferedImage(800, height, BufferedImage.TYPE_INT_ARGB);
                        image.getGraphics().setColor(Color.WHITE);
                        image.getGraphics().fillRect(0, 0, image.getWidth(), image.getHeight());
                        mappingPanel.paint(image);
                        document.addPicture(image, 600, height * 6/8);
                    } catch (InvalidFormatException | FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    XWPFTable table = document.createTable(fieldToFieldMapping.getTargetItems().size() + 1, 4);
                    XWPFTableRow header = table.getRow(0);
                    setTextAndHeaderShading(header.getCell(0), "Destination field");
                    setTextAndHeaderShading(header.getCell(1), "SourceField");
                    setTextAndHeaderShading(header.getCell(2), "Logic");
                    setTextAndHeaderShading(header.getCell(3), "Comment");

                    int rowNumber = 1;
                    for (MappableItem targetField : fieldToFieldMapping.getTargetItems()) {
                        XWPFTableRow row = table.getRow(rowNumber++);
                        row.getCell(0).setText(targetField.getName());

                        StringBuilder source = new StringBuilder();
                        StringBuilder logic = new StringBuilder();
                        StringBuilder comment = new StringBuilder();

                        for (ItemToItemMap fieldMap : fieldToFieldMapping.getSourceToTargetMaps()) {
                            if (fieldMap.getTargetItem() == targetField) {
                                if (source.length() != 0)
                                    source.append("\n");
                                source.append(fieldMap.getSourceItem().getName().trim());

                                if (logic.length() != 0)
                                    logic.append("\n");
                                if (fieldMap.getLogic() != null)
                                    logic.append(fieldMap.getLogic().trim());
                            }
                        }

                        for (Field field : targetTable.getFields()) {
                            if (field.getName().equals(targetField.getName())) {
                                if (comment.length() != 0)
                                    comment.append("\n");
                                if (field.getComment() != null)
                                    comment.append(field.getComment().trim());
                            }
                        }

                        createCellParagraph(row.getCell(1), source.toString());
                        createCellParagraph(row.getCell(2), logic.toString());
                        createCellParagraph(row.getCell(3), comment.toString());
                    }
                }

                run.addBreak();
            }
        }
    }


    /**
     * Creates a break (new line) on the document
     *
     * @param document XWPFDocument object
     */

    private static void addDocumentBreak(CustomXWPFDocument document) {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.addBreak();
    }


    /**
     * Add appendix about table of the EHR database
     *
     * @param document XWPFDocument object
     * @param etl ETL procedure
     */

    private static void addSourceTablesToAppendix(CustomXWPFDocument document, ETL_RIAH etl) {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.addBreak(BreakType.PAGE);
        run.setText("Appendix: source tables");
        run.setFontSize(18);

        for (Table sourceTable : etl.getSourceDB().getTables()) {
            paragraph = document.createParagraph();
            run = paragraph.createRun();
            run.setText("Table: " + sourceTable.getName());
            run.setFontSize(14);

            createDocumentParagraph(document, sourceTable.getComment());

            XWPFTable table = document.createTable(sourceTable.getFields().size() + 1, 4);
            // table.setWidth(2000);
            XWPFTableRow header = table.getRow(0);
            setTextAndHeaderShading(header.getCell(0), "Field");
            setTextAndHeaderShading(header.getCell(1), "Type");
            setTextAndHeaderShading(header.getCell(2), "Most freq. value");
            setTextAndHeaderShading(header.getCell(3), "Comment");
            int rowNr = 1;
            for (Field sourceField : sourceTable.getFields()) {
                XWPFTableRow row = table.getRow(rowNr++);
                row.getCell(0).setText(sourceField.getName());
                row.getCell(1).setText(sourceField.getType());
                row.getCell(2).setText(sourceField.getValueCounts().getMostFrequentValue());
                createCellParagraph(row.getCell(3), sourceField.getComment() != null ? sourceField.getComment().trim() : "");
            }

        }
        run.setFontSize(18);
    }


    /**
     * Creates images of mappings that end in a table
     *
     * @param document XWPFDocument object
     * @param etl ETL procedure
     * @param targetTable table of the OMOP CDM database
     */

    private static void createMappingToTable(CustomXWPFDocument document, ETL_RIAH etl, Table targetTable) {
        MappingPanel mappingPanel = new MappingPanel(etl.getMappingToTargetTable(targetTable));
        int height = mappingPanel.getMinimumSize().height;
        mappingPanel.setSize(800, height);

        try {
            BufferedImage image = new BufferedImage(800, height, BufferedImage.TYPE_INT_ARGB);
            image.getGraphics().setColor(Color.WHITE);
            image.getGraphics().fillRect(0, 0, image.getWidth(), image.getHeight());
            mappingPanel.paint(image);
            document.addPicture(image, 600, height * 6/8);
        } catch (InvalidFormatException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Create table with mapping info (source table, source table comment, and logic)
     *
     * @param document XWPFDocument object
     * @param etl ETL procedure
     * @param table table of the OMOP CDM database
     */

    private static void addTableMappingSection(CustomXWPFDocument document, ETL_RIAH etl, Table table) {
        XWPFTable docTable = document.createTable(etl.getNumberOfMappingsToTable(table) + 1, 3);
        XWPFTableRow header = docTable.getRow(0);
        setTextAndHeaderShading(header.getCell(0), "Source Table");
        setTextAndHeaderShading(header.getCell(1), "Source Table comment");
        setTextAndHeaderShading(header.getCell(2), "Table mapping logic");

        int nRow = 1;
        for (ItemToItemMap map : etl.getTableToTableMapping().getSourceToTargetMaps()) {
            if (map.getTargetItem() == table) {
                XWPFTableRow row = docTable.getRow(nRow++);
                row.getCell(0).setText(map.getSourceItem().getName());
                row.getCell(1).setText(((Table) map.getSourceItem()).getComment() != null ? ((Table) map.getSourceItem()).getComment() : "");
                row.getCell(2).setText(map.getLogic() != null ? map.getLogic() : "");
            }
        }
    }


    /**
     * Creates paragraph in table
     *
     * @param cell table cell
     * @param text text to fill the cell
     */

    private static void createCellParagraph(XWPFTableCell cell, String text) {
        if (text == null || text.equals("")) return;

        cell.removeParagraph(0);
        for(String line: text.split("\n"))
            addToParagraph(cell.addParagraph(), line);
    }


    /**
     * Creates a paragraph in document
     *
     * @param document XWPFDocument object
     * @param text text to fill the paragraph
     */

    private static void createDocumentParagraph(CustomXWPFDocument document, String text) {
        if (text == null || text.equals("")) return;

        for (String line : text.split("\n"))
            addToParagraph(document.createParagraph(), line);
    }


    /**
     * Adds text to paragraph
     *
     * @param paragraph XWPFParagraph object
     * @param text text to add
     */

    private static void addToParagraph(XWPFParagraph paragraph, String text) {
        XWPFRun run = paragraph.createRun();
        run.setText(text);
    }


    /**
     * Changes color of cells in a table
     *
     * @param cell table cell
     * @param text text to add to cell
     */

    private static void setTextAndHeaderShading(XWPFTableCell cell, String text) {
        cell.setText(text);
        cell.setColor("AAAAFF");
    }
}
