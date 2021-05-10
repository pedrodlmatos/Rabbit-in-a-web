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
     * @return altered table
     */

    TargetTable changeComment(Long tableId, String comment);


    /**
     * Deletes stem table and its mappings
     *
     * @param table stem table on the OMOP CDM database
     */

    void removeStemTable(TargetTable table);
}
