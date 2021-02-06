package com.ua.riah.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ua.riah.model.source.SourceTable;
import com.ua.riah.model.target.TargetTable;

import javax.persistence.CascadeType;
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
@Table(name = "TABLE_MAPPING")
public class TableMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "source_table_id", nullable = false)
    private SourceTable source;

    @ManyToOne
    @JoinColumn(name = "target_table_id", nullable = false)
    private TargetTable target;

    @OneToMany(mappedBy = "tableMapping", cascade = CascadeType.ALL)
    private List<FieldMapping> fieldMappings;

    @ManyToOne
    @JoinColumn(name = "etl_id", nullable = false)
    @JsonIgnore
    private ETL etl;

    // CONSTRUCTOR
    public TableMapping() {
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

    public void setFieldMappings(List<FieldMapping> fieldMappings) {
        this.fieldMappings = fieldMappings;
    }

    @Override
    public String toString() {
        return "TableMapping{" +
                "id=" + id +
                ", source=" + source +
                ", target=" + target +
                ", etl=" + etl +
                '}';
    }
}