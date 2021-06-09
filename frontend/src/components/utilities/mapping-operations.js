/**
 * Manipulates mapping colors
 */

class MappingOperations {

    /**
     * Defines the color of a table mapping
     *
     * @param selectedBox selected table/field
     * @param complete
     * @param sourceId
     * @param targetId
     * @returns string color in arrow
     */

    defineMappingColor = (selectedBox, complete, sourceId, targetId) => {
        if (Object.keys(selectedBox).length === 0) return complete ? 'black' : 'grey'           // if table mapping is complete, color is black, grey otherwise
        else if (selectedBox.id === sourceId) return 'orange';                                          // orange if mapping starts in selected table
        else if (selectedBox.id === targetId) return 'blue';                                            // blue if mapping ends in selected table
        else return 'grey';                                                                             // grey otherwise
    }


    /**
     * Changes color from arrows that start in selected table and makes the
     * other lighter
     *
     * @param mappings list of table/field mappings
     * @param box selected table/field
     */

    selectMappingsFromSource = (mappings, box) => {
        mappings.forEach(mapping => {
            mapping.color = mapping.start.name === box.name ? 'orange' : 'lightgrey';
        })
    }


    /**
     * Changes color from arrows that end in selected table and makes the
     * other lighter
     *
     * @param mappings list of table mappings
     * @param table selected table
     */

    selectMappingsToTarget = (mappings, table) => {
        mappings.forEach(element => {
            element.color = element.end.name === table.name ? 'blue' : 'lightgrey';
        })
    }


    /**
     * Unselects all arrows (changes color to grey)
     */

    resetMappingColor = (mappings) => {
        mappings.forEach(element => {
            element.color = element.complete ? 'black' : 'grey'
        })
    }
}

export default new MappingOperations();