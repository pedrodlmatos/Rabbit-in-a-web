package com.ua.hiah.rabbitcore.utilities.files;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WriteCSVFileWithHeader {

    private CSVPrinter printer;
    private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private boolean headerWritten = false;

    public WriteCSVFileWithHeader(String fileName) {
        this(fileName, CSVFormat.RFC4180);
    }

    public WriteCSVFileWithHeader(String fileName, CSVFormat format) {
        try {
            printer = new CSVPrinter(new PrintWriter(outputStream), format);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void write(Row row) {
        try {
            if (!headerWritten)
                writeHeader(row);

            printer.printRecord(row.getCells());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void writeHeader(Row row) {
        try {
            headerWritten = true;
            Map<String, Integer> fieldNameToColumnIndex = row.getFieldNameToColumnIndex();
            int size = fieldNameToColumnIndex.size();
            List<String> header = new ArrayList<>(size);

            for (int i = 0; i < size; i++)
                header.add(null);

            for (Map.Entry<String, Integer> entry : fieldNameToColumnIndex.entrySet())
                header.set(entry.getValue(), entry.getKey());

            printer.printRecord(header);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public byte[] closeAndGetContent() {
        try {
            printer.flush();
            printer.close();

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
