package com.ua.hiah.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.annotations.Expose;
import com.ua.hiah.model.source.SourceTable;
import com.ua.hiah.model.target.TargetTable;
import com.ua.hiah.views.Views;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TABLE_MAPPING")
public class TableMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonView({Views.ETLSession.class, Views.TableMapping.class})
    private Long id;

    @ManyToOne
    @JoinColumn(name = "source_table_id", nullable = false)
    @JsonView({Views.ETLSession.class, Views.TableMapping.class})
    @Expose
    private SourceTable source;

    @ManyToOne
    @JoinColumn(name = "target_table_id", nullable = false)
    @JsonView({Views.ETLSession.class, Views.TableMapping.class})
    @Expose
    private TargetTable target;

    @OneToMany(mappedBy = "tableMapping", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonView(Views.TableMapping.class)
    @Expose
    private List<FieldMapping> fieldMappings;

    @JsonView({Views.ETLSession.class, Views.TableMapping.class})
    @Column(name = "complete", nullable = false)
    @Expose
    private boolean complete;

    @Column(name = "logic", nullable = true, columnDefinition = "TEXT")
    @JsonView({Views.ETLSession.class, Views.TableMapping.class})
    @Expose
    private String logic;

    @ManyToOne
    @JoinColumn(name = "etl_id", nullable = false)
    @JsonIgnore
    private ETL etl;

    // CONSTRUCTOR
    public TableMapping() {
    }

    public TableMapping(ETL etl, SourceTable sourceTable, TargetTable targetTable, String logic) {
        this.etl = etl;
        this.source = sourceTable;
        this.target = targetTable;
        this.logic = logic;
        this.fieldMappings = new ArrayList<>();
    }

    // GETTERS AND SETTERS
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SourceTable getSource() {
        return source;
    }

    public void setSource(SourceTable source) {
        this.source = source;
    }

    public TargetTable getTarget() {
        return target;
    }

    public void setTarget(TargetTable target) {
        this.target = target;
    }

    public ETL getEtl() {
        return etl;
    }

    public void setEtl(ETL etl) {
        this.etl = etl;
    }

    public List<FieldMapping> getFieldMappings() {
        return fieldMappings;
    }

    /*
    public void setFieldMappings(List<FieldMapping> fieldMappings) {
        this.fieldMappings = fieldMappings;
    }
    */

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public String getLogic() {
        return logic;
    }

    public void setLogic(String logic) {
        this.logic = logic;
    }

    @Override
    public String toString() {
        return "TableMapping{" +
                "id=" + id +
                ", source=" + source +
                ", target=" + target +
                '}';
    }
}
