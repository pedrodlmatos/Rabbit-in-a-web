/* Adapted from ETLWordDocumentGenerator (rabbit-in-a-hat) */
package rabbitinahat;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.*;
import rabbitcore.ooxml.CustomXWPFDocument;
import rabbitinahat.model.*;

import java.io.*;

public class ETLWordDocumentGenerator {

    public static File generate(ETL_RIAH etl, boolean includeCounts) {
        try {
            CustomXWPFDocument document = new CustomXWPFDocument();

            addTableLevelSection(document, etl);

            for (Table targetTable : etl.getTargetDB().getTables())
                addTargetTableSection(document, etl, targetTable);

            if (includeCounts)
                addSourceTablesToAppendix(document, etl);


            File file = new File("file.docx");
            FileOutputStream outputStream = new FileOutputStream(file);
            //ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.write(outputStream);
            outputStream.close();
            document.close();



            return file;

        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File generate(ETL_RIAH etl) {
        return generate(etl, true);
    }


    private static void addTableLevelSection(CustomXWPFDocument document, ETL_RIAH etl) throws FileNotFoundException, InvalidFormatException {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();

        /*
        MappingPanel mappingPanel = new MappingPanel(etl.getTableToTableMapping());
        mappingPanel.setShowOnlyConnectedItems(true);
        int height = mappingPanel.getMinimumSize().height;
        mappingPanel.setSize(800, height);
        */

        //run.setText(mappingPanel.getSourceDbName() + " Data Mapping Approach to " + mappingPanel.getTargetDbName());
        run.setText(etl.getSourceDB().getName() + " Data mapping approach to " + etl.getTargetDB().getName());
        run.setFontSize(18);

        /*
        BufferedImage image = new BufferedImage(800, height, BufferedImage.TYPE_INT_ARGB);
        image.getGraphics().setColor(Color.WHITE);
        image.getGraphics().fillRect(0, 0, image.getWidth(), image.getHeight());
        mappingPanel.paint(image.getGraphics());
        document.addPicture(image, 600, height * 6/8);
        */

    }

    private static void addTargetTableSection(CustomXWPFDocument document, ETL_RIAH etl, Table targetTable) throws FileNotFoundException, InvalidFormatException {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.addBreak(BreakType.PAGE);

        run.setText("Table name: " + targetTable.getName());
        run.setFontSize(18);

        createDocumentParagraph(document, targetTable.getComment());

        for (ItemToItemMap tableMapping : etl.getTableToTableMapping().getSourceToTargetMaps()) {
            if (tableMapping.getTargetItem() == targetTable) {
                Table sourceTable = (Table) tableMapping.getSourceItem();
                Mapping<Field> fieldMappings = etl.getFieldToFieldMapping(sourceTable, targetTable);

                paragraph = document.createParagraph();
                run = paragraph.createRun();
                run.setText("Reading from " + tableMapping.getSourceItem());
                run.setFontSize(14);

                createDocumentParagraph(document, tableMapping.getLogic());

                //createDocumentParagraph(document, tableToTableMap.getComment());

                // add picture of field mappings
                /*
                MappingPanel mappingPanel = new MappingPanel(fieldMappings);
                mappingPanel.setShowOnlyConnectedItems(true);
                int height = mappingPanel.getMinimumSize().height;
                mappingPanel.setSize(800, height);

                BufferedImage image = new BufferedImage(800, height, BufferedImage.TYPE_INT_ARGB);
                image.getGraphics().setColor(Color.WHITE);
                image.getGraphics().fillRect(0, 0, image.getWidth(), image.getHeight());
                mappingPanel.paint(image.getGraphics());
                document.addPicture(image, 600, height * 6/8);
                */

                // Add table of field mappings
                XWPFTable table = document.createTable(fieldMappings.getTargetItems().size() + 1, 4);
                XWPFTableRow header = table.getRow(0);
                setTextAndHeaderShading(header.getCell(0), "Destination field");
                setTextAndHeaderShading(header.getCell(1), "SourceField");
                setTextAndHeaderShading(header.getCell(2), "Logic");
                setTextAndHeaderShading(header.getCell(3), "Comment");

                int rowNumber = 1;
                for (MappableItem targetField : fieldMappings.getTargetItems()) {
                    XWPFTableRow row = table.getRow(rowNumber++);
                    row.getCell(0).setText(targetField.getName());

                    StringBuilder source = new StringBuilder();
                    StringBuilder logic = new StringBuilder();
                    StringBuilder comment = new StringBuilder();

                    for (ItemToItemMap fieldMap : fieldMappings.getSourceToTargetMaps()) {
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

        }
    }

    private static void createDocumentParagraph(CustomXWPFDocument document, String text) {
        if (text == null || text.equals("")) return;

        for (String line : text.split("\n")) {
            addToParagraph(document.createParagraph(), line);
        }
    }

    private static void setTextAndHeaderShading(XWPFTableCell cell, String text) {
        cell.setText(text);
        cell.setColor("AAAAFF");
    }

    private static void createCellParagraph(XWPFTableCell cell, String text) {
        if (text == null || text.equals("")) return;

        cell.removeParagraph(0);
        for(String line: text.split("\n"))
            addToParagraph(cell.addParagraph(), line);
    }

    private static void addToParagraph(XWPFParagraph paragraph, String text) {
        XWPFRun run = paragraph.createRun();
        run.setText(text);
    }

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
}
