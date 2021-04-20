/* Adapted from ETL (rabbit-in-a-hat) */
package rabbitinahat.model;

import com.ua.hiah.model.ETL;
import com.ua.hiah.model.FieldMapping;
import com.ua.hiah.model.TableMapping;
import com.ua.hiah.model.source.SourceDatabase;
import com.ua.hiah.model.target.TargetDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ETL_RIAH {

    private Database sourceDB;
    private Database targetDB;
    private List<ItemToItemMap> tableToTableMaps = new ArrayList<>();
    private Map<ItemToItemMap, List<ItemToItemMap>> fieldToFieldMaps = new HashMap<>();


    public ETL_RIAH(ETL etl) {
        this.sourceDB = new Database(etl.getSourceDatabase());
        this.targetDB = new Database(etl.getTargetDatabase());

        for (TableMapping tableMapping : etl.getTableMappings()) {
            Table sourceTable = this.sourceDB.getTables().stream().filter(table -> table.getName().equals(tableMapping.getSource().getName())).findFirst().orElse(null);
            Table targetTable = this.targetDB.getTables().stream().filter(trgTable -> trgTable.getName().equals(tableMapping.getTarget().getName())).findFirst().orElse(null);
            if (sourceTable != null && targetTable != null) {
                // create table mapping
                ItemToItemMap itemToItemMap = new ItemToItemMap(sourceTable, targetTable, tableMapping.getLogic(), tableMapping.isComplete());
                this.tableToTableMaps.add(itemToItemMap);
                List<ItemToItemMap> fieldMaps = new ArrayList<>();
                for (FieldMapping fieldMapping : tableMapping.getFieldMappings()) {
                    Field sourceField = sourceTable.getFields().stream().filter(field -> field.getName().equals(fieldMapping.getSource().getName())).findFirst().orElse(null);
                    Field targetField = targetTable.getFields().stream().filter(field -> field.getName().equals(fieldMapping.getTarget().getName())).findFirst().orElse(null);
                    if (sourceField != null && targetField != null) {
                        ItemToItemMap fieldMap = new ItemToItemMap(sourceField, targetField, fieldMapping.getLogic());
                        fieldMaps.add(fieldMap);
                    }
                }
                this.fieldToFieldMaps.put(itemToItemMap, fieldMaps);
            }
        }
    }

    public Database getSourceDB() {
        return sourceDB;
    }

    public void setSourceDB(Database sourceDB) {
        this.sourceDB = sourceDB;
    }

    public Database getTargetDB() {
        return targetDB;
    }

    public void setTargetDB(Database targetDB) {
        this.targetDB = targetDB;
    }

    public Mapping<Table> getTableToTableMapping() {
        return new Mapping<>(sourceDB.getTables(), targetDB.getTables(), tableToTableMaps);
    }

    public Mapping<Field> getFieldToFieldMapping(Table sourceTable, Table targetTable) {
        List<ItemToItemMap> fieldToFieldMappings = fieldToFieldMaps.get(new ItemToItemMap(sourceTable, targetTable));
        if (fieldToFieldMappings == null) {
            fieldToFieldMappings = new ArrayList<>();
            fieldToFieldMaps.put(new ItemToItemMap(sourceTable, targetTable), fieldToFieldMappings);
        }
        return new Mapping<>(sourceTable.getFields(), targetTable.getFields(), fieldToFieldMappings);
    }

    public boolean hasStemTable() {
        return false;
        //return getSourceDatabase().getTables().stream().anyMatch(Table::isStem);
    }



    @Override
    public String toString() {
        return "ETL_RIAH{" +
                "sourceDB=" + sourceDB +
                '}';
    }
}
