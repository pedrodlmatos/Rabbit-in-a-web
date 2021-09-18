/* Adapted from Table (rabbit-core) */
package rabbitinahat.model;

import com.ua.riaw.etlProcedure.source.ehrField.EHRField;
import com.ua.riaw.etlProcedure.source.ehrTable.EHRTable;
import com.ua.riaw.etlProcedure.target.omopField.OMOPField;
import com.ua.riaw.etlProcedure.target.omopTable.OMOPTable;

import java.util.ArrayList;
import java.util.List;

public class Table implements MappableItem {

    private Database database;
    private String name;
    private String description;
    private boolean stem = false;
    private int rowCount;
    private int rowsCheckedCount;
    private String comment;
    private List<Field> fields = new ArrayList<>();



    public Table() {
        super();
    }

    public Table(EHRTable ehrTable, Database database) {
        super();
        this.database = database;
        this.name = ehrTable.getName();
        this.stem = ehrTable.isStem();
        this.rowCount = ehrTable.getRowCount();
        this.rowsCheckedCount = ehrTable.getRowsCheckedCount();
        this.comment = ehrTable.getComment();
        this.fields = getFieldsFromSourceTable(ehrTable);
    }

    public Table(OMOPTable omopTable, Database database) {
        this.database = database;
        this.name = omopTable.getName();
        this.stem = omopTable.isStem();
        this.comment = omopTable.getComment();
        this.fields = getFieldsFromTargetTable(omopTable);
    }




    private List<Field> getFieldsFromSourceTable(EHRTable ehrTable) {
        List<Field> fields = new ArrayList<>();
        for (EHRField ehrField : ehrTable.getFields()) {
            Field field = new Field(ehrField, this);
            fields.add(field);
        }
        return fields;
    }

    private List<Field> getFieldsFromTargetTable(OMOPTable omopTable) {
        List<Field> fields = new ArrayList<>();
        for (OMOPField omopField : omopTable.getFields()) {
            Field field = new Field(omopField, this);
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
        return getName();
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
        return stem;
    }

    @Override
    public void setStem(boolean isStem) {

    }

}
