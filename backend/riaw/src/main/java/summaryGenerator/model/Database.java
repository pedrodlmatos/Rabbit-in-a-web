/* Adapted from Database (rabbit-core) */
package summaryGenerator.model;

import com.ua.riaw.etlProcedure.source.ehrDatabase.EHRDatabase;
import com.ua.riaw.etlProcedure.source.ehrTable.EHRTable;
import com.ua.riaw.etlProcedure.target.omopDatabase.OMOPDatabase;
import com.ua.riaw.etlProcedure.target.omopTable.OMOPTable;

import java.util.ArrayList;
import java.util.List;

public class Database {

    private List<Table> tables;
    private String name;
    public String conceptIdHintsVocabularyVersion;

    public Database(EHRDatabase ehrDatabase) {
        this.name = ehrDatabase.getDatabaseName();
        this.tables = getTablesFromDatabase(ehrDatabase.getTables());
    }

    public Database(OMOPDatabase omopDatabase) {
        this.name = omopDatabase.getDatabaseName();
        this.conceptIdHintsVocabularyVersion = omopDatabase.getConceptIdHintsVocabularyVersion();
        this.tables = getTablesFromTargetDatabase(omopDatabase);
    }

    private List<Table> getTablesFromDatabase(List<EHRTable> tables) {
        List<Table> tempTables = new ArrayList<>();
        for (EHRTable ehrTable : tables) {
            Table table = new Table(ehrTable, this);
            tempTables.add(table);
        }

        return tempTables;
    }

    private List<Table> getTablesFromTargetDatabase(OMOPDatabase omopDatabase) {
        List<Table> tables = new ArrayList<>();
        for (OMOPTable omopTable : omopDatabase.getTables()) {
            Table table = new Table(omopTable, this);
            tables.add(table);
        }
        return tables;
    }

    public List<Table> getTables() {
        return tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConceptIdHintsVocabularyVersion() {
        return conceptIdHintsVocabularyVersion;
    }

    public void setConceptIdHintsVocabularyVersion(String conceptIdHintsVocabularyVersion) {
        this.conceptIdHintsVocabularyVersion = conceptIdHintsVocabularyVersion;
    }

    @Override
    public String toString() {
        return "Database{" +
                "tables=" + tables +
                ", name='" + name + '\'' +
                ", conceptIdHintsVocabularyVersion='" + conceptIdHintsVocabularyVersion + '\'' +
                '}';
    }
}
