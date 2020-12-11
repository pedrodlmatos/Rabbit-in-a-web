package com.ua.riah.mapping;

public class Concept {
    private String conceptId;
    private String conceptName;
    private String standardConcept;
    private String domainId;
    private String vocabularyId;
    private String conceptClassId;

    public String getConceptId() {
        return conceptId;
    }

    void setConceptId(String conceptId) {
        this.conceptId = conceptId;
    }

    public String getConceptName() {
        return conceptName;
    }

    void setConceptName(String conceptName) {
        this.conceptName = conceptName;
    }

    public String getStandardConcept() {
        return standardConcept;
    }

    void setStandardConcept(String standardConcept) {
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

    public String toString() {
        return this.conceptId + " -- " + this.conceptName;
    }
}