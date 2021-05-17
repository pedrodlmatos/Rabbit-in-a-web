package com.ua.hiah.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.annotations.Expose;
import com.ua.hiah.model.auth.User;
import com.ua.hiah.model.source.SourceDatabase;
import com.ua.hiah.model.target.TargetDatabase;
import com.ua.hiah.views.Views;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ETL")
public class ETL {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView({Views.ETLSessionsList.class, Views.AdminETLProcedureList.class})
    private Long id;

    @Column(name = "name", nullable = false)
    @JsonView({Views.ETLSessionsList.class, Views.AdminETLProcedureList.class})
    @Expose
    private String name;

    @ManyToMany
    @JoinTable(
            name = "etl_users",
            joinColumns = @JoinColumn(name = "etl_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonView(Views.AdminETLProcedureList.class)
    private List<User> users;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sourceDatabase_id", referencedColumnName = "id")
    @JsonView({Views.ETLSessionsList.class, Views.AdminETLProcedureList.class})
    @Expose
    private SourceDatabase sourceDatabase;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "targetDatabase_id", referencedColumnName = "id")
    @JsonView({Views.ETLSessionsList.class, Views.AdminETLProcedureList.class})
    @Expose
    private TargetDatabase targetDatabase;

    @OneToMany(mappedBy = "etl", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonView({Views.ETLSessionsList.class, Views.AdminETLProcedureList.class})
    @Expose
    private List<TableMapping> tableMappings;

    // CONSTRUCTORS
    public ETL() {
        this.users = new ArrayList<>();
        this.tableMappings = new ArrayList<>();
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

    public SourceDatabase getSourceDatabase() {
        return sourceDatabase;
    }

    public void setSourceDatabase(SourceDatabase sourceDatabase) {
        this.sourceDatabase = sourceDatabase;
    }

    public TargetDatabase getTargetDatabase() {
        return targetDatabase;
    }

    public void setTargetDatabase(TargetDatabase targetDatabase) {
        this.targetDatabase = targetDatabase;
    }

    public List<TableMapping> getTableMappings() {
        return tableMappings;
    }

    public void setTableMappings(List<TableMapping> tableMappings) {
        this.tableMappings = tableMappings;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return String.format("ETL{" +
                "\tid=%s,\n" +
                "\tname=%s,\n" +
                "\tsourceDatabase=%s,\n" +
                "\ttargetDatabase=%s,\n" +
                "\ttableMappings=%s\n" +
                "}", id, name, sourceDatabase, targetDatabase, tableMappings);
    }
}
