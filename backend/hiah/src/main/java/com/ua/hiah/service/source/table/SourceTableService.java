package com.ua.hiah.service.source.table;

import com.ua.hiah.model.source.SourceTable;

public interface SourceTableService {


    /**
     * Retrieves a table from the EHR database given its id
     *
     * @param id table's id
     * @return retrieved table if found, null otherwise
     */

    SourceTable getTableById(Long id);


    /**
     * Changes the comment of a table of the EHR database
     *
     * @param tableId table's id
     * @param comment comment to change to
     * @return changed table
     */

    SourceTable changeComment(Long tableId, String comment);


    /**
     * Removes stem table and its mappings
     *
     * @param table stem table on the EHR database
     */

    void removeStemTable(SourceTable table);
}
