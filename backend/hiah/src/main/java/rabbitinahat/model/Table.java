/* Adapted from Table (rabbit-core) */
package rabbitinahat.model;

import com.ua.hiah.model.source.SourceField;
import com.ua.hiah.model.source.SourceTable;
import com.ua.hiah.model.target.TargetField;
import com.ua.hiah.model.target.TargetTable;

import java.util.ArrayList;
import java.util.List;

public class Table implements MappableItem {

    private Database database;
    private String name;
    private String description;
    private int rowCount;
    private int rowsCheckedCount;
    private String comment;
    private List<Field> fields = new ArrayList<>();



    public Table() {
        super();
    }

    public Table(SourceTable sourceTable, Database database) {
        super();
        this.database = database;
        this.name = sourceTable.getName();
        this.rowCount = sourceTable.getRowCount();
        this.rowsCheckedCount = sourceTable.getRowsCheckedCount();
        this.comment = sourceTable.getComment();
        this.fields = getFieldsFromSourceTable(sourceTable);
    }

    public Table(TargetTable targetTable, Database database) {
        this.database = database;
        this.name = targetTable.getName();
        this.comment = targetTable.getComment();
        this.fields = getFieldsFromTargetTable(targetTable);
    }




    private List<Field> getFieldsFromSourceTable(SourceTable sourceTable) {
        List<Field> fields = new ArrayList<>();
        for (SourceField sourceField : sourceTable.getFields()) {
            Field field = new Field(sourceField, this);
            fields.add(field);
        }
        return fields;
    }

    private List<Field> getFieldsFromTargetTable(TargetTable targetTable) {
        List<Field> fields = new ArrayList<>();
        for (TargetField targetField : targetTable.getFields()) {
            Field field = new Field(targetField, this);
            fields.add(field);
        }
        return fields;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getRowsCheckedCount() {
        return rowsCheckedCount;
    }

    public void setRowsCheckedCount(int rowsCheckedCount) {
        this.rowsCheckedCount = rowsCheckedCount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "Table{" +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", rowCount=" + rowCount +
                ", rowsCheckedCount=" + rowsCheckedCount +
                ", comment='" + comment + '\'' +
                ", fields=" + fields +
                '}';
    }

    @Override
    public String outputName() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Database getDb() {
        return null;
    }

    @Override
    public boolean isStem() {
        return false;
    }

    @Override
    public void setStem(boolean isStem) {

    }

}
