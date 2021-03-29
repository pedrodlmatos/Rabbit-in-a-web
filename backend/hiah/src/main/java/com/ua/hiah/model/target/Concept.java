package com.ua.hiah.model.target;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
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
@Table(name = "CONCEPT")
public class Concept {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(Views.ETLSession.class)
    private Long id;

    @Column(name = "concept_id", nullable = false)
    @JsonView(Views.ETLSession.class)
    private Long conceptId;

    @Column(name = "concept_name", nullable = false)
    @JsonView(Views.ETLSession.class)
    private String conceptName;

    @Column(name = "standard_concept", nullable = false)
    @JsonView(Views.ETLSession.class)
    private String standardConcept;

    @Column(name = "domain_id")
    @JsonView(Views.ETLSession.class)
    private String domainId;

    @Column(name = "vocabulary_id")
    @JsonView(Views.ETLSession.class)
    private String vocabularyId;

    @Column(name = "concept_class_id")
    @JsonView(Views.ETLSession.class)
    private String conceptClassId;

    @ManyToOne
    @JoinColumn(name = "field_id", nullable = false)
    //@JsonView(Views.ETLSession.class)
    @JsonIgnore
    private TargetField field;

    public Concept() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConceptId() {
        return conceptId;
    }

    public void setConceptId(Long conceptId) {
        this.conceptId = conceptId;
    }

    public String getConceptName() {
        return conceptName;
    }

    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
    }

    public String getStandardConcept() {
        return standardConcept;
    }

    public void setStandardConcept(String standardConcept) {
        this.standardConcept = standardConcept;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public String getVocabularyId() {
        return vocabularyId;
    }

    public void setVocabularyId(String vocabularyId) {
        this.vocabularyId = vocabularyId;
    }

    public String getConceptClassId() {
        return conceptClassId;
    }

    public void setConceptClassId(String conceptClassId) {
        this.conceptClassId = conceptClassId;
    }

    public TargetField getField() {
        return field;
    }

    public void setField(TargetField field) {
        this.field = field;
    }
}
