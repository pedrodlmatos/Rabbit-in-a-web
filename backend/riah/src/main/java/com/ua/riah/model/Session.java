package com.ua.riah.model;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;


@Entity
@Table(name = "SESSIONS")
public class Session {

    @Id
    @Column(name = "id")
    @JsonView(SummaryViews.SessionSummary.class)
    private String id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ETL_id", referencedColumnName = "id")
    @JsonView(SummaryViews.SessionSummary.class)
    private ETL etl;


    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ETL getEtl() {
        return etl;
    }

    public void setEtl(ETL etl) {
        this.etl = etl;
    }
}
