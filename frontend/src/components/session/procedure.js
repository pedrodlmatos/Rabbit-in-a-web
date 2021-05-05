import { Grid, CircularProgress, makeStyles } from '@material-ui/core'
import React, { useState, useEffect } from 'react'
import Xarrow from 'react-xarrows/lib';
import ETLService from '../../services/etl-list-service';
import TableService from '../../services/table-service';
import TableMappingService from '../../services/table-mapping-service';
import Controls from '../controls/controls';
import HelpModal from '../modals/help-modal/help-modal';
import FieldMappingModal from '../modals/field-mapping-modal/field-mapping-modal';
import { CDMVersions } from '../../services/CDMVersions';
import TableMappingLogic from './table-mapping-logic';
import FilesModal from '../modals/files-modal/files-modal';
import SourceTableDetails from './source-table-details';
import TargetTableDetails from './target-table-details';

const useStyles = makeStyles(theme => ({
    tablesArea: {
        marginTop: theme.spacing(3),
        marginBottom: theme.spacing(3),
        marginRight: theme.spacing(6),
        marginLeft: theme.spacing(6)
    },
    databaseNames: {
        height: 100,
        justifyContent: 'center', 
        alignItems: 'center', 
    },
    tableDetails: {
        //marginLeft: theme.spacing(-20)
    },
    hiddenButton: {
        visibility: 'hidden'
    },
    showButton: {
        marginTop: theme.spacing(1),
        visibility: 'false'
    }
}))


export default function Procedure() {
    const classes = useStyles();

    const initialETLValues = {
        id: null, name: null,
        targetDatabase: { id: null, tables: [], databaseName: '' },
        sourceDatabase: { id: null, tables: [], databaseName: null }
    }

    const columns = React.useMemo(() => [
        { Header: 'Field', accessor: 'field' },
        { Header: 'Type', accessor: 'type' },
        { Header: 'Description', accessor: 'description' }
    ], [])

    const [loading, setLoading] = useState(true);
    const [etl, setEtl] = useState(initialETLValues);
    const [omopName, setOmopName] = useState('');
    const [mappings, setMappings] = useState([]);
    const [selectedMapping, setSelectedMapping] = useState({});
    const [showHelpModal, setShowHelpModal] = useState(false);
    const [showFilesModal, setShowFilesModal] = useState(false);
    
    const [selectedTable, setSelectedTable] = useState({})
    const [sourceSelected, setSourceSelected] = useState(false);
    const [showTableDetails, setShowTableDetails] = useState(false);
    const [tableDetails, setTableDetails] = useState([]);
    const [loadingSaveTableComment, setLoadingSaveTableComment] = useState(false);
      
    const [showFieldMappingModal, setShowFieldMappingModal] = useState(false);
    const [loadingSaveTableMappingLogic, setLoadingSaveTableMappingLogic] = useState(false);
    
    useEffect(() => {
        const procedure_id = window.location.pathname.toString().replace("/procedure/", "");

        // make request to API
        ETLService.getETLById(procedure_id).then(res => {
            setEtl({
                id: res.data.id,
                name: res.data.name,
                sourceDatabase: res.data.sourceDatabase,
                targetDatabase: res.data.targetDatabase
            });
            setOmopName(CDMVersions.filter(function(cdm) { return cdm.id === res.data.targetDatabase.databaseName })[0].name);
            // table mappings
            let maps = [];
            res.data.tableMappings.forEach(function(item) {
                const arrow = {
                    id: item.id,
                    start:  item.source,
                    end: item.target,
                    complete: item.complete,
                    logic: item.logic,
                    color: item.complete ? "black" : "grey"
                }
                maps.push(arrow);
            });
            setMappings(maps);    
            setLoading(false);
        }).catch(res => {
            console.log(res);
        })
    }, []);


    /**
     * Defines the color of a table from the EHR database according to table selection
     *  - If no table is selected -> color = orange
     *  - If a table from the OMOP CDM is selected -> color = orange
     *  - If the table from the EHR database is selected:
     *    - selected table -> color = orange
     *    - other tables -> color = light orange
     */

    const defineSourceTableColor = (table) => {
        if (table.stem) return "#A000A0";                                           // stem table
        if (sourceSelected && selectedTable.id !== table.id) return "#FFD3A6";      // unselected source table (when a source table is selected)
        else return "#FF9224";                                                      // selected source source or when none is selected
    }


    /**
     * Defines the selected table and changes state of current and previous selected table.
     *
     *  - If no table is selected, only changes the state of the selected table
     *  - If there is a table selected, unselect it and then select the new table changing states
     *  - If select the table that was previous selected, unselects it
     *
     * @param table selected source table
     */

    const selectSourceTable = (table) => {
        // clean state
        setSelectedMapping({});
        if (Object.keys(selectedTable).length === 0) {
            // all tables are unselected
            setSelectedTable(table);
            setSourceSelected(true);
            selectArrowsFromSource(table);      // change color of mappings that comes from the selected table
            defineData(table);                  // define fields info
        } else if (selectedTable === table) {
            // select the same table -> unselect
            resetArrowsColor();                 // change color of arrows to grey
            setSourceSelected(false);     // unselect
            setShowTableDetails(false);
            setSelectedTable({});
            setTableDetails(null);
        } else {
            // select any other source table
            resetArrowsColor();                // change color of arrows to grey
            setSelectedTable(table);           // change select table information
            setSourceSelected(true);
            selectArrowsFromSource(table);     // change color of mappings that comes from the selected table
            defineData(table);                 // change content of fields table
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
     * @param table
     */

    const selectTargetTable = (table) => {
        // clean state
        setSelectedMapping({});
        if (Object.keys(selectedTable).length === 0) {
            // no table is selected
            selectArrowsFromTarget(table);          // change color of mappings that goes to the selected table
            setSelectedTable(table);                // change select table information
            setSourceSelected(false);
            defineData(table);                      // change content of fields table
        } else if (selectedTable === table) {
            // select the same table -> unselect
            resetArrowsColor();                     // change color of arrows to grey
            setSourceSelected(false);
            setShowTableDetails(false);
            setSelectedTable({});             // unselect
            setTableDetails(null);
        } else if (sourceSelected) {
            // source table is selected -> create arrow
            const source_id = selectedTable.id;
            //resetArrowsColor();                    // change arrows color to grey
            //setSelectedTable({})                   // unselects tables
            createTableMapping(source_id, table.id); // create arrow
            //setSourceSelected(false);              // clean state
            //setShowTableDetails(false);
            //setTableDetails(null);
        } else {
            // other target table is selected
            resetArrowsColor();                      // change color of arrows to grey
            setSelectedTable(table);                 // change select table information
            setSourceSelected(false);
            selectArrowsFromTarget(table);           // change color of mappings that comes from the selected table
            defineData(table);                       // change content of fields table
        }
    }


    /**
     * Changes CDM database 
     *
     * @param e change OMOP CDM event
     */
    
    const handleCDMChange = e => {
        setLoading(true);
        if (Object.keys(selectedTable).length > 0) {                             // clean state if any table is selected
            setSelectedTable({});
            setSourceSelected(false);
            setShowTableDetails(false);
            setTableDetails([]);
        }
        if (Object.keys(selectedMapping).length > 0) {                          // clean state if any table mapping is selected
            setSelectedMapping({});
        }
        ETLService.changeTargetDatabase(etl.id, e.target.value).then(response => {
            setEtl({...etl, targetDatabase: response.data.targetDatabase });
            setOmopName(CDMVersions.filter(function(cdm) { return cdm.id === response.data.targetDatabase.databaseName })[0].name);
            setMappings([]);
            setLoading(false);
        });
    }


    /**
     * Defines the color of a table mapping
     * 
     * @param {*} mapping table mapping
     * @returns string
     */

    const defineArrowColor = (mapping) => {
        if (Object.keys(selectedTable).length === 0) return mapping.complete ? 'black' : 'grey'
        else if (selectedTable.id === mapping.source.id) return 'orange';
        else if (selectedTable.id === mapping.target.id) return 'blue';
        else return 'grey';
    }


    /**
     * Changes color from arrows that start in selected table and makes the
     * other lighter
     * 
     * @param {*} table selected table
     */

    const selectArrowsFromSource = (table) => {
        mappings.forEach(element => {
            element.color = element.start.name === table.name ? 'orange' : 'lightgrey';
        })
    }


    /**
     * Changes color from arrows that end in selected table and makes the
     * other lighter
     * 
     * @param {*} table selected table
     */

    const selectArrowsFromTarget = (table) => {
        mappings.forEach(element => {
            element.color = element.end.name === table.name ? 'blue' : 'lightgrey';
        })
    }


    /**
     * Unselects all arrows (changes color to grey)
     */

    const resetArrowsColor = () => {
        mappings.forEach(element => {
            element.color = element.complete ? 'black' : 'grey'
        })
    }


    /**
     * Selects an arrow (changes its color to red)
     *  - If no arrow is previously selected, only selects an arrow
     *  - If selects the arrow previously selected, unselect it
     *  - If selects other arrow, unselects previous and selects the new one
     * 
     *  @param {*} arrow selected table mapping
     */

    const selectArrow = (arrow) => {
        resetArrowsColor();                                                         // change color to grey
        setSelectedTable({});
        setSourceSelected(false);
        setShowTableDetails(false);
        setTableDetails([]);
        const index = mappings.indexOf(arrow);
        if (Object.keys(selectedMapping).length === 0) {                            // no arrow is selected
            let arrows = mappings;
            arrows[index].color = "red";
            setSelectedMapping(arrow);
            setMappings(arrows);
        } else if(selectedMapping === arrow) {                                      // select the arrow previous selected to unselect
            setSelectedMapping({});
            resetArrowsColor();
        } else {                                                                    // select any other unselected arrow
            resetArrowsColor();                                                     // unselect previous
            let arrows = mappings;                                                  // select a new one
            arrows[index].color = "red";
            setSelectedMapping(arrow);
            setMappings(arrows);
        }
    }



    /**
     * Creates an arrow between a source table and a target table.
     *
     * @param sourceTable_id source table's id
     * @param targetTable_id target table's id
     */

    const createTableMapping = (sourceTable_id, targetTable_id) => {
        TableMappingService.addTableMapping(etl.id, sourceTable_id, targetTable_id).then(res => {
            const arrow = {
                id: res.data.id,
                start: res.data.source,
                end: res.data.target,
                complete: res.data.complete,
                logic: res.data.logic,
                color: defineArrowColor(res.data)
            }
            setMappings([arrow].concat(mappings));
        }).catch(err => {
            console.log(err);
        })
    }


    /**
     * Removes the selected table mapping
     */

    const removeTableMapping = () => {
        // close field mapping modal
        setShowFieldMappingModal(false);
        // make request to API
        removeMapping(etl.id, selectedMapping.id);
        setSelectedMapping({});
    }


    /**
     * Makes a call to API to delete a table mapping and replace the previous with ones received
     * 
     * @param {*} etl_id ETL id
     * @param {*} mapping_id table mapping id
     */

    const removeMapping = (etl_id, mapping_id) => {
        TableMappingService.removeTableMapping(etl_id, mapping_id).then(res => {
            let maps = []
            res.data.forEach(function(item) {
                const arrow = {
                    id: item.id,
                    start: item.source,
                    end: item.target,
                    complete: item.complete,
                    color: defineArrowColor(item)
                }
                maps = maps.concat(arrow);
            });
            setMappings(maps);
        }).catch(res => {
            console.log(res);
        })
    }


    /**
     * Closes the field mapping modal and cleans state
     */

    const closeFieldMappingModal = () => {
        setShowFieldMappingModal(false)
        setSelectedMapping({});
    }


    /**
     * 
     * @param {*} tableMappingId 
     * @param {*} completion 
     */

    const changeCompleteStatus = (tableMappingId, completion) => {
        mappings.forEach(map => {
            if (map.id === tableMappingId) {
                map.color = completion ? "black" : "grey";
                map.complete = completion;
            }
        })
    }


    /**
     * Defines the content of fields table (field name, type and description)
     *
     * @param table table with data
     */

    const defineData = (table) => {
        let data = [];
        table.fields.forEach(element => {
            data.push({
                field: element.name,
                type: element.type,
                description: element.description
            })
        })
        setTableDetails(data);
        setShowTableDetails(true);
    }


    /**
     * Changes state to close field mapping modal
     */

    const openFieldMappingModal = (mapping) => {
        setSelectedMapping(mapping);
        setShowFieldMappingModal(true);
    }


    /**
     * Save table comment
     */

    const saveComment = () => {
        setLoadingSaveTableComment(true);
        if (sourceSelected) {
            TableService.changeSourceTableComment(selectedTable.id, selectedTable.comment).then(response => {
                const index = etl.sourceDatabase.tables.findIndex(x => x.id === response.data.id);
                etl.sourceDatabase.tables[index].comment = response.data.comment;
                setLoadingSaveTableComment(false);
            }).catch(error => {
                console.log(error);
            });
        } else {
            TableService.changeTargetTableComment(selectedTable.id, selectedTable.comment).then(response => {
                const index = etl.targetDatabase.tables.findIndex(x => x.id === response.data.id);
                etl.targetDatabase.tables[index].comment = response.data.comment;
                setLoadingSaveTableComment(false);
            }).catch(error => {
                console.log(error);
            });
        }   
    }


    /**
     * Verifies if a source table is connect to a target table
     * 
     * @param {*} targetTable target table
     * @returns true if are connect, false otherwise
     */

    const connectedToTarget = (targetTable_id) => {
        let result = false;
        mappings.forEach(item => {
            if (item.end.id === targetTable_id && item.start.id === selectedTable.id) {
                result = true;
            }
        })
        return result;
    }


    /**
     * Creates a table mapping between two tables or removes it if already exists
     * 
     * @param {*} e check event
     */

    const connectToTargetTable = e => {
        const targetTable_id = e.target.value[0];
        
        if (connectedToTarget(targetTable_id)) {
            mappings.forEach(item => {
                if (item.end.id === targetTable_id && item.start.id === selectedTable.id) {
                    removeMapping(etl.id, item.id);
                }
            })
        } else {
            createTableMapping(selectedTable.id, targetTable_id);
        }
    }


    /**
     * Verifies if a target table is connected to a source table
     * 
     * @param {*} sourceTable_id source table id
     * @returns true if they are connected, false otherwise
     */
    const connectedToSource = (sourceTable_id) => {
        let result = false;
        mappings.forEach(item => {
            if (item.start.id === sourceTable_id && item.end.id === selectedTable.id) {
                result = true;
            }
        })
        return result;
    }


    /**
     * Creates a table mapping between two tables or removes it if already exists
     * 
     * @param {*} e check event
     */

    const connectToSourceTable = e => {
        const sourceTable_id = e.target.value[0];
        
        if (connectedToSource(sourceTable_id)) {
            mappings.forEach(item => {
                if (item.start.id === sourceTable_id && item.end.id === selectedTable.id) {
                    removeMapping(etl.id, item.id);
                }
            })
        } else {
            createTableMapping(sourceTable_id, selectedTable.id);
        }
    }


    /**
     * Saves changed table mapping logic
     */

    const saveTableMappingLogic = () => {
        setLoadingSaveTableMappingLogic(true);
        // make request to API
        TableMappingService.editMappingLogic(selectedMapping.id, selectedMapping.logic).then(response => {
            let index = mappings.findIndex(x => x.id === response.data.id);
            mappings[index].logic = response.data.logic;
            setLoadingSaveTableMappingLogic(false);
        }).catch(error => {
            console.log(error);
        });
    }


    return(
        <div className={classes.tablesArea}>
            { loading ? (
                <CircularProgress color="primary" variant="indeterminate" size={40} />
            ) : (
                <Grid container>
                    <Grid item xs={6} sm={6} md={6} lg={6}>
                        <Grid container>
                            <Grid item xs={6} sm={6} md={6} lg={6}>
                                <h1>{ etl.name }</h1>
                            </Grid>

                            <Grid item xs={2} sm={2} md={2} lg={2}>
                                <Controls.Button text="Help " onClick={() => setShowHelpModal(true)}>
                                    <i className="fa fa-info"/>
                                </Controls.Button>
                                <HelpModal modalIsOpen={showHelpModal} closeModal={() => setShowHelpModal(false)}/>
                            </Grid>

                            <Grid item xs={2} sm={2} md={2} lg={2}>
                                <Controls.Button text="Files" onClick={() => setShowFilesModal(true)} />
                                <FilesModal etl_id={etl.id} openModal={showFilesModal} closeModal={() => setShowFilesModal(false)} />
                            </Grid>


                            { Object.keys(selectedMapping).length !== 0 ? (
                                <Grid item xs={2} sm={2} md={2} lg={2}>
                                    <Controls.Button  
                                        color="secondary" 
                                        text="Remove" 
                                        onClick={removeTableMapping} 
                                    />
                                </Grid>
                            ) : (
                              <Grid item xs={2} sm={2} md={2} lg={2}>
                                <Controls.Button
                                  color="primary"
                                  text="Add stem table"
                                  onClick={() => ETLService.addStemTables(etl.id)}
                                />
                              </Grid>
                            ) }
                        </Grid>
                            
                        <Grid className={classes.databaseNames} container>
                            <Grid item xs={6} sm={6} md={6} lg={6}>
                                <h4>{ etl.sourceDatabase.databaseName }</h4>
                            </Grid>
                            
                            <Grid item xs={6} sm={6} md={6} lg={6}>
                                <Controls.Select 
                                    name={omopName} 
                                    label="OMOP CDM" 
                                    value={etl.targetDatabase.databaseName}
                                    onChange={handleCDMChange}
                                    options={CDMVersions} 
                                />
                            </Grid>
                        </Grid>

                        <Grid container>
                            <Grid item xs={6} sm={6} md={6} lg={6}>                                
                                { etl.sourceDatabase.tables.map(item => {
                                    return(
                                        <Controls.TooltipBox
                                            key={item.id}
                                            id={'s_' + item.name}
                                            element={item}
                                            handler="right"
                                            clicked={selectedTable.id === item.id}
                                            help="Select first an EHR table and then an OMOP CDM table" 
                                            position="right-end"
                                            color={defineSourceTableColor(item)}
                                            border="#000000"
                                            handleSelection={selectSourceTable}
                                            createMapping={createTableMapping}
                                        />
                                    )
                                })}
                            </Grid>
                            
                            <Grid item xs={6} sm={6} md={6} lg={6}>                               
                                { etl.targetDatabase.tables.map(item => {
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
                                            handleSelection={selectTargetTable}
                                            createMapping={createTableMapping}
                                        />
                                    )
                                })}
                            </Grid>
                            { mappings.map((ar, i) => (
                                <Xarrow key={i}
                                    start={'s_' + ar.start.name}
                                    end={'t_' + ar.end.name}
                                    startAnchor="right"
                                    endAnchor="left"
                                    color={ar.color}
                                    strokeWidth={7.5}
                                    curveness={0.5}
                                    passProps={{
                                        onClick: () => selectArrow(ar),
                                        onDoubleClick: () => openFieldMappingModal(ar)
                                    }}
                                />
                            ))}
                            <FieldMappingModal 
                                openModal={showFieldMappingModal}
                                closeModal={closeFieldMappingModal}
                                mappingId={selectedMapping.id}
                                removeTableMapping={removeTableMapping}
                                changeMappingCompletion={changeCompleteStatus}
                            />
                        </Grid>
                    </Grid>

                    <Grid className={classes.tableDetails} item xs={6} sm={6} md={6} lg={6}>
                        { showTableDetails ? (
                            <div>
                                { sourceSelected ? (
                                    <SourceTableDetails
                                        table={selectedTable}
                                        columns={columns}
                                        data={tableDetails}
                                        onChange={(e) => setSelectedTable({...selectedTable, comment: e.target.value })}
                                        disabled={loadingSaveTableComment}
                                        save={saveComment}
                                        omopTables={etl.targetDatabase.tables}
                                        verify={connectedToTarget}
                                        connect={connectToTargetTable}
                                    />
                                ) : (
                                    <TargetTableDetails
                                        table={selectedTable}
                                        columns={columns}
                                        data={tableDetails}
                                        onChange={(e) => setSelectedTable({...selectedTable, comment: e.target.value })}
                                        disabled={loadingSaveTableComment}
                                        save={saveComment}
                                        ehrTables={etl.sourceDatabase.tables}
                                        verify={connectedToSource}
                                        connect={connectToSourceTable}
                                    />
                                    
                                ) }
                            </div>
                        ) : (
                            <></>
                        )}

                        { Object.keys(selectedMapping).length !== 0 ? (
                            <TableMappingLogic
                                value={selectedMapping.logic === null ? '' : selectedMapping.logic}
                                disabled={loadingSaveTableMappingLogic}
                                onChange={(e) => setSelectedMapping({...selectedMapping, logic: e.target.value})}
                                save={() => saveTableMappingLogic()}
                            />
                        ) : (
                            <></>
                        ) }
                    </Grid>
                </Grid>
            )}
        </div>
    )
}