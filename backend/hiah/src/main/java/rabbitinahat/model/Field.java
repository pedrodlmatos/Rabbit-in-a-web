/* Adapted from Field (rabbit-core) */
package rabbitinahat.model;

import com.ua.hiah.model.source.SourceField;
import com.ua.hiah.model.source.ValueCount;
import com.ua.hiah.model.target.Concept;
import com.ua.hiah.model.target.TargetField;
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

    public Field(SourceField sourceField, Table table) {
        this.table = table;
        this.name = sourceField.getName();
        this.comment = sourceField.getComment();
        this.isNullable = sourceField.isNullable();
        this.type = sourceField.getType();
        this.maxLength = sourceField.getMaxLength();
        this.fractionEmpty = sourceField.getFractionEmpty();
        this.uniqueCount = sourceField.getUniqueCount();
        this.fractionUnique = sourceField.getFractionUnique();
        getValueCountsFromField(sourceField);
    }

    public Field(TargetField targetField, Table table) {
        this.table = table;
        this.name = targetField.getName();
        this.comment = targetField.getComment();
        this.type = targetField.getType();
        this.isNullable = targetField.isNullable();
        this.concepts = getConceptsFromTargetField(targetField);
    }


    private void getValueCountsFromField(SourceField sourceField) {
        for (ValueCount valueCount : sourceField.getValueCounts()) {
            valueCounts.add(valueCount.getValue(), valueCount.getFrequency());
        }
    }

    private List<ConceptsMap.Concept> getConceptsFromTargetField(TargetField targetField) {
        List<ConceptsMap.Concept> concepts = new ArrayList<>();
        for (Concept concept : targetField.getConcepts()) {
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
        return null;
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
