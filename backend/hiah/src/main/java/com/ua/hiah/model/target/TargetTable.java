package com.ua.hiah.model.target;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.annotations.Expose;
import com.ua.hiah.model.FieldMapping;
import com.ua.hiah.views.Views;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TARGET_TABLE")
public class TargetTable {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView({Views.ETLSession.class, Views.TableMapping.class})
    private Long id;

    @Column(name = "name")
    @JsonView({Views.ETLSession.class, Views.TableMapping.class})
    @Expose
    private String name;

    @ManyToOne
    @JoinColumn(name = "target_database_id")
    @JsonIgnore
    private TargetDatabase targetDatabase;

    @Column(name = "comment", nullable = true, columnDefinition = "TEXT")
    @JsonView(Views.ETLSession.class)
    @Expose
    private String comment;

    @Column(name = "stem", nullable = false)
    @JsonView(Views.ETLSession.class)
    @Expose
    private boolean stem;

    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonView({Views.ETLSession.class, Views.TableMapping.class})
    @Expose
    private List<TargetField> fields;


    @OneToMany(mappedBy = "target", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(name = "mappings", nullable = true)
    @JsonIgnore
    private List<FieldMapping> mappings;


    // CONSTRUCTORS
    public TargetTable() {
        this.fields = new ArrayList<>();
        this.mappings = new ArrayList<>();
        this.stem = false;
    }

    public TargetTable(String name, TargetDatabase database) {
        this.name = name;
        this.targetDatabase = database;
        this.fields = new ArrayList<>();
        this.mappings = new ArrayList<>();
        this.stem = false;
    }

    public TargetTable(String name, String comment, TargetDatabase targetDatabase) {
        this.name = name;
        this.comment = comment;
        this.targetDatabase = targetDatabase;
        this.fields = new ArrayList<>();
        this.mappings = new ArrayList<>();
        this.stem = false;
    }

    public TargetTable(String name, boolean stem, TargetDatabase targetDatabase) {
        this.name = name;
        this.stem = stem;
        this.targetDatabase = targetDatabase;
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

    public boolean isStem() {
        return stem;
    }

    public void setStem(boolean stem) {
        this.stem = stem;
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
