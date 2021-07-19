package com.ua.riaw.model.omop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.annotations.Expose;
import com.ua.riaw.views.Views;

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
    @JsonView({
            Views.ETLProcedure.class,
            Views.CreateMapping.class,
            Views.TableMapping.class
    })
    private Long id;

    @Column(name = "concept_id", nullable = false)
    @JsonView({
            Views.ETLProcedure.class,
            Views.CreateMapping.class,
            Views.TableMapping.class
    })
    @Expose
    private Long conceptId;

    @Column(name = "concept_name", nullable = false)
    @JsonView({
            Views.ETLProcedure.class,
            Views.CreateMapping.class,
            Views.TableMapping.class
    })
    @Expose
    private String conceptName;

    @Column(name = "standard_concept", nullable = false)
    @JsonView({
            Views.ETLProcedure.class,
            Views.CreateMapping.class,
            Views.TableMapping.class
    })
    @Expose
    private String standardConcept;

    @Column(name = "domain_id")
    @JsonView({
            Views.ETLProcedure.class,
            Views.CreateMapping.class,
            Views.TableMapping.class
    })
    @Expose
    private String domainId;

    @Column(name = "vocabulary_id")
    @JsonView({
            Views.ETLProcedure.class,
            Views.CreateMapping.class,
            Views.TableMapping.class
    })
    @Expose
    private String vocabularyId;

    @Column(name = "concept_class_id")
    @JsonView({
            Views.ETLProcedure.class,
            Views.CreateMapping.class,
            Views.TableMapping.class
    })
    @Expose
    private String conceptClassId;

    @ManyToOne
    @JoinColumn(name = "omop_field_id", nullable = false)
    @JsonIgnore
    private OMOPField omopField;

    // CONSTRUCTORS
    public Concept() { }

    public Concept(Long conceptId, String conceptName, String standardConcept, String domainId, String vocabularyId, String conceptClassId, OMOPField omopField) {
        this.conceptId = conceptId;
        this.conceptName = conceptName;
        this.standardConcept = standardConcept;
        this.domainId = domainId;
        this.vocabularyId = vocabularyId;
        this.conceptClassId = conceptClassId;
        this.omopField = omopField;
    }

    // GETTERS AND SETTERS
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

    public OMOPField getOmopField() {
        return omopField;
    }

    public void setOmopField(OMOPField field) {
        this.omopField = field;
    }
}
