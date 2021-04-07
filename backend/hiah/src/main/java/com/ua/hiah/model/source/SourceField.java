package com.ua.hiah.model.source;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.ua.hiah.model.FieldMapping;
import com.ua.hiah.model.target.TargetField;
import com.ua.hiah.views.Views;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "SOURCE_FIELD")
public class SourceField {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(Views.ETLSession.class)
    private Long id;

    @Column(name = "name")
    @JsonView(Views.ETLSession.class)
    private String name;

    /*
    @Column(name = "description", columnDefinition = "TEXT")
    @JsonView(Views.ETLSession.class)
    private String description;
    */

    @Column(name = "type")
    @JsonView(Views.ETLSession.class)
    private String type;

    @Column(name = "nullable")
    private boolean isNullable;

    @Column(name = "maxLength", nullable = true)
    private int maxLength;

    @Column(name = "fractionEmpty", nullable = true)
    private double fractionEmpty;

    @Column(name = "uniqueCount", nullable = true)
    private int uniqueCount;

    @Column(name = "fractionUnique", nullable = true)
    private double fractionUnique;

    @Column(name = "comment", nullable = true, columnDefinition = "TEXT")
    @JsonView(Views.ETLSession.class)
    private String comment;

    @ManyToOne
    @JoinColumn(name = "source_table_id")
    @JsonIgnore
    private SourceTable table;

    @OneToMany(mappedBy = "source")
    @Column(name = "mappings", nullable = true)
    @JsonIgnore
    private List<FieldMapping> mappings;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL)
    @Column(name = "valueCount")
    @JsonView(Views.ETLSession.class)
    private List<ValueCount> valueCounts;


    // CONSTRUCTOR
    public SourceField() {
    }

    public SourceField(String name, String type, int maxLength, double fractionEmpty, int uniqueCount, double fractionUnique, SourceTable table) {
        this.name = name;
        this.type = type;
        this.maxLength = maxLength;
        this.fractionEmpty = fractionEmpty;
        this.uniqueCount = uniqueCount;
        this.fractionUnique = fractionUnique;
        this.table = table;
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

    public SourceTable getTable() {
        return table;
    }

    public void setTable(SourceTable table) {
        this.table = table;
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
    public List<String> getMappingsFromSourceField() {
        List<String> result = new ArrayList<>();

        for (FieldMapping mapping : this.getMappings()) {
            TargetField targetField = mapping.getTarget();
            result.add(String.format("%s.%s", targetField.getTable().getName(), targetField.getName()));
        }

        return result;
    }

    @Override
    public String toString() {
        return "SourceField{" +
                "id=" + id +
                ", name='" + name + '\'' +
                //", description='" + description + '\'' +
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
