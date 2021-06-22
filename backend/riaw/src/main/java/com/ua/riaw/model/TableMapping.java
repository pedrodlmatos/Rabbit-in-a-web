package com.ua.riaw.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.annotations.Expose;
import com.ua.riaw.model.ehr.EHRTable;
import com.ua.riaw.model.omop.OMOPTable;
import com.ua.riaw.views.Views;

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
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TABLE_MAPPING")
public class TableMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonView({
            Views.ETLProcedure.class,
            Views.TableMapping.class,
            Views.CreateMapping.class,
            Views.ChangeLogic.class,
            Views.ChangeCompletion.class
    })
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ehr_table_id", nullable = false)
    @JsonView({
            Views.ETLProcedure.class,
            Views.TableMapping.class,
            Views.CreateMapping.class
    })
    @Expose
    private EHRTable ehrTable;

    @ManyToOne
    @JoinColumn(name = "omop_table_id", nullable = false)
    @JsonView({
            Views.ETLProcedure.class,
            Views.TableMapping.class,
            Views.CreateMapping.class
    })
    @Expose
    private OMOPTable omopTable;

    @OneToMany(mappedBy = "tableMapping", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonView(Views.TableMapping.class)
    @Expose
    private List<FieldMapping> fieldMappings;

    @JsonView({
            Views.ETLProcedure.class,
            Views.TableMapping.class,
            Views.CreateMapping.class,
            Views.ChangeCompletion.class
    })
    @Column(name = "complete", nullable = false)
    @Expose
    private boolean complete;

    @Column(name = "logic", nullable = true, columnDefinition = "TEXT")
    @JsonView({
            Views.ETLProcedure.class,
            Views.TableMapping.class,
            Views.CreateMapping.class,
            Views.ChangeLogic.class
    })
    @Expose
    private String logic;

    @ManyToOne
    @JoinColumn(name = "etl_id", nullable = false)
    @JsonIgnore
    private ETL etl;

    // CONSTRUCTORS
    public TableMapping() {
    }

    public TableMapping(ETL etl, EHRTable ehrTable, OMOPTable omopTable) {
        this.etl = etl;
        this.ehrTable = ehrTable;
        this.omopTable = omopTable;
        this.fieldMappings = new ArrayList<>();
    }

    public TableMapping(ETL etl, EHRTable ehrTable, OMOPTable omopTable, String logic) {
        this.etl = etl;
        this.ehrTable = ehrTable;
        this.omopTable = omopTable;
        this.logic = logic;
        this.fieldMappings = new ArrayList<>();
    }

    public TableMapping(EHRTable ehrTable, OMOPTable omopTable, boolean complete, ETL etl) {
        this.etl = etl;
        this.ehrTable = ehrTable;
        this.omopTable = omopTable;
        this.complete = complete;
        this.fieldMappings = new ArrayList<>();
    }

    // GETTERS AND SETTERS
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EHRTable getEhrTable() {
        return ehrTable;
    }

    public void setEhrTable(EHRTable source) {
        this.ehrTable = source;
    }

    public OMOPTable getOmopTable() {
        return omopTable;
    }

    public void setOmopTable(OMOPTable target) {
        this.omopTable = target;
    }

    public ETL getEtl() {
        return etl;
    }

    public void setEtl(ETL etl) {
        this.etl = etl;
    }

    public List<FieldMapping> getFieldMappings() {
        return fieldMappings;
    }


    public void setFieldMappings(List<FieldMapping> fieldMappings) {
        this.fieldMappings = fieldMappings;
    }


    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public String getLogic() {
        return logic;
    }

    public void setLogic(String logic) {
        this.logic = logic;
    }

    @Override
    public String toString() {
        return "TableMapping{" +
                "id=" + id +
                ", ehrTable=" + ehrTable +
                ", omopTable=" + omopTable +
                ", fieldMappings=" + fieldMappings +
                ", complete=" + complete +
                ", logic='" + logic + '\'' +
                '}';
    }
}
