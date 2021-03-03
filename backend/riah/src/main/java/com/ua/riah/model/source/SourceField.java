package com.ua.riah.model.source;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.ua.riah.model.FieldMapping;
import com.ua.riah.views.Views;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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

    @Column(name = "description", columnDefinition = "TEXT")
    @JsonView(Views.ETLSession.class)
    private String description;

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

    @ManyToOne
    @JoinColumn(name = "source_table_id")
    @JsonIgnore
    private SourceTable table;

    @OneToMany(mappedBy = "source")
    @Column(name = "mappings", nullable = true)
    @JsonIgnore
    private List<FieldMapping> mappings;


    // CONSTRUCTOR
    public SourceField() {
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

    public List<FieldMapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<FieldMapping> mappings) {
        this.mappings = mappings;
    }
}
