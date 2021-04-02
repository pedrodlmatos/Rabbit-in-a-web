package com.ua.hiah.model.source;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.ua.hiah.model.FieldMapping;
import com.ua.hiah.views.Views;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "SOURCE_TABLE")
public class SourceTable {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(Views.ETLSession.class)
    private Long id;

    @Column(name = "name")
    @JsonView(Views.ETLSession.class)
    private String name;

    @ManyToOne
    @JoinColumn(name = "source_database_id", nullable = false)
    @JsonIgnore
    private SourceDatabase sourceDatabase;

    @Column(name = "comment", nullable = true, columnDefinition = "TEXT")
    @JsonView(Views.ETLSession.class)
    private String comment;

    @Column(name = "rowCount", nullable = true)
    @JsonView(Views.ETLSession.class)
    private int rowCount;

    @Column(name = "rowsCheckedCount", nullable = true)
    private int rowsCheckedCount;

    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL)
    @JsonView(Views.ETLSession.class)
    private List<SourceField> fields;


    @OneToMany(mappedBy = "source", cascade = CascadeType.ALL)
    @Column(name = "mappings", nullable = true)
    @JsonIgnore
    private List<FieldMapping> mappings;


    // CONSTRUCTOR
    public SourceTable() {
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
}
