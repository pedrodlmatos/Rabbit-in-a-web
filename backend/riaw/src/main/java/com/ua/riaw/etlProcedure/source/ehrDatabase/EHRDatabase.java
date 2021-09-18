package com.ua.riaw.etlProcedure.source.ehrDatabase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.annotations.Expose;
import com.ua.riaw.etlProcedure.ETL;
import com.ua.riaw.etlProcedure.source.ehrTable.EHRTable;
import com.ua.riaw.utils.views.Views;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "EHR_DATABASE")
public class EHRDatabase {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "database_name")
    @JsonView({
            Views.AdminETLProcedureList.class,
            Views.UserETLProcedureList.class,
            Views.RecentETLProcedureList.class,
            Views.ETLProcedure.class
    })
    @Expose
    private String databaseName;

    @OneToMany(mappedBy = "ehrDatabase", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonView(Views.ETLProcedure.class)
    @Expose
    private List<EHRTable> tables;

    @OneToOne(mappedBy = "ehrDatabase")
    @JsonIgnore
    private ETL etl;

    // CONSTRUCTORS
    public EHRDatabase() {
    }

    public EHRDatabase(String name) {
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

    public List<EHRTable> getTables() {
        return tables;
    }

    public void setTables(List<EHRTable> tables) {
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
        return "EHRDatabase{" +
                "id=" + id +
                ", databaseName='" + databaseName + '\'' +
                ", tables=" + tables +
                ", etl=" + etl +
                '}';
    }
}
