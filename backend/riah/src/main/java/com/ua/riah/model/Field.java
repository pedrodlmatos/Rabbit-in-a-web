package com.ua.riah.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "FIELD")
public class Field {

    @Id
    @Column(name = "id", unique = true)
    private String id;

    @ManyToOne
    @JoinColumn(name = "table_id")
    @JsonIgnore
    private DBTable table;

    @Column(name = "name")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "type")
    private String type;

    @Column(name = "maxLength", nullable = true)
    private int maxLength;

    @Column(name = "fractionEmpty", nullable = true)
    private double fractionEmpty;

    @Column(name = "uniqueCount", nullable = true)
    private int uniqueCount;

    @Column(name = "fractionUnique", nullable = true)
    private double fractionUnique;

    @Column(name = "nullable")
    private boolean isNullable;


    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DBTable getTable() {
        return table;
    }

    public void setTable(DBTable table) {
        this.table = table;
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

    public boolean isNullable() {
        return isNullable;
    }

    public void setNullable(boolean nullable) {
        isNullable = nullable;
    }
}
