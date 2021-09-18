package com.ua.riaw.etlProcedure.fieldMapping;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.annotations.Expose;
import com.ua.riaw.etlProcedure.source.ehrField.EHRField;
import com.ua.riaw.etlProcedure.tableMapping.TableMapping;
import com.ua.riaw.etlProcedure.target.omopField.OMOPField;
import com.ua.riaw.utils.views.Views;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "FIELD_MAPPING")
public class FieldMapping {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView({
            Views.ETLProcedure.class,
            Views.TableMapping.class,
            Views.CreateMapping.class,
            Views.ChangeLogic.class
    })
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ehr_field_id", nullable = false)
    @JsonView({
            Views.ETLProcedure.class,
            Views.TableMapping.class,
            Views.CreateMapping.class
    })
    @Expose
    private EHRField ehrField;

    @ManyToOne
    @JoinColumn(name = "omop_field_id", nullable = false)
    @JsonView({
            Views.ETLProcedure.class,
            Views.TableMapping.class,
            Views.CreateMapping.class
    })
    @Expose
    private OMOPField omopField;

    @Column(name = "logic", nullable = true, columnDefinition = "TEXT")
    @JsonView({
            Views.ETLProcedure.class,
            Views.CreateMapping.class,
            Views.TableMapping.class,
            Views.ChangeLogic.class
    })
    @Expose
    private String logic;

    @ManyToOne
    @JoinColumn(name = "table_mapping_id", nullable = false)
    @JsonIgnore
    private TableMapping tableMapping;

    // CONSTRUCTORS
    public FieldMapping() {
    }

    public FieldMapping(EHRField ehrField, OMOPField omopField, TableMapping tableMapping) {
        this.ehrField = ehrField;
        this.omopField = omopField;
        this.tableMapping = tableMapping;
    }

    public FieldMapping(EHRField ehrField, OMOPField omopField, String logic, TableMapping tableMapping) {
        this.ehrField = ehrField;
        this.omopField = omopField;
        this.logic = logic;
        this.tableMapping = tableMapping;
    }

    // GETTERS AND SETTERS
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EHRField getEhrField() {
        return ehrField;
    }

    public void setEhrField(EHRField source) {
        this.ehrField = source;
    }

    public OMOPField getOmopField() {
        return omopField;
    }

    public void setOmopField(OMOPField target) {
        this.omopField = target;
    }

    public TableMapping getTableMapping() {
        return tableMapping;
    }

    public void setTableMapping(TableMapping tableMapping) {
        this.tableMapping = tableMapping;
    }

    public String getLogic() {
        return logic;
    }

    public void setLogic(String logic) {
        this.logic = logic;
    }
}
