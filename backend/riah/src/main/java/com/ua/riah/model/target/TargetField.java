package com.ua.riah.model.target;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.ua.riah.model.FieldMapping;
import com.ua.riah.views.Views;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_table_id")
    @JsonIgnore
    private TargetTable table;

    @OneToMany(mappedBy = "target")
    @Column(name = "mappings", nullable = true)
    @JsonIgnore
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

    public List<FieldMapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<FieldMapping> mappings) {
        this.mappings = mappings;
    }
}
