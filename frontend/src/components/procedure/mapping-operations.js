class MappingOperations {

    /**
     * Defines the color of a table mapping
     *
     * @param selectedTable selected table
     * @param {*} mapping table mapping
     * @returns string color in arrow
     */

    defineArrowColor = (selectedTable, mapping) => {
        if (Object.keys(selectedTable).length === 0) return mapping.complete ? 'black' : 'grey'         // if table mapping is complete, color is black, grey otherwise
        else if (selectedTable.id === mapping.source.id) return 'orange';                               // orange if mapping starts in selected table
        else if (selectedTable.id === mapping.target.id) return 'blue';                                 // blue if mapping ends in selected table
        else return 'grey';                                                                             // grey otherwise
    }


    /**
     * Changes color from arrows that start in selected table and makes the
     * other lighter
     *
     * @param mappings list of table mappings
     * @param table selected table
     */

    selectArrowsFromSource = (mappings, table) => {
        mappings.forEach(element => {
            element.color = element.start.name === table.name ? 'orange' : 'lightgrey';
        })
    }


    /**
     * Changes color from arrows that end in selected table and makes the
     * other lighter
     *
     * @param mappings list of table mappings
     * @param table selected table
     */

    selectArrowsFromTarget = (mappings, table) => {
        mappings.forEach(element => {
            element.color = element.end.name === table.name ? 'blue' : 'lightgrey';
        })
    }


    /**
     * Unselects all arrows (changes color to grey)
     */

    resetArrowsColor = (mappings) => {
        mappings.forEach(element => {
            element.color = element.complete ? 'black' : 'grey'
        })
    }

}

export default new MappingOperations();