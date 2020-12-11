package com.ua.riah.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ETL")
public class ETL {

    @Id
    @Column(name = "id", nullable = false)
    @JsonView(SummaryViews.SessionSummary.class)
    private String id;

    @OneToOne(mappedBy = "etl")
    @JsonIgnore
    private Session session;

    @ManyToOne
    @JoinColumn(name = "sourceDB_id", nullable = true)
    @JsonView(SummaryViews.SessionSummary.class)
    private Database sourceDB;

    @ManyToOne
    @JoinColumn(name = "targetDB_id", nullable = false)
    @JsonView(SummaryViews.SessionSummary.class)
    private Database targetDB;

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Database getSourceDB() {
        return sourceDB;
    }

    public void setSourceDB(Database sourceDB) {
        this.sourceDB = sourceDB;
    }

    public Database getTargetDB() {
        return targetDB;
    }

    public void setTargetDB(Database targetDB) {
        this.targetDB = targetDB;
    }
}
