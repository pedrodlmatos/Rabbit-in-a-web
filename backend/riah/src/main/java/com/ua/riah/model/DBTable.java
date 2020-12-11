package com.ua.riah.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "DBTABLE")
public class DBTable {

    @Id
    @Column(name = "id", unique = true)
    private String id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "database_id", nullable = false)
    @JsonIgnore
    private Database database;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "rowCount", nullable = true)
    private int rowCount;

    @Column(name = "rowsCheckedCount", nullable = true)
    private int rowsCheckedCount;

    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL)
    private List<Field> fields;


    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
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
}
