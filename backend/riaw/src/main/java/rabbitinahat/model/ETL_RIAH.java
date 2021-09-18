/* Adapted from ETL (rabbit-in-a-hat) */
package rabbitinahat.model;

import com.ua.riaw.etlProcedure.ETL;
import com.ua.riaw.etlProcedure.fieldMapping.FieldMapping;
import com.ua.riaw.etlProcedure.tableMapping.TableMapping;

import java.util.*;

public class ETL_RIAH {

    private Database sourceDB;
    private Database targetDB;
    private List<ItemToItemMap> tableToTableMaps = new ArrayList<>();
    private Map<ItemToItemMap, List<ItemToItemMap>> fieldToFieldMaps = new HashMap<>();


    public ETL_RIAH(ETL etl) {
        this.sourceDB = new Database(etl.getSourceDatabase());
        this.targetDB = new Database(etl.getTargetDatabase());

        for (TableMapping tableMapping : etl.getTableMappings()) {
            Table sourceTable = this.sourceDB.getTables().stream().filter(table -> table.getName().equals(tableMapping.getEhrTable().getName())).findFirst().orElse(null);
            Table targetTable = this.targetDB.getTables().stream().filter(trgTable -> trgTable.getName().equals(tableMapping.getOmopTable().getName())).findFirst().orElse(null);
            if (sourceTable != null && targetTable != null) {
                // create table mapping
                ItemToItemMap itemToItemMap = new ItemToItemMap(sourceTable, targetTable, tableMapping.getLogic(), tableMapping.isComplete());
                this.tableToTableMaps.add(itemToItemMap);
                List<ItemToItemMap> fieldMaps = new ArrayList<>();
                for (FieldMapping fieldMapping : tableMapping.getFieldMappings()) {
                    Field sourceField = sourceTable.getFields().stream().filter(field -> field.getName().equals(fieldMapping.getEhrField().getName())).findFirst().orElse(null);
                    Field targetField = targetTable.getFields().stream().filter(field -> field.getName().equals(fieldMapping.getOmopField().getName())).findFirst().orElse(null);
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

    public Mapping<Table> getMappingToTargetTable(Table targetTable) {
        List<ItemToItemMap> mappings = new ArrayList<>();
        List<Table> sourceTables = new ArrayList<>();
        for (ItemToItemMap map : tableToTableMaps) {
            if (map.getTargetItem() == targetTable) {
                sourceTables.add((Table) map.getSourceItem());
                mappings.add(map);
            }
        }
        return new Mapping<>(sourceTables, new ArrayList<>(Collections.singleton(targetTable)), mappings);
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

    public int getNumberOfMappingsToTable(MappableItem item) {
        int nMaps = 0;
        for (ItemToItemMap map : tableToTableMaps) {
            if (map.getTargetItem() == item) {
                nMaps++;
            }
        }
        return nMaps;
    }
}
