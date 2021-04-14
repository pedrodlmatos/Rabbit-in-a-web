package com.ua.hiah.model.source;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.annotations.Expose;
import com.ua.hiah.model.ETL;
import com.ua.hiah.views.Views;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "SOURCE_DATABASE")
public class SourceDatabase {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "database_name")
    @JsonView(Views.ETLSessionsList.class)
    @Expose
    private String databaseName;

    @OneToMany(mappedBy = "sourceDatabase", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonView(Views.ETLSession.class)
    @Expose
    private List<SourceTable> tables;

    @OneToOne(mappedBy = "sourceDatabase")
    @JsonIgnore
    private ETL etl;

    // CONSTRUCTORS
    public SourceDatabase() {
    }

    public SourceDatabase(String name) {
        this.databaseName = name;
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

    @Override
    public String toString() {
        return String.format("SourceDatabase{\n" +
                "\tid=%s,\n" +
                "\tdatabaseName=%s,\n" +
                "\ttables=%s\n" +
                "}", id, databaseName, tables);
    }
}
