package com.ua.riaw.etlProcedure.target.omopTable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.annotations.Expose;
import com.ua.riaw.etlProcedure.target.omopDatabase.OMOPDatabase;
import com.ua.riaw.etlProcedure.tableMapping.TableMapping;
import com.ua.riaw.etlProcedure.target.omopField.OMOPField;
import com.ua.riaw.utils.views.Views;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(name = "OMOP_TABLE")
public class OMOPTable {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView({
            Views.ETLProcedure.class,
            Views.TableMapping.class,
            Views.ChangeComment.class,
            Views.CreateMapping.class
    })
    private Long id;

    @Column(name = "name")
    @JsonView({
            Views.ETLProcedure.class,
            Views.TableMapping.class,
            Views.CreateMapping.class
    })
    @Expose
    private String name;

    @ManyToOne
    @JoinColumn(name = "omop_database_id")
    @JsonIgnore
    private OMOPDatabase omopDatabase;

    @Column(name = "comment", nullable = true, columnDefinition = "TEXT")
    @JsonView({
            Views.ETLProcedure.class,
            Views.ChangeComment.class
    })
    @Expose
    private String comment;

    @Column(name = "stem", nullable = false)
    @JsonView({
            Views.ETLProcedure.class,
            Views.TableMapping.class
    })
    @Expose
    private boolean stem;

    @OneToMany(mappedBy = "omopTable", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonView({
            Views.ETLProcedure.class,
            Views.TableMapping.class
    })
    @Expose
    private List<OMOPField> fields;


    @OneToMany(mappedBy = "omopTable", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<TableMapping> mappings;


    // CONSTRUCTORS
    public OMOPTable() {
        this.fields = new ArrayList<>();
        this.mappings = new ArrayList<>();
        this.stem = false;
    }

    public OMOPTable(String name, OMOPDatabase database) {
        this.name = name;
        this.omopDatabase = database;
        this.fields = new ArrayList<>();
        this.mappings = new ArrayList<>();
        this.stem = false;
    }

    public OMOPTable(String name, String comment, OMOPDatabase omopDatabase) {
        this.name = name;
        this.comment = comment;
        this.omopDatabase = omopDatabase;
        this.fields = new ArrayList<>();
        this.mappings = new ArrayList<>();
        this.stem = false;
    }

    public OMOPTable(String name, boolean stem, OMOPDatabase omopDatabase) {
        this.name = name;
        this.stem = stem;
        this.omopDatabase = omopDatabase;
        this.fields = new ArrayList<>();
        this.mappings = new ArrayList<>();
    }

    public OMOPTable(String name, boolean stem, String comment, OMOPDatabase database) {
        this.name = name;
        this.stem = stem;
        this.comment = comment;
        this.omopDatabase = database;
        this.fields = new ArrayList<>();
        this.mappings = new ArrayList<>();
    }

    // GETTERS AND SETTERS
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

    public OMOPDatabase getTargetDatabase() {
        return omopDatabase;
    }

    public void setTargetDatabase(OMOPDatabase omopDatabase) {
        this.omopDatabase = omopDatabase;
    }

    public List<OMOPField> getFields() {
        return fields;
    }


    public void setFields(List<OMOPField> fields) {
        this.fields = fields;
    }


    public List<TableMapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<TableMapping> mappings) {
        this.mappings = mappings;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isStem() {
        return stem;
    }

    public void setStem(boolean stem) {
        this.stem = stem;
    }

    @Override
    public String toString() {
        return "OMOPTable{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", comment='" + comment + '\'' +
                ", fields=" + fields +
                '}';
    }
}
