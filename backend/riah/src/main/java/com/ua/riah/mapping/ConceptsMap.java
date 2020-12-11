package com.ua.riah.mapping;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConceptsMap {

    private Map<String, Map<String, List<Concept>>> conceptMap;

    private ConceptsMap() {
        this.conceptMap = new HashMap<>();
    }

    public ConceptsMap(String fileName) {
        this();
        this.load(fileName);
    }

    private void load(String fileName) {

        FileInputStream fileInputStream = null;
        try {
            File file = new File(fileName);
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            System.err.println("ERROR opening concepts hint file: " + e);
        }

        try (InputStream conceptStream = fileInputStream) {
            for (CSVRecord conceptRow : CSVFormat.RFC4180.withHeader().parse(new InputStreamReader(conceptStream))) {
                String omopTableName = conceptRow.get("omop_cdm_table");
                String omopFieldName = conceptRow.get("omop_cdm_field");

                Concept concept = new Concept();
                concept.setConceptId(conceptRow.get("concept_id"));
                concept.setConceptName(conceptRow.get("concept_name"));
                concept.setStandardConcept(conceptRow.get("standard_concept"));

                // Optional fields
                if (conceptRow.isSet("domain_id")) {
                    concept.setDomainId(conceptRow.get("domain_id"));
                }

                if (conceptRow.isSet("vocabulary_id")) {
                    concept.setVocabularyId(conceptRow.get("vocabulary_id"));
                }

                if (conceptRow.isSet("concept_class_id")) {
                    concept.setConceptClassId(conceptRow.get("concept_class_id"));
                }

                this.put(omopTableName, omopFieldName, concept);
            }
        } catch (IOException e) {
            System.err.println("Could not load concept_id hints: " + e);
        }
    }

    public void put(String targetTableName, String targetFieldName, Concept concept) {
        this.conceptMap
                .computeIfAbsent(targetTableName, t -> new HashMap<>())
                .computeIfAbsent(targetFieldName, t -> new ArrayList<>())
                .add(concept);
    }

    public List<Concept> get(String targetTable, String targetField) {
        return conceptMap.getOrDefault(targetTable, Collections.emptyMap()).get(targetField);
    }
}
