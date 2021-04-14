package com.ua.hiah.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.annotations.Expose;
import com.ua.hiah.model.source.SourceField;
import com.ua.hiah.model.target.TargetField;
import com.ua.hiah.views.Views;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.TableMapping.class)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "source_id", nullable = false)
    @JsonView(Views.TableMapping.class)
    @Expose
    private SourceField source;

    @ManyToOne
    @JoinColumn(name = "target_id", nullable = false)
    @JsonView(Views.TableMapping.class)
    @Expose
    private TargetField target;

    @Column(name = "logic", nullable = true, columnDefinition = "TEXT")
    @JsonView(Views.TableMapping.class)
    @Expose
    private String logic;

    @ManyToOne
    @JoinColumn(name = "table_mapping_id", nullable = false)
    @JsonIgnore
    private TableMapping tableMapping;

    // CONSTRUCTORS
    public FieldMapping() {
    }

    public FieldMapping(SourceField sourceField, TargetField targetField, TableMapping tableMapping) {
        this.source = sourceField;
        this.target = targetField;
        this.tableMapping = tableMapping;
    }

    public FieldMapping(SourceField source, TargetField target, String logic, TableMapping tableMapping) {
        this.source = source;
        this.target = target;
        this.logic = logic;
        this.tableMapping = tableMapping;
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

    public String getLogic() {
        return logic;
    }

    public void setLogic(String logic) {
        this.logic = logic;
    }
}
