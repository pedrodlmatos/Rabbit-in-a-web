package com.ua.riaw.service.omop.table;

import com.ua.riaw.model.omop.OMOPTable;

public interface OMOPTableService {


    /**
     * Retrieves table from OMOP CDM database given its id
     *
     * @param target_id table's id
     * @return retrieved table if found, null otherwise
     */

    OMOPTable getTableById(Long target_id);


    /**
     * Changes comment of a table from the OMOP CDM database
     *
     * @param omopTableId OMOP table's id
     * @param comment comment to change tp
     * @param etl_id ETL procedure's id
     * @param username user's username
     * @return altered table
     */

    OMOPTable changeComment(Long omopTableId, String comment, Long etl_id, String username);


    /**
     * Deletes stem table and its mappings
     *
     * @param table stem table on the OMOP CDM database
     */

    void removeStemTable(OMOPTable table);
}
