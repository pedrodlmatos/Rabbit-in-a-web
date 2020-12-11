package com.ua.riah.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "DATABASE")
public class Database {

    @Id
    @Column(name = "id", unique = true)
    @JsonView(SummaryViews.SessionSummary.class)
    private String id;

    @Column(name = "dbName", nullable = false)
    private String dbName;

    @OneToMany(mappedBy = "database", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DBTable> tables;

    @OneToMany(mappedBy = "targetDB", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ETL> etl;


    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public List<DBTable> getTables() {
        return tables;
    }

    public void setTables(List<DBTable> tables) {
        this.tables = tables;
    }

    public List<ETL> getEtl() {
        return etl;
    }

    public void setEtl(List<ETL> etl) {
        this.etl = etl;
    }
}
