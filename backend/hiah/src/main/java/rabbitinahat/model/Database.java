/* Adapted from Database (rabbit-core) */
package rabbitinahat.model;

import com.ua.hiah.model.source.SourceDatabase;
import com.ua.hiah.model.source.SourceTable;
import com.ua.hiah.model.target.TargetDatabase;
import com.ua.hiah.model.target.TargetTable;

import java.util.ArrayList;
import java.util.List;

public class Database {

    private List<Table> tables;
    private String name;
    public String conceptIdHintsVocabularyVersion;

    public Database(SourceDatabase sourceDatabase) {
        this.name = sourceDatabase.getDatabaseName();
        this.tables = getTablesFromDatabase(sourceDatabase.getTables());
    }

    public Database(TargetDatabase targetDatabase) {
        this.name = targetDatabase.getDatabaseName();
        this.conceptIdHintsVocabularyVersion = targetDatabase.getConceptIdHintsVocabularyVersion();
        this.tables = getTablesFromTargetDatabase(targetDatabase);
    }

    private List<Table> getTablesFromDatabase(List<SourceTable> tables) {
        List<Table> tempTables = new ArrayList<>();
        for (SourceTable sourceTable : tables) {
            Table table = new Table(sourceTable, this);
            tempTables.add(table);
        }

        return tempTables;
    }

    private List<Table> getTablesFromTargetDatabase(TargetDatabase targetDatabase) {
        List<Table> tables = new ArrayList<>();
        for (TargetTable targetTable : targetDatabase.getTables()) {
            Table table = new Table(targetTable, this);
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
