package com.ua.riaw.etlProcedure.source.ehrField;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.annotations.Expose;
import com.ua.riaw.etlProcedure.source.ehrTable.EHRTable;
import com.ua.riaw.etlProcedure.fieldMapping.FieldMapping;
import com.ua.riaw.etlProcedure.source.valueCounts.ValueCount;
import com.ua.riaw.etlProcedure.target.omopField.OMOPField;
import com.ua.riaw.utils.views.Views;

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
@Table(name = "EHR_FIELD")
public class EHRField {

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
    @JsonView({
            Views.ETLProcedure.class,
            Views.TableMapping.class,
            Views.CreateMapping.class
    })
    @Expose
    private boolean isNullable;

    @Column(name = "max_length", nullable = true)
    @Expose
    private int maxLength;

    @Column(name = "fraction_empty", nullable = true)
    @Expose
    private double fractionEmpty;

    @Column(name = "unique_count", nullable = true)
    @Expose
    private int uniqueCount;

    @Column(name = "fraction_unique", nullable = true)
    @Expose
    private double fractionUnique;

    @Column(name = "comment", nullable = true, columnDefinition = "TEXT")
    @JsonView({
            Views.TableMapping.class,
            Views.ChangeComment.class,
            Views.ETLProcedure.class,
            Views.CreateMapping.class
    })
    @Expose
    private String comment;

    @ManyToOne
    @JoinColumn(name = "ehr_table_id")
    @JsonIgnore
    private EHRTable ehrTable;

    @OneToMany(mappedBy = "ehrField", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<FieldMapping> mappings;

    @OneToMany(mappedBy = "ehrField", cascade = CascadeType.ALL)
    @JsonView({Views.ETLProcedure.class, Views.TableMapping.class})
    @Expose
    private List<ValueCount> valueCounts;


    // CONSTRUCTORS
    public EHRField() {
    }

    public EHRField(String name, String type, int maxLength, double fractionEmpty, int uniqueCount, double fractionUnique, EHRTable ehrTable) {
        this.name = name;
        this.type = type;
        this.maxLength = maxLength;
        this.fractionEmpty = fractionEmpty;
        this.uniqueCount = uniqueCount;
        this.fractionUnique = fractionUnique;
        this.ehrTable = ehrTable;
        this.stem = false;
        this.mappings = new ArrayList<>();
        this.valueCounts = new ArrayList<>();
    }

    public EHRField(String name, String type, int maxLength, double fractionEmpty, int uniqueCount, double fractionUnique, String comment, EHRTable ehrTable) {
        this.name = name;
        this.type = type;
        this.maxLength = maxLength;
        this.fractionEmpty = fractionEmpty;
        this.uniqueCount = uniqueCount;
        this.fractionUnique = fractionUnique;
        this.comment = comment;
        this.ehrTable = ehrTable;
        this.stem = false;
        this.mappings = new ArrayList<>();
        this.valueCounts = new ArrayList<>();
    }

    public EHRField(String name, boolean isNullable, String type, String description, boolean stem, EHRTable ehrTable) {
        this.name = name;
        this.isNullable = isNullable;
        this.type = type;
        //this.description = description;
        this.stem = stem;
        this.ehrTable = ehrTable;
        this.mappings = new ArrayList<>();
        this.valueCounts = new ArrayList<>();
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

    /*
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    */

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

    public EHRTable getEHRTable() {
        return ehrTable;
    }

    public void setEHRTable(EHRTable table) {
        this.ehrTable = table;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public double getFractionEmpty() {
        return fractionEmpty;
    }

    public void setFractionEmpty(double fractionEmpty) {
        this.fractionEmpty = fractionEmpty;
    }

    public int getUniqueCount() {
        return uniqueCount;
    }

    public void setUniqueCount(int uniqueCount) {
        this.uniqueCount = uniqueCount;
    }

    public double getFractionUnique() {
        return fractionUnique;
    }

    public void setFractionUnique(double fractionUnique) {
        this.fractionUnique = fractionUnique;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<FieldMapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<FieldMapping> mappings) {
        this.mappings = mappings;
    }

    public List<ValueCount> getValueCounts() {
        return valueCounts;
    }

    public void setValueCounts(List<ValueCount> valueCounts) {
        this.valueCounts = valueCounts;
    }

    /* Adapted from ETL (Rabbit in a hat) */
    public List<String> getMappingsFromEHRField() {
        List<String> result = new ArrayList<>();

        for (FieldMapping mapping : this.getMappings()) {
            OMOPField omopField = mapping.getOmopField();
            result.add(String.format("%s.%s", omopField.getOmopTable().getName(), omopField.getName()));
        }

        return result;
    }

    @Override
    public String toString() {
        return "EHRField{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", isNullable=" + isNullable +
                ", maxLength=" + maxLength +
                ", fractionEmpty=" + fractionEmpty +
                ", uniqueCount=" + uniqueCount +
                ", fractionUnique=" + fractionUnique +
                ", comment='" + comment + '\'' +
                '}';
    }
}
