package com.ua.hiah.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.ua.hiah.model.source.SourceField;
import com.ua.hiah.model.target.TargetField;
import com.ua.hiah.views.Views;

import javax.persistence.*;

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
    private SourceField source;

    @ManyToOne
    @JoinColumn(name = "target_id", nullable = false)
    @JsonView(Views.TableMapping.class)
    private TargetField target;

    @Column(name = "logic", nullable = true, columnDefinition = "TEXT")
    @JsonView(Views.TableMapping.class)
    private String logic;

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

    public String getLogic() {
        return logic;
    }

    public void setLogic(String logic) {
        this.logic = logic;
    }
}
