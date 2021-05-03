package com.ua.hiah.model.source;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.annotations.Expose;
import com.ua.hiah.model.FieldMapping;
import com.ua.hiah.views.Views;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "SOURCE_TABLE")
public class SourceTable {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView({Views.ETLSession.class, Views.TableMapping.class})
    private Long id;

    @Column(name = "name")
    @JsonView({Views.ETLSession.class, Views.TableMapping.class})
    @Expose
    private String name;

    @ManyToOne
    @JoinColumn(name = "source_database_id", nullable = false)
    @JsonIgnore
    private SourceDatabase sourceDatabase;

    @Column(name = "comment", nullable = true, columnDefinition = "TEXT")
    @JsonView(Views.ETLSession.class)
    @Expose
    private String comment;

    @Column(name = "stem", nullable = false)
    @JsonView(Views.ETLSession.class)
    @Expose
    private boolean stem;

    @Column(name = "row_count", nullable = true)
    @JsonView(Views.ETLSession.class)
    @Expose
    private int rowCount;

    @Column(name = "rows_checked_count", nullable = true)
    @Expose
    private int rowsCheckedCount;

    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL)
    @JsonView({Views.ETLSession.class, Views.TableMapping.class})
    @Expose
    private List<SourceField> fields;


    @OneToMany(mappedBy = "source", cascade = CascadeType.ALL)
    @Column(name = "mappings", nullable = true)
    @JsonIgnore
    private List<FieldMapping> mappings;


    // CONSTRUCTORS
    public SourceTable() {
    }

    public SourceTable(String tableName, int rowCount, int rowsCheckedCount, SourceDatabase database) {
        this.name = tableName;
        this.rowCount = rowCount;
        this.rowsCheckedCount = rowsCheckedCount;
        this.sourceDatabase = database;
        this.fields = new ArrayList<>();
        this.mappings = new ArrayList<>();
        this.stem = false;
    }

    public SourceTable(String name, int rowCount, int rowsCheckedCount, String comment, SourceDatabase sourceDatabase) {
        this.name = name;
        this.sourceDatabase = sourceDatabase;
        this.comment = comment;
        this.rowCount = rowCount;
        this.rowsCheckedCount = rowsCheckedCount;
        this.fields = new ArrayList<>();
        this.mappings = new ArrayList<>();
        this.stem = false;
    }

    public SourceTable(String name, boolean stem, SourceDatabase sourceDatabase) {
        this.name = name;
        this.stem = stem;
        this.sourceDatabase = sourceDatabase;
        this.fields = new ArrayList<>();
        this.mappings = new ArrayList<>();
    }

    // GETTERS AND SETTERS
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SourceDatabase getSourceDatabase() {
        return sourceDatabase;
    }

    public void setSourceDatabase(SourceDatabase sourceDatabase) {
        this.sourceDatabase = sourceDatabase;
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

    public List<SourceField> getFields() {
        return fields;
    }

    public void setFields(List<SourceField> fields) {
        this.fields = fields;
    }

    public List<FieldMapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<FieldMapping> mappings) {
        this.mappings = mappings;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isStem() {
        return stem;
    }

    public void setStem(boolean stem) {
        this.stem = stem;
    }

    @Override
    public String toString() {
        return "SourceTable{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", comment='" + comment + '\'' +
                ", rowCount=" + rowCount +
                ", rowsCheckedCount=" + rowsCheckedCount +
                ", fields=" + fields +
                '}';
    }

    /* Adapted from Table (rabbit-core) */
    public String createSheetNameFromTableName(String tableName) {
        String name = tableName;

        // Excel sheet names have a maximum of 31 characters
        if (name.length() > 31) {
            name = name.substring(0, 31);
        }

        // Backslash causes issues in excel
        name = name.replace('/','_');
        return name;
    }
}
