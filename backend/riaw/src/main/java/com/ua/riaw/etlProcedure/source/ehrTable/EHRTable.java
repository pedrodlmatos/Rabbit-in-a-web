package com.ua.riaw.etlProcedure.source.ehrTable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.annotations.Expose;
import com.ua.riaw.etlProcedure.source.ehrDatabase.EHRDatabase;
import com.ua.riaw.etlProcedure.tableMapping.TableMapping;
import com.ua.riaw.etlProcedure.source.ehrField.EHRField;
import com.ua.riaw.utils.views.Views;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "EHR_TABLE")
public class EHRTable {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView({
            Views.ETLProcedure.class,
            Views.TableMapping.class,
            Views.ChangeComment.class,
            Views.CreateMapping.class
    })
    private Long id;

    @Column(name = "name")
    @JsonView({
            Views.ETLProcedure.class,
            Views.TableMapping.class,
            Views.CreateMapping.class
    })
    @Expose
    private String name;

    @ManyToOne
    @JoinColumn(name = "ehr_database_id", nullable = false)
    @JsonIgnore
    private EHRDatabase ehrDatabase;

    @Column(name = "comment", nullable = true, columnDefinition = "TEXT")
    @JsonView({
            Views.ETLProcedure.class,
            Views.ChangeComment.class
    })
    @Expose
    private String comment;

    @Column(name = "stem", nullable = false)
    @JsonView({
            Views.ETLProcedure.class,
            Views.TableMapping.class
    })
    @Expose
    private boolean stem;

    @Column(name = "row_count", nullable = true)
    @JsonView(Views.ETLProcedure.class)
    @Expose
    private int rowCount;

    @Column(name = "rows_checked_count", nullable = true)
    @Expose
    private int rowsCheckedCount;

    @OneToMany(mappedBy = "ehrTable", cascade = CascadeType.ALL)
    @JsonView({
            Views.ETLProcedure.class,
            Views.TableMapping.class,
            Views.ETLProcedure.class,
            Views.CreateMapping.class
    })
    @Expose
    private List<EHRField> fields;


    @OneToMany(mappedBy = "ehrTable", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<TableMapping> mappings;


    // CONSTRUCTORS
    public EHRTable() {
    }

    public EHRTable(String tableName, int rowCount, int rowsCheckedCount, EHRDatabase database) {
        this.name = tableName;
        this.rowCount = rowCount;
        this.rowsCheckedCount = rowsCheckedCount;
        this.ehrDatabase = database;
        this.fields = new ArrayList<>();
        this.mappings = new ArrayList<>();
        this.stem = false;
    }

    public EHRTable(String name, int rowCount, int rowsCheckedCount, String comment, EHRDatabase ehrDatabase) {
        this.name = name;
        this.ehrDatabase = ehrDatabase;
        this.comment = comment;
        this.rowCount = rowCount;
        this.rowsCheckedCount = rowsCheckedCount;
        this.fields = new ArrayList<>();
        this.mappings = new ArrayList<>();
        this.stem = false;
    }

    public EHRTable(String name, boolean stem, EHRDatabase ehrDatabase) {
        this.name = name;
        this.stem = stem;
        this.ehrDatabase = ehrDatabase;
        this.fields = new ArrayList<>();
        this.mappings = new ArrayList<>();
    }

    public EHRTable(String name, boolean stem, int rowCount, int rowsCheckedCount, String comment, EHRDatabase database) {
        this.name = name;
        this.stem = stem;
        this.rowCount = rowCount;
        this.rowsCheckedCount = rowsCheckedCount;
        this.comment = comment;
        this.ehrDatabase = database;
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

    public EHRDatabase getSourceDatabase() {
        return ehrDatabase;
    }

    public void setSourceDatabase(EHRDatabase ehrDatabase) {
        this.ehrDatabase = ehrDatabase;
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

    public List<EHRField> getFields() {
        return fields;
    }

    public void setFields(List<EHRField> fields) {
        this.fields = fields;
    }

    public List<TableMapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<TableMapping> mappings) {
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
        return "EHRTable{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", comment='" + comment + '\'' +
                ", stem=" + stem +
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
