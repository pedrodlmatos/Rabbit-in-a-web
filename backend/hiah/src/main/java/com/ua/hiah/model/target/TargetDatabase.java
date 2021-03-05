package com.ua.hiah.model.target;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.ua.hiah.model.CDMVersion;
import com.ua.hiah.model.ETL;
import com.ua.hiah.views.Views;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "TARGET_DATABASE")
public class TargetDatabase {

    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "version", unique = true)
    private CDMVersion version;

    @Column(name = "database_name", nullable = false)
    @JsonView(Views.ETLSessionsList.class)
    private String databaseName;

    @OneToMany(mappedBy = "targetDatabase", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonView(Views.ETLSession.class)
    private List<TargetTable> tables;

    @OneToMany(mappedBy = "targetDatabase", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ETL> etl;


    // CONSTRUCTOR
    public TargetDatabase() {
    }

    public TargetDatabase(String databaseName, List<ETL> etl) {
        this.databaseName = databaseName;
        this.etl = etl;
    }

    // GETTERS AND SETTERS
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public List<TargetTable> getTables() {
        return tables;
    }

    public void setTables(List<TargetTable> tables) {
        this.tables = tables;
    }

    public List<ETL> getEtl() {
        return etl;
    }

    public void setEtl(List<ETL> etl) {
        this.etl = etl;
    }

    public CDMVersion getVersion() {
        return version;
    }

    public void setVersion(CDMVersion version) {
        this.version = version;
    }


}
