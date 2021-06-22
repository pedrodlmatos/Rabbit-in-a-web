package com.ua.riaw.service.tableMapping;

import com.ua.riaw.model.CDMVersion;
import com.ua.riaw.model.ETL;
import com.ua.riaw.model.TableMapping;
import com.ua.riaw.model.ehr.EHRDatabase;
import com.ua.riaw.model.ehr.EHRTable;
import com.ua.riaw.model.omop.OMOPDatabase;
import com.ua.riaw.model.omop.OMOPTable;

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
     * @param ehrTableId source table's id
     * @param omopTableId target table's id
     * @param etl_id ETL procedure's id
     * @param username User's username
     * @return created table mapping
     */

    TableMapping addTableMapping(Long ehrTableId, Long omopTableId, Long etl_id, String username);


    /**
     * Deletes a table mapping given its id
     *
     * @param tableMappingId table mapping id
     * @param etl_id ETL procedure's id
     * @param username User's username
     */

    void removeTableMapping(Long tableMappingId, Long etl_id, String username);


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
     * @param ehrDatabase source database
     * @param omopDatabase target database
     * @return table mappings created
     */

    List<TableMapping> getTableMappingsFromJSON(ETL etl, List<TableMapping> tableMappings, EHRDatabase ehrDatabase, OMOPDatabase omopDatabase);


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
     * @param omopDatabase target database
     * @param sourceStemTable stem table on EHR database
     * @param etl ETL procedure object
     * @return list of created table mappings
     */

    List<TableMapping> createMappingsWithStemTable(CDMVersion version, OMOPDatabase omopDatabase, EHRTable sourceStemTable, ETL etl);




























    /**
     * Gets all table mappings from a given ETL procedure
     *
     * @param etl_id ETL procedure's id
     * @return list with table mappings
     */

    List<TableMapping> getTableMappingFromETL(Long etl_id);


    void removeTableMappingsFromTable(Long etl_id, EHRTable table);

    void removeTableMappingsToTable(Long etl_id, OMOPTable table);
}
