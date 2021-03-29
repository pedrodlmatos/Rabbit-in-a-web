package com.ua.hiah.model.target;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.ua.hiah.model.FieldMapping;
import com.ua.hiah.model.source.SourceField;
import com.ua.hiah.views.Views;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TARGET_FIELD")
public class TargetField {

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

    @Column(name = "comment", nullable = true, columnDefinition = "TEXT")
    @JsonView(Views.ETLSession.class)
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_table_id")
    @JsonIgnore
    private TargetTable table;

    @OneToMany(mappedBy = "field")
    @Column(name = "concepts")
    private List<Concept> concepts;

    @OneToMany(mappedBy = "target")
    @Column(name = "mappings", nullable = true)
    private List<FieldMapping> mappings;


    // CONSTRUCTOR
    public TargetField() {
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

    public TargetTable getTable() {
        return table;
    }

    public void setTable(TargetTable table) {
        this.table = table;
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

    /* Adapted from ETL (Rabbit in a hat) */
    public List<String> getMappingsToTargetField() {
        List<String> result = new ArrayList<>();

        for (FieldMapping mapping : this.getMappings()) {
            SourceField sourceField = mapping.getSource();
            result.add(String.format("%s.%s", sourceField.getTable().getName(), sourceField.getName()));
        }

        return result;
    }


    @Override
    public String toString() {
        return "TargetField{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", isNullable=" + isNullable +
                '}';
    }
}
