package com.ua.riah.model.source;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ua.riah.model.ETL;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "SOURCE_DATABASE")
public class SourceDatabase {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "database_name", nullable = false)
    private String databaseName;

    @OneToMany(mappedBy = "sourceDatabase", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SourceTable> tables;

    @OneToOne(mappedBy = "sourceDatabase")
    @JsonIgnore
    private ETL etl;

    // CONSTRUCTOR
    public SourceDatabase() {
    }

    // GETTER AND SETTER
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

    public List<SourceTable> getTables() {
        return tables;
    }

    public void setTables(List<SourceTable> tables) {
        this.tables = tables;
    }

    public ETL getEtl() {
        return etl;
    }

    public void setEtl(ETL etl) {
        this.etl = etl;
    }
}
