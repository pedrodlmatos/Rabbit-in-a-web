package rabbitcore.utilities.files;

import rabbitcore.utilities.StringUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Row {
    private List<String> cells;
    private Map<String, Integer> fieldNameToColumnIndex;

    public Row() {
        fieldNameToColumnIndex = new HashMap<>();
        cells = new ArrayList<>();
    }

    public Row(List<String> cells, Map<String, Integer> fieldNameToColumnIndex) {
        this.cells = cells;
        this.fieldNameToColumnIndex = fieldNameToColumnIndex;
    }

    public Row(Row row) {
        cells = new ArrayList<>(row.cells);
        fieldNameToColumnIndex = new HashMap<>(row.fieldNameToColumnIndex);
    }

    public String get(String fieldName) {
        int index;
        try {
            index = fieldNameToColumnIndex.get(fieldName);
        } catch (NullPointerException e) {
            throw new RuntimeException(String.format("Field %s not found", fieldName));
        }

        if (cells.size() <= index)
            return null;
        else
            return cells.get(index);
    }

    public List<String> getFieldNames() {
        List<String> names = new ArrayList<>(fieldNameToColumnIndex.size());
        for (int i = 0; i < fieldNameToColumnIndex.size(); i++)
            names.add(null);

        for (Map.Entry<String, Integer> entry : fieldNameToColumnIndex.entrySet())
            names.set(entry.getValue(), entry.getKey());

        return names;
    }

    public int getInt(String fieldName) {
        return Integer.parseInt(get(fieldName).trim());
    }

    public long getLong(String fieldName) {
        return Long.parseLong(get(fieldName).trim());
    }

    public double getDouble(String fieldName) {
        return Double.parseDouble(get(fieldName).trim());
    }

    public void add(String fieldName, String value) {
        fieldNameToColumnIndex.put(fieldName, cells.size());
        cells.add(value);
    }

    public void add(String fieldName, int value) {
        add(fieldName, Integer.toString(value));
    }

    public void add(String fieldName, boolean value) {
        add(fieldName, Boolean.toString(value));
    }

    public void add(String fieldName, double value) {
        add(fieldName, Double.toString(value));
    }

    public void add(String fieldName, long value) {
        add(fieldName, Long.toString(value));
    }

    public void set(String fieldName, String value) {
        cells.set(fieldNameToColumnIndex.get(fieldName), value);
    }

    public void set(String fieldName, int value) {
        set(fieldName, Integer.toString(value));
    }

    public void set(String fieldName, long value) {
        set(fieldName, Long.toString(value));
    }

    public void set(String fieldName, double value) {
        set(fieldName, Double.toString(value));
    }

    public List<String> getCells() {
        return cells;
    }

    public Map<String, Integer> getFieldNameToColumnIndex() {
        return fieldNameToColumnIndex;
    }

    public String toString() {
        List<String> data = new ArrayList<>(cells);
        for (String fieldName : fieldNameToColumnIndex.keySet()) {
            int index = fieldNameToColumnIndex.get(fieldName);
            if (data.size() > index)
                data.set(index, String.format("[%s: %s]", fieldName, data.get(index)));
        }

        return StringUtilities.join(data, ",");
    }

    public void remove(String field) {
        int index = fieldNameToColumnIndex.remove(field);
        cells.remove(index);
        Map<String, Integer> tempMap = new HashMap<>();

        for (Map.Entry<String, Integer> entry : fieldNameToColumnIndex.entrySet()) {
            if (entry.getValue() > index)
                tempMap.put(entry.getKey(), entry.getValue() - 1);
            else
                tempMap.put(entry.getKey(), entry.getValue());
        }
    }

    public void upperCaseFieldNames() {
        Map<String, Integer> tempMap = new HashMap<>();

        for (Map.Entry<String, Integer> entry : fieldNameToColumnIndex.entrySet())
            tempMap.put(entry.getKey().toUpperCase(), entry.getValue());

        fieldNameToColumnIndex = tempMap;
    }
}
