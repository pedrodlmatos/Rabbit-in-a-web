package com.ua.riah.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ua.riah.model.source.SourceField;
import com.ua.riah.model.target.TargetField;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "FIELD_MAPPING")
public class FieldMapping {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "source_id", nullable = false)
    private SourceField source;

    @ManyToOne
    @JoinColumn(name = "target_id", nullable = false)
    private TargetField target;

    @ManyToOne
    @JoinColumn(name = "table_mapping_id", nullable = false)
    @JsonIgnore
    private TableMapping tableMapping;

    // CONSTRUCTOR
    public FieldMapping() {
    }

    // GETTERS AND SETTERS
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SourceField getSource() {
        return source;
    }

    public void setSource(SourceField source) {
        this.source = source;
    }

    public TargetField getTarget() {
        return target;
    }

    public void setTarget(TargetField target) {
        this.target = target;
    }

    public TableMapping getTableMapping() {
        return tableMapping;
    }

    public void setTableMapping(TableMapping tableMapping) {
        this.tableMapping = tableMapping;
    }
}