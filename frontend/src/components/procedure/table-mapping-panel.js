import { Grid, makeStyles } from '@material-ui/core'
import Controls from '../controls/controls'
import TableOperations from './table-operations'
import React, { useState } from 'react'
import Xarrow from 'react-xarrows'
import MappingOperations from '../utilities/mapping-operations'

const useStyles = makeStyles((theme) => ({
    tableDetails: {
        //marginLeft: theme.spacing(-20)
    },
}))

export default function TableMappingPanel(props) {

    const classes = useStyles();
    const {
        ehrTables,
        omopTables,
        tableMappings,
        selectTable,
        createTableMapping,
        showTableMapping,
        openFieldMappingPanel
    } = props;

    const [selectedTable, setSelectedTable] = useState({});
    const [sourceSelected, setSourceSelected] = useState(false);

    const [selectedTableMapping, setSelectedTableMapping] = useState({});

    /**
     * Defines the selected table and changes state of current and previous selected table.
     *
     *  - If no table is selected, only changes the state of the selected table
     *  - If there is a table selected, unselect it and then select the new table changing states
     *  - If select the table that was previous selected, unselects it
     *
     * @param ehrTable selected source table
     */

    const selectEHRTable = (ehrTable) => {
        // clean state
        showTableMapping(false, {});
        if (Object.keys(selectedTable).length === 0) {
            // all tables are unselected -> select table
            setSelectedTable(ehrTable);
            setSourceSelected(true);
            MappingOperations.selectMappingsFromSource(tableMappings, ehrTable);                // change color of mappings that comes from the selected table
            selectTable(true, ehrTable, defineData(ehrTable), true);                            // define fields info in parent
        } else if (selectedTable === ehrTable) {
            // select the same table -> unselect
            MappingOperations.resetMappingColor(tableMappings);                                 // change color of arrows to grey
            setSourceSelected(false);                                                     // unselect
            setSelectedTable({});
            selectTable(false, {}, [], false);                                                  // update parent
        } else {
            // select any other source table
            MappingOperations.resetMappingColor(tableMappings);                                 // change color of arrows to grey
            setSelectedTable(ehrTable);                                                         // change selected table
            setSourceSelected(true);
            MappingOperations.selectMappingsFromSource(tableMappings, ehrTable);                // change color of mappings that comes from the selected table
            selectTable(true, ehrTable, defineData(ehrTable), true)                             // change content of fields table in parent
        }
    }


    /**
     * Defines the selected table and changes state of current and previous selected table
     *
     * - If no table is selected, only changes the state of the selected table
     * - If theres is a source table selected, creates arrow
     * - If select the same table, unselect
     * - Else selects a different target table
     *
     * @param omopTable selected target table
     */

    const selectOMOPTable = (omopTable) => {
        // clean state
        showTableMapping(false, {});
        if (Object.keys(selectedTable).length === 0) {
            // no table is selected
            MappingOperations.selectMappingsToTarget(tableMappings, omopTable);          // change color of mappings that goes to the selected table
            setSelectedTable(omopTable);                                                 // change select table information
            setSourceSelected(false);
            selectTable(true, omopTable, defineData(omopTable), false);                  // change content of fields table
        } else if (selectedTable === omopTable) {
            // select the same table -> unselect
            MappingOperations.resetMappingColor(tableMappings);                          // change color of arrows to grey
            setSourceSelected(false);
            setSelectedTable({});                                                  // unselect
            selectTable(false, {}, {}, false);
        } else if (sourceSelected) {
            // source table is selected -> create arrow
            const source_id = selectedTable.id;
            //resetArrowsColor();                                                        // change arrows color to grey
            //setSelectedTable({})                                                       // unselects tables
            createTableMapping(source_id, omopTable.id);                                 // create arrow
            //setSourceSelected(false);                                                  // clean state
            //setShowTableDetails(false);
            //setTableDetails(null);
        } else {
            // other target table is selected
            MappingOperations.resetMappingColor(tableMappings);                            // change color of arrows to grey
            setSelectedTable(omopTable);                                                   // change select table information
            setSourceSelected(false);
            MappingOperations.selectMappingsToTarget(tableMappings, omopTable);            // change color of mappings that comes from the selected table
            selectTable(true, omopTable, defineData(omopTable), false);                    // change content of fields table
        }
    }


    /**
     * Defines the content of the table details (field name, type and description)
     *
     * @param table table with data
     */

    const defineData = table => {
        let data = [];
        table.fields.forEach(element => {
            data.push({
                field: element.name,
                type: element.type,
                description: element.description
            })
        })
        return data;
    }


    /**
     * Selects a table mapping (changing its color to red)
     * - If no table mapping is previously selected, only selects the table mapping
     * - If selects the table mapping previously selected, unselect it
     * - If selects other table mapping, unselects previous and selects the new one
     *
     * @param tableMapping selected table mapping
     */

    const selectTableMapping = (tableMapping) => {
        // change color to grey
        MappingOperations.resetMappingColor(tableMappings);
        // clean state
        setSourceSelected(false);
        setSelectedTable({});
        selectTable(false, {}, false);
        const index = tableMappings.indexOf(tableMapping);

        if (Object.keys(selectedTableMapping).length === 0) {
            // no arrow is selected
            tableMappings[index].color = "red";                                              // change to red the selected table mapping
            setSelectedTableMapping(tableMapping);
            showTableMapping(true, tableMapping);
            //setTableMappings(mappings);
        } else if(selectedTableMapping === tableMapping) {
            // select the arrow previous selected to unselect
            setSelectedTableMapping({});
            showTableMapping(false, {})
            MappingOperations.resetMappingColor(tableMappings);                         // change color of arrows to grey
        } else {
            // select any other unselected arrow
            MappingOperations.resetMappingColor(tableMappings);                         // change color of arrows to grey
            tableMappings[index].color = "red";                                              // change to red the selected table mapping
            setSelectedTableMapping(tableMapping);
            showTableMapping(true, tableMapping);
            //setTableMappings(mappings);
        }
    }


    return(
        <Grid container>
            {/* EHR tables*/}
            <Grid item xs={6} sm={6} md={6} lg={6}>
                { ehrTables.map(item => {
                    return(
                        <Controls.TooltipBox
                            key={item.id}
                            id={'s_' + item.name}
                            element={item}
                            handler="right"
                            clicked={selectedTable.id === item.id}
                            help="Select first an EHR table and then an OMOP CDM table"
                            position="right-end"
                            color={TableOperations.defineSourceTableColor(sourceSelected, selectedTable, item)}
                            border="#000000"
                            handleSelection={selectEHRTable}
                            createMapping={createTableMapping}
                        />
                    )
                })}
            </Grid>

            {/* OMOP CDM tables*/}
            <Grid item xs={6} sm={6} md={6} lg={6}>
                { omopTables.map(item => {
                    return(
                        <Controls.TooltipBox
                            key={item.id}
                            id={'t_' + item.name}
                            element={item}
                            handler="left"
                            clicked={item.id === selectedTable.id}
                            help="Select first an EHR table and then an OMOP CDM table"
                            position="right-end"
                            color={item.stem ? "#A000A0" : "#53ECEC"}
                            border="#000000"
                            handleSelection={selectOMOPTable}
                            createMapping={createTableMapping}
                        />
                    )
                })}
            </Grid>

            {/* Table mappings */}
            { tableMappings.map((ar, i) => (
                <Xarrow key={i}
                        start={'s_' + ar.start.name}
                        end={'t_' + ar.end.name}
                        startAnchor="right"
                        endAnchor="left"
                        color={ar.color}
                        strokeWidth={7.5}
                        curveness={0.5}
                        passProps={{
                            onClick: () => selectTableMapping(ar),
                            onDoubleClick: () => openFieldMappingPanel(ar)
                        }}
                />
            ))}
        </Grid>
    )
}