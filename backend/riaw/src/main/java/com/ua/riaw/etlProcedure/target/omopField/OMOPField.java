package com.ua.riaw.etlProcedure.target.omopField;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.annotations.Expose;
import com.ua.riaw.etlProcedure.target.omopTable.OMOPTable;
import com.ua.riaw.etlProcedure.fieldMapping.FieldMapping;
import com.ua.riaw.etlProcedure.source.ehrField.EHRField;
import com.ua.riaw.etlProcedure.target.concept.Concept;
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
@Table(name = "OMOP_FIELD")
public class OMOPField {

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

    @Column(name = "description", columnDefinition = "TEXT")
    @JsonView({
            Views.ETLProcedure.class,
            Views.TableMapping.class,
            Views.CreateMapping.class
    })
    @Expose
    private String description;

    @Column(name = "type")
    @JsonView({
            Views.ETLProcedure.class,
            Views.TableMapping.class,
            Views.CreateMapping.class
    })
    @Expose
    private String type;

    @Column(name = "stem", nullable = false)
    @JsonView({
            Views.ETLProcedure.class,
            Views.TableMapping.class,
            Views.CreateMapping.class
    })
    @Expose
    private boolean stem;

    @Column(name = "nullable")
    @Expose
    private boolean isNullable;

    @Column(name = "comment", nullable = true, columnDefinition = "TEXT")
    @JsonView({
            Views.TableMapping.class,
            Views.ChangeComment.class,
            Views.ETLProcedure.class,
            Views.CreateMapping.class
    })
    @Expose
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "omop_table_id")
    @JsonIgnore
    private OMOPTable omopTable;

    @OneToMany(mappedBy = "omopField", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<FieldMapping> mappings;

    @OneToMany(mappedBy = "omopField", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonView({
            Views.TableMapping.class,
            Views.ETLProcedure.class,
            Views.CreateMapping.class
    })
    @Expose
    private List<Concept> concepts;


    // CONSTRUCTORS
    public OMOPField() {
    }

    public OMOPField(String name, boolean isNullable, String type, String description, OMOPTable omopTable) {
        this.name = name;
        this.isNullable = isNullable;
        this.type = type;
        this.description = description;
        this.omopTable = omopTable;
        this.mappings = new ArrayList<>();
        this.concepts = new ArrayList<>();
        this.stem = false;
    }

    public OMOPField(String name, boolean nullable, String type, String description, String comment, OMOPTable omopTable) {
        this.name = name;
        this.isNullable = nullable;
        this.type = type;
        this.description = description;
        this.comment = comment;
        this.omopTable = omopTable;
        this.mappings = new ArrayList<>();
        this.concepts = new ArrayList<>();
        this.stem = false;
    }

    public OMOPField(String name, boolean isNullable, String type, String description, boolean stem, OMOPTable omopTable) {
        this.name = name;
        this.isNullable = isNullable;
        this.type = type;
        this.description = description;
        this.stem = stem;
        this.omopTable = omopTable;
        this.mappings = new ArrayList<>();
        this.concepts = new ArrayList<>();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isNullable() {
        return isNullable;
    }

    public void setNullable(boolean nullable) {
        isNullable = nullable;
    }

    public OMOPTable getOmopTable() {
        return omopTable;
    }

    public void setOmopTable(OMOPTable table) {
        this.omopTable = table;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Concept> getConcepts() {
        return concepts;
    }

    public void setConcepts(List<Concept> concepts) {
        this.concepts = concepts;
    }

    public List<FieldMapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<FieldMapping> mappings) {
        this.mappings = mappings;
    }

    public boolean isStem() {
        return stem;
    }

    public void setStem(boolean stem) {
        this.stem = stem;
    }

    /* Adapted from ETL (Rabbit in a hat) */
    public List<String> getMappingsToTargetField() {
        List<String> result = new ArrayList<>();

        for (FieldMapping mapping : this.getMappings()) {
            EHRField ehrField = mapping.getEhrField();
            result.add(String.format("%s.%s", ehrField.getEHRTable().getName(), ehrField.getName()));
        }

        return result;
    }


    @Override
    public String toString() {
        return "OMOPField{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", isNullable=" + isNullable +
                '}';
    }
}
