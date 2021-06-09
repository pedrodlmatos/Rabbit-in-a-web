package com.ua.hiah.service.ehr.table;

import com.ua.hiah.model.ehr.EHRTable;

public interface EHRTableService {


    /**
     * Retrieves a table from the EHR database given its id
     *
     * @param id table's id
     * @return retrieved table if found, null otherwise
     */

    EHRTable getTableById(Long id);


    /**
     * Changes the comment of a table of the EHR database
     *
     *
     * @param ehrTableId table's id
     * @param comment comment to change to
     * @param etl_id ETL procedure's id
     * @param username User's username
     * @return changed table
     */

    EHRTable changeComment(Long ehrTableId, String comment, Long etl_id, String username);


    /**
     * Removes stem table and its mappings
     *
     * @param table stem table on the EHR database
     */

    void removeStemTable(EHRTable table);
}
