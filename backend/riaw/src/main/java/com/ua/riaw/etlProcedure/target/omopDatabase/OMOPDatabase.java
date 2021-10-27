package com.ua.riaw.etlProcedure.target.omopDatabase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.annotations.Expose;
import com.ua.riaw.etlProcedure.ETL;
import com.ua.riaw.etlProcedure.target.omopTable.OMOPTable;
import com.ua.riaw.utils.views.Views;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "OMOP_DATABASE")
public class OMOPDatabase {

    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "version")
    @Expose
    private CDMVersion version;

    @Column(name = "database_name", nullable = false)
    @JsonView({
            Views.AdminETLProcedureList.class,
            Views.UserETLProcedureList.class,
            Views.RecentETLProcedureList.class,
            Views.ETLProcedure.class
    })
    @Expose
    private String databaseName;

    @Column(name = "vocabulary_version", nullable = true)
    @JsonView(Views.ETLProcedure.class)
    @Expose
    private String conceptIdHintsVocabularyVersion;

    @OneToMany(mappedBy = "omopDatabase", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonView(Views.ETLProcedure.class)
    @Expose
    private List<OMOPTable> tables;

    @OneToOne(mappedBy = "omopDatabase")
    @JsonIgnore
    private ETL etl;


    // CONSTRUCTORS
    public OMOPDatabase() {
        this.tables = new ArrayList<>();
    }

    public OMOPDatabase(String databaseName, ETL etl) {
        this.databaseName = databaseName;
        this.etl = etl;
    }

    public OMOPDatabase(String databaseName, CDMVersion version, String vocabularyVersion) {
        this.databaseName = databaseName;
        this.version = version;
        this.conceptIdHintsVocabularyVersion = vocabularyVersion;
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

    public List<OMOPTable> getTables() {
        return tables;
    }


    public void setTables(List<OMOPTable> tables) {
        this.tables = tables;
    }


    public ETL getEtl() {
        return etl;
    }

    public void setEtl(ETL etl) {
        this.etl = etl;
    }

    public CDMVersion getVersion() {
        return version;
    }

    public void setVersion(CDMVersion version) {
        this.version = version;
    }

    public String getConceptIdHintsVocabularyVersion() {
        return conceptIdHintsVocabularyVersion;
    }

    public void setConceptIdHintsVocabularyVersion(String conceptIdHintsVocabularyVersion) {
        this.conceptIdHintsVocabularyVersion = conceptIdHintsVocabularyVersion;
    }

    @Override
    public String toString() {
        return "OMOPDatabase{" +
                "id=" + id +
                ", version=" + version +
                ", databaseName='" + databaseName + '\'' +
                ", tables=" + tables +
                '}';
    }
}
