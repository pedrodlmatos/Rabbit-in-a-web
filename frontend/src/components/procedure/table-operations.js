class TableOperations {

    /**
     * Defines the color of a table from the EHR database according to table selection
     *  - If no table is selected -> color = orange
     *  - If a table from the OMOP CDM is selected -> color = orange
     *  - If the table from the EHR database is selected:
     *    - selected table -> color = orange
     *    - other tables -> color = light orange
     *
     * @param sourceSelected if a table from EHR database is selected
     * @param selectedTable previous selected table
     * @param table new selected table
     */

    defineSourceTableColor = (sourceSelected, selectedTable, table) => {
        if (table.stem) return "#A000A0";                                                           // stem table
        if (sourceSelected && selectedTable.id !== table.id) return "rgba(255,167,84,0.86)";        // unselected source table (when a source table is selected)
        else return "rgba(255, 126, 0, 1)";                                                         // selected source source or when none is selected

    }


    defineSourceFieldColor = (sourceSelected, selectedField, field) => {
        if (sourceSelected && selectedField.id !== field.id) return "rgba(255,192,135,0.86)";      // unselected source table (when a source table is selected)
        else return "rgba(255,167,84,0.86)";
    }


    /**
     * Defines the color of a table from the EHR database according to table selection
     *  - If no table is selected -> color = orange
     *  - If a table from the OMOP CDM is selected -> color = orange
     *  - If the table from the EHR database is selected:
     *    - selected table -> color = orange
     *    - other tables -> color = light orange
     *
     * @param targetSelected if a table from EHR database is selected
     * @param selectedTable previous selected table
     * @param table new selected table
     */

    defineTargetTableColor = (targetSelected, selectedTable, table) => {
        if (table.stem) return "#A000A0";                                                         // stem table
        if (targetSelected && selectedTable.id !== table.id) return "rgb(20,134,215, 0.6)";       // unselected source table (when a source table is selected)
        else return "rgb(20,134,215)";                                                            // selected source source or when none is selected
    }


    defineTargetFieldColor = (targetSelected, selectedField, field) => {
        if (targetSelected && selectedField.id !== field.id) return "rgb(20,134,215, 0.4)";       // unselected source table (when a source table is selected)
        else return "rgb(20,134,215, 0.6)";
    }



    /**
     * Verifies if there is a stem table
     *
     * @returns {boolean}
     */

    hasStemTable = (tables) => {
        let result = false;
        tables.forEach(table => {
            if (table.stem) result = true;
        })
        return result;
    }


    /**
     * Verifies if a target table is connected to a source table
     *
     * @param mappings list of table mappings
     * @param selectedTable selected table
     * @param sourceTable_id source table's id
     * @returns true if they are connected, false otherwise
     */

    connectedToSource = (mappings, selectedTable, sourceTable_id) => {
        let result = false;
        mappings.forEach(item => {
            if (item.start.id === sourceTable_id && item.end.id === selectedTable.id) result = true;
        })
        return result;
    }


    /**
     * Verifies if a source table is connect to a target table
     *
     * @param mappings list of table mappings
     * @param selectedTable selected table
     * @param targetTable_id target table's id
     * @returns true if are connect, false otherwise
     */

    connectedToTarget = (mappings, selectedTable, targetTable_id) => {
        let result = false;
        mappings.forEach(item => {
            if (item.end.id === targetTable_id && item.start.id === selectedTable.id) result = true;
        })
        return result;
    }



}

export default new TableOperations();