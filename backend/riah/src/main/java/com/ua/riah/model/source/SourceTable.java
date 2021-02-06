package com.ua.riah.model.source;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ua.riah.model.FieldMapping;

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
import java.util.List;

@Entity
@Table(name = "SOURCE_TABLE")
public class SourceTable {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "source_database_id", nullable = false)
    @JsonIgnore
    private SourceDatabase sourceDatabase;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "rowCount", nullable = true)
    private int rowCount;

    @Column(name = "rowsCheckedCount", nullable = true)
    private int rowsCheckedCount;

    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL)
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
}
