package com.ua.hiah.model.target;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.ua.hiah.model.CDMVersion;
import com.ua.hiah.model.ETL;
import com.ua.hiah.views.Views;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "TARGET_DATABASE")
public class TargetDatabase {

    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "version")
    private CDMVersion version;

    @Column(name = "database_name", nullable = false)
    @JsonView(Views.ETLSessionsList.class)
    private String databaseName;

    @Column(name = "vocabulary_version", nullable = true)
    @JsonView(Views.ETLSession.class)
    private String conceptIdHintsVocabularyVersion;

    @OneToMany(mappedBy = "targetDatabase", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JsonView(Views.ETLSession.class)
    private List<TargetTable> tables;

    @OneToOne(mappedBy = "targetDatabase")
    @JsonIgnore
    private ETL etl;


    // CONSTRUCTOR
    public TargetDatabase() {
    }

    public TargetDatabase(String databaseName, ETL etl) {
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
        return "TargetDatabase{" +
                "id=" + id +
                ", version=" + version +
                ", databaseName='" + databaseName + '\'' +
                ", tables=" + tables +
                '}';
    }
}
