package com.ua.hiah.service.tableMapping;

import com.ua.hiah.model.CDMVersion;
import com.ua.hiah.model.ETL;
import com.ua.hiah.model.TableMapping;
import com.ua.hiah.model.source.SourceDatabase;
import com.ua.hiah.model.source.SourceTable;
import com.ua.hiah.model.target.TargetDatabase;
import com.ua.hiah.model.target.TargetTable;

import java.util.List;

public interface TableMappingService {

    /**
     * Gets a table mapping given its id
     *
     * @param map_id table mapping id
     * @param etl_id ETL procedure's id
     * @param username user's username
     * @return table mapping if found, null otherwise
     */

    TableMapping getTableMappingById(Long map_id, Long etl_id, String username);


    TableMapping getTableMappingById(Long map_id);

    /**
     * Creates a table mapping between a table from the EHR database and a table from the OMOP CDM
     *
     * @param source_id source table's id
     * @param target_id target table's id
     * @param etl_id ETL procedure's id
     * @param username User's username
     * @return created table mapping
     */

    TableMapping addTableMapping(Long source_id, Long target_id, Long etl_id, String username);


    /**
     * Deletes a table mapping given its id
     *
     * @param map_id table mapping id
     * @param etl_id ETL procedure's id
     * @param username User's username
     */

    void removeTableMapping(Long map_id, Long etl_id, String username);


    /**
     * Changes the completion state of a table mapping
     *
     * @param tableMappingId table mapping id
     * @param completion state to change to
     * @param etl_id ETL procedure's id
     * @param username User's username
     * @return altered table mapping
     */

    TableMapping changeCompletionStatus(Long tableMappingId, boolean completion, Long etl_id, String username);


    /**
     * Changes the table mapping logic
     *
     * @param tableMappingId table mapping id
     * @param logic logic to change to
     * @param etl_id ETL procedure's id
     * @param username User's username
     * @return altered table mapping
     */

    TableMapping changeMappingLogic(Long tableMappingId, String logic, Long etl_id, String username);


    /**
     * Creates the table mapping contained in a JSON file
     *
     * @param etl ETL procedure object
     * @param tableMappings table mapping in JSON
     * @param sourceDatabase source database
     * @param targetDatabase target database
     * @return table mappings created
     */

    List<TableMapping> getTableMappingsFromJSON(ETL etl, List<TableMapping> tableMappings, SourceDatabase sourceDatabase, TargetDatabase targetDatabase);


    /**
     * Removes all table mappings of a given ETL procedures
     *
     * @param etl_id ETL procedure's id
     */

    void removeTableMappingsFromETL(long etl_id);


    /**
     * Creates mapping to or from a stem table (stored in file)
     *
     * @param version OMOP CDM version
     * @param targetDatabase target database
     * @param sourceStemTable stem table on EHR database
     * @param etl ETL procedure object
     * @return list of created table mappings
     */

    List<TableMapping> createMappingsWithStemTable(CDMVersion version, TargetDatabase targetDatabase, SourceTable sourceStemTable, ETL etl);




























    /**
     * Gets all table mappings from a given ETL procedure
     *
     * @param etl_id ETL procedure's id
     * @return list with table mappings
     */

    List<TableMapping> getTableMappingFromETL(Long etl_id);


    void removeTableMappingsFromTable(Long etl_id, SourceTable table);

    void removeTableMappingsToTable(Long etl_id, TargetTable table);
}
