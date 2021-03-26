package com.ua.hiah.model.target;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.ua.hiah.model.FieldMapping;
import com.ua.hiah.views.Views;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "TARGET_TABLE")
public class TargetTable {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.ETLSession.class)
    private Long id;

    @Column(name = "name")
    @JsonView(Views.ETLSession.class)
    private String name;

    @ManyToOne
    @JoinColumn(name = "target_database_id")
    @JsonIgnore
    private TargetDatabase targetDatabase;

    @Column(name = "comment", nullable = true, columnDefinition = "TEXT")
    @JsonView(Views.ETLSession.class)
    private String comment;

    @OneToMany(mappedBy = "table", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JsonView(Views.ETLSession.class)
    private List<TargetField> fields;


    @OneToMany(mappedBy = "target", cascade = CascadeType.ALL)
    @Column(name = "mappings", nullable = true)
    @JsonIgnore
    private List<FieldMapping> mappings;


    // CONSTRUCTOR
    public TargetTable() {
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

    public TargetDatabase getTargetDatabase() {
        return targetDatabase;
    }

    public void setTargetDatabase(TargetDatabase targetDatabase) {
        this.targetDatabase = targetDatabase;
    }

    public List<TargetField> getFields() {
        return fields;
    }

    public void setFields(List<TargetField> fields) {
        this.fields = fields;
    }


    public List<FieldMapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<FieldMapping> mappings) {
        this.mappings = mappings;
    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "TargetTable{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", comment='" + comment + '\'' +
                ", fields=" + fields +
                '}';
    }
}
