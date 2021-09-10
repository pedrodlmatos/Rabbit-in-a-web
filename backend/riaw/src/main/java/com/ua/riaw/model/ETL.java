package com.ua.riaw.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.annotations.Expose;
import com.ua.riaw.model.auth.User;
import com.ua.riaw.model.ehr.EHRDatabase;
import com.ua.riaw.model.omop.OMOPDatabase;
import com.ua.riaw.views.Views;

import javax.persistence.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "ETL")
public class ETL {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView({
            Views.AdminETLProcedureList.class,
            Views.UserETLProcedureList.class,
            Views.RecentETLProcedureList.class,
            Views.CreateETLProcedure.class,
            Views.ETLProcedure.class
    })
    private Long id;

    @Column(name = "name", nullable = false)
    @JsonView({
            Views.AdminETLProcedureList.class,
            Views.UserETLProcedureList.class,
            Views.RecentETLProcedureList.class,
            Views.ETLProcedure.class
    })
    @Expose
    private String name;

    @ManyToMany
    @JoinTable(
            name = "etl_users",
            joinColumns = @JoinColumn(name = "etl_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonView({
            Views.AdminETLProcedureList.class,
            Views.ETLProcedure.class,
            Views.ETLUsers.class
    })
    private Set<User> users;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ehr_database_id", referencedColumnName = "id")
    @JsonView({
            Views.AdminETLProcedureList.class,
            Views.UserETLProcedureList.class,
            Views.RecentETLProcedureList.class,
            Views.ETLProcedure.class
    })
    @Expose
    private EHRDatabase ehrDatabase;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "omop_database_id", referencedColumnName = "id")
    @JsonView({
            Views.AdminETLProcedureList.class,
            Views.UserETLProcedureList.class,
            Views.RecentETLProcedureList.class,
            Views.ETLProcedure.class
    })
    @Expose
    private OMOPDatabase omopDatabase;

    @OneToMany(mappedBy = "etl", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonView(Views.ETLProcedure.class)
    @Expose
    private List<TableMapping> tableMappings;

    @Column(name = "deleted", nullable = false)
    @JsonView(Views.AdminETLProcedureList.class)
    private boolean deleted;

    @Column(name = "creation_date", nullable = false)
    @JsonView({Views.AdminETLProcedureList.class, Views.UserETLProcedureList.class})
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd-MM-yyyy HH:mm", timezone = "UTC")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @Column(name = "modification_date", nullable = false)
    @JsonView({Views.AdminETLProcedureList.class, Views.UserETLProcedureList.class})
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd-MM-yyyy HH:mm", timezone = "UTC")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modificationDate;


    // CONSTRUCTORS
    public ETL() {
        this.users = new HashSet<>();
        this.tableMappings = new ArrayList<>();
        this.deleted = false;
        this.creationDate = Date.from(Instant.now());
        this.modificationDate = Date.from(Instant.now());
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

    public EHRDatabase getSourceDatabase() {
        return ehrDatabase;
    }

    public void setSourceDatabase(EHRDatabase ehrDatabase) {
        this.ehrDatabase = ehrDatabase;
    }

    public OMOPDatabase getTargetDatabase() {
        return omopDatabase;
    }

    public void setTargetDatabase(OMOPDatabase omopDatabase) {
        this.omopDatabase = omopDatabase;
    }

    public List<TableMapping> getTableMappings() {
        return tableMappings;
    }

    public void setTableMappings(List<TableMapping> tableMappings) {
        this.tableMappings = tableMappings;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    @Override
    public String toString() {
        return "ETL{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", users=" + users +
                ", ehrDatabase=" + ehrDatabase +
                ", omopDatabase=" + omopDatabase +
                ", tableMappings=" + tableMappings +
                ", deleted=" + deleted +
                ", creationDate=" + creationDate +
                ", modificationDate=" + modificationDate +
                '}';
    }
}
