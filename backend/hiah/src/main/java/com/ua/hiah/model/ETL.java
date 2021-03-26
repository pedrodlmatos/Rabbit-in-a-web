package com.ua.hiah.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.ua.hiah.model.source.SourceDatabase;
import com.ua.hiah.model.target.TargetDatabase;
import com.ua.hiah.views.Views;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "ETL")
public class ETL {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.ETLSessionsList.class)
    private Long id;

    @Column(name = "name", nullable = false)
    @JsonView(Views.ETLSessionsList.class)
    private String name;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "sourceDatabase_id", referencedColumnName = "id")
    @JsonView(Views.ETLSessionsList.class)
    private SourceDatabase sourceDatabase;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "targetDatabase_id", referencedColumnName = "id")
    @JsonView(Views.ETLSessionsList.class)
    private TargetDatabase targetDatabase;

    @OneToMany(mappedBy = "etl", cascade = CascadeType.ALL)
    @JsonView(Views.ETLSession.class)
    private List<TableMapping> tableMappings;


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

    public TargetDatabase getTargetDatabase() {
        return targetDatabase;
    }

    public void setTargetDatabase(TargetDatabase targetDatabase) {
        this.targetDatabase = targetDatabase;
    }

    public List<TableMapping> getTableMappings() {
        return tableMappings;
    }

    public void setTableMappings(List<TableMapping> tableMappings) {
        this.tableMappings = tableMappings;
    }

    @Override
    public String toString() {
        return "ETL{" +
                "id=" + id +
                ", name='" + name + '\'' +
                //", sourceDatabase=" + sourceDatabase +
                ", targetDatabase=" + targetDatabase +
                ", tableMappings=" + tableMappings +
                '}';
    }
}
