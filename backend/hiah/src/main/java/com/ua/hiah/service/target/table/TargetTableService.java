package com.ua.hiah.service.target.table;

import com.ua.hiah.model.target.TargetTable;

public interface TargetTableService {


    /**
     * Retrieves table from OMOP CDM database given its id
     *
     * @param target_id table's id
     * @return retrieved table if found, null otherwise
     */

    TargetTable getTableById(Long target_id);


    /**
     * Changes comment of a table from the OMOP CDM database
     *
     * @param tableId table's id
     * @param comment comment to change tp
     * @param etl_id ETL procedure's id
     * @param username user's username
     * @return altered table
     */

    TargetTable changeComment(Long tableId, String comment, Long etl_id, String username);


    /**
     * Deletes stem table and its mappings
     *
     * @param table stem table on the OMOP CDM database
     */

    void removeStemTable(TargetTable table);
}
