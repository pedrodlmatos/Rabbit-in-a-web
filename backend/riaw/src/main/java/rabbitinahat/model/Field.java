/* Adapted from Field (rabbit-core) */
package rabbitinahat.model;

import com.ua.riaw.model.ehr.EHRField;
import com.ua.riaw.model.ehr.ValueCount;
import com.ua.riaw.model.omop.Concept;
import com.ua.riaw.model.omop.OMOPField;
import rabbitcore.riah_datamodel.ConceptsMap;

import java.util.ArrayList;
import java.util.List;

public class Field implements MappableItem {

    // general attributes
    private Table table;
    private String name;
    private String comment;
    private String type;
    private boolean isNullable;

    // attributes of OMOP CDM field
    private List<ConceptsMap.Concept> concepts;

    // attributes of EHR field
    private int maxLength;
    private ValueCounts valueCounts = new ValueCounts();
    private double fractionEmpty;
    private int uniqueCount;
    private double fractionUnique;

    public Field() {}

    public Field(EHRField ehrField, Table table) {
        this.table = table;
        this.name = ehrField.getName();
        this.comment = ehrField.getComment();
        this.isNullable = ehrField.isNullable();
        this.type = ehrField.getType();
        this.maxLength = ehrField.getMaxLength();
        this.fractionEmpty = ehrField.getFractionEmpty();
        this.uniqueCount = ehrField.getUniqueCount();
        this.fractionUnique = ehrField.getFractionUnique();
        getValueCountsFromField(ehrField);
    }

    public Field(OMOPField omopField, Table table) {
        this.table = table;
        this.name = omopField.getName();
        this.comment = omopField.getComment();
        this.type = omopField.getType();
        this.isNullable = omopField.isNullable();
        this.concepts = getConceptsFromTargetField(omopField);
    }


    private void getValueCountsFromField(EHRField ehrField) {
        for (ValueCount valueCount : ehrField.getValueCounts()) {
            valueCounts.add(valueCount.getValue(), valueCount.getFrequency());
        }
    }

    private List<ConceptsMap.Concept> getConceptsFromTargetField(OMOPField omopField) {
        List<ConceptsMap.Concept> concepts = new ArrayList<>();
        for (Concept concept : omopField.getConcepts()) {
            ConceptsMap.Concept concept_riah = new ConceptsMap.Concept(concept, this);
            concepts.add(concept_riah);
        }
        return concepts;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ValueCounts getValueCounts() {
        return valueCounts;
    }

    public void setValueCounts(ValueCounts valueCounts) {
        this.valueCounts = valueCounts;
    }

    @Override
    public String outputName() {
        if (!isNullable)
            return "*" + name;
        else
            return name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Database getDb() {
        return null;
    }

    @Override
    public boolean isStem() {
        return false;
    }

    @Override
    public void setStem(boolean isStem) {

    }

    @Override
    public String toString() {
        return "Field{" +
                "name='" + name + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
