/* Adapted from ConceptsMap (rabbit-core) */
package rabbitcore.riah_datamodel;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import rabbitinahat.model.Field;

import java.io.*;
import java.util.*;

public class ConceptsMap {

    private Map<String, Map<String, List<TempConcept>>> conceptMap;
    public String vocabularyVersion;

    private ConceptsMap() {
        this.conceptMap = new HashMap<>();
    }

    public ConceptsMap(String fileName) {
        this();
        this.load(fileName);
    }

    public ConceptsMap(List<Concept> concepts) {

    }

    private void load(String fileName) {
        try (InputStream conceptStream = new FileInputStream(new File(fileName))) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conceptStream));
            //vocabularyVersion = bufferedReader.readLine();

            for (CSVRecord conceptRow : CSVFormat.RFC4180.withHeader().parse(bufferedReader)) {
                String omopTableName = conceptRow.get("omop_cdm_table");
                String omopFieldName = conceptRow.get("omop_cdm_field");

                TempConcept concept = new TempConcept();
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
            try {
                throw new IOException("Could not load concept_id hints: " + e.getMessage());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public void put(String targetTableName, String targetFieldName, TempConcept concept) {
        this.conceptMap
                .computeIfAbsent(targetTableName, t -> new HashMap<>())
                .computeIfAbsent(targetFieldName, t -> new ArrayList<>())
                .add(concept);
    }

    public List<TempConcept> get(String targetTable, String targetField) {
        return conceptMap.getOrDefault(targetTable, Collections.emptyMap()).get(targetField);
    }

    public static class TempConcept {
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


    public static class Concept {
        private Field field;
        private Long conceptId;
        private String conceptName;
        private String standardConcept;
        private String domainId;
        private String vocabularyId;
        private String conceptClassId;

        public Concept(com.ua.riaw.model.omop.Concept concept, Field field) {
            this.field = field;
            this.conceptId = concept.getConceptId();
            this.conceptName = concept.getConceptName();
            this.standardConcept = concept.getStandardConcept();
            this.domainId = concept.getDomainId();
            this.vocabularyId = concept.getVocabularyId();
            this.conceptClassId = concept.getConceptClassId();
        }

        public Field getField() {
            return field;
        }

        public void setField(Field field) {
            this.field = field;
        }

        public Long getConceptId() {
            return conceptId;
        }

        void setConceptId(Long conceptId) {
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
}
