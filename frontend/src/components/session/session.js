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
import ElementBox from '../controls/box';
import InfoTable from '../info-table/info-table';

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


export default function Session() {
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
    
    const [selectedTable, setSelectedTable] = useState({})
    const [sourceSelected, setSourceSelected] = useState(false);
    const [showTableDetails, setShowTableDetails] = useState(false);
    const [tableDetails, setTableDetails] = useState([]);
    
    
    const [showFieldMappingModal, setShowFieldMappingModal] = useState(false);
    const [enableEditCommentButton, setEnableEditCommentButton] = useState(true);    

    
    useEffect(() => {
        const session_id = window.location.pathname.toString().replace("/session/", "");
        
        ETLService.getETLById(session_id).then(res => {
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
                    start: item.source,
                    end: item.target,
                    complete: item.complete,
                    color: defineArrowColor(item)
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
     * Defines the selected table and changes state of current and previous selected table.
     *
     *  - If no table is selected, only changes the state of the selected table
     *  - If there is a table selected, unselect it and then select the new table changing states
     *  - If select the table that was previous selected, unselects it
     *
     * @param table selected source table
     */

    const selectSourceTable = (table) => {
        setEnableEditCommentButton(true);                                           // clean state
        setSelectedMapping({});
        if (Object.keys(selectedTable).length === 0) {                              // all tables are unselected
            setSelectedTable(table);
            setSourceSelected(true);
            selectArrowsFromSource(table);                                          // change color of mappings that comes from the selected table
            defineData(table);                                                      // define fields info
        } else if (selectedTable === table) {                                       // select the same table
            resetArrowsColor();                                                     // change color of arrows to grey
            setSelectedTable({});                                                   // unselect
            setSourceSelected(false);
            setShowTableDetails(false);
            setTableDetails(null);
        } else {                                                                    // select any other source table
            resetArrowsColor();                                                     // change color of arrows to grey
            setSelectedTable(table);                                                // change select table information
            setSourceSelected(true);
            selectArrowsFromSource(table);                                          // change color of mappings that comes from the selected table
            defineData(table);                                                      // change content of fields table
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
     * @param element
     */

    const selectTargetTable = (table) => {
        setEnableEditCommentButton(true);
        setSelectedMapping({});
        if (Object.keys(selectedTable).length === 0) {                              // no table is selected
            selectArrowsFromTarget(table);                                          // change color of mappings that goes to the selected table
            setSelectedTable(table);                                                // change select table information
            setSourceSelected(false);
            defineData(table);                                                      // change content of fields table
        } else if (selectedTable === table) {                                       // select the same table -> unselect
            resetArrowsColor();                                                     // change color of arrows to grey
            setSelectedTable({});                                                   // unselect
            setSourceSelected(false);
            setShowTableDetails(false);
            setTableDetails(null);
        } else if (sourceSelected) {                                                // source table is selected -> create arrow
            resetArrowsColor();                                                     // change arrows color to grey
            createArrow(selectedTable.id, table.id);                                // create arrow
            setSelectedTable({});                                                   // unselects tables
            setSourceSelected(false);                                               // clean state
            setShowTableDetails(false);
            setTableDetails(null);
        } else {                                                                    // other target table is selected
            resetArrowsColor();                                                     // change color of arrows to grey
            setSelectedTable(table);                                                // change select table information
            setSourceSelected(false);
            selectArrowsFromTarget(table);                                          // change color of mappings that comes from the selected table
            defineData(table);                                                      // change content of fields table
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
     * @returns color
     */

    const defineArrowColor = (mapping) => {
        let color = 'grey';
        if (Object.keys(selectedTable).length === 0)
            color = mapping.complete ? 'black' : 'grey'
        else if (selectedTable.id === mapping.source.id)
            color = 'orange';
        else if (selectedTable.id === mapping.target.id)
            color = 'blue';
        return color;
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
     */

    const selectArrow = (arrow) => {
        resetArrowsColor();                                                         // change color to grey
        setSelectedTable({});
        setShowTableDetails(false);
        setTableDetails([]);
        const index = mappings.indexOf(arrow);
        if (selectedMapping === {}) {                                               // no arrow is selected
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
     * @param sourceTable table from EHR database
     * @param targetTable table from OMOP CDM database
     */

    const createArrow = (sourceTable_id, targetTable_id) => {
        TableMappingService.addTableMapping(etl.id, sourceTable_id, targetTable_id).then(res => {
            const arrow = {
                id: res.data.id,
                start: res.data.source,
                end: res.data.target,
                complete: res.data.complete,
                color: defineArrowColor(res.data)
            }
            setMappings(mappings.concat(arrow));
        }).catch(err => {
            console.log(err);
        })
    }


    /**
     * Removes an arrow
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
     * 
     * @param {*} tableMappingId 
     * @param {*} completion 
     */

    const changeCompleteStatus = (tableMappingId, completion) => {
        mappings.forEach(map => {
            if (map.id === tableMappingId) {
                map.color = completion ? "black" : "grey"
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
        setEnableEditCommentButton(true);
        let tables = []
        if (sourceSelected) {
            TableService.changeSourceTableComment(selectedTable.id, selectedTable.comment).then(response => {
                etl.sourceDatabase.tables.forEach(item => {
                    if (item.id === response.data.id) {
                        tables = tables.concat(response.data)
                    } else {
                        tables = tables.concat(item)
                    }
                })
                etl.sourceDatabase.tables = tables;

                setEtl({
                    ...etl,
                    sourceDatabase: etl.sourceDatabase
                })
            }).catch(error => {
                console.log(error);
            });
        } else {
            TableService.changeTargetTableComment(selectedTable.id, selectedTable.comment).then(response => {
                etl.targetDatabase.tables.forEach(item => {
                    if (item.id === response.data.id) {
                        tables = tables.concat(response.data)
                    } else {
                        tables = tables.concat(item)
                    }
                })
                etl.targetDatabase.tables = tables;
                setEtl({
                    ...etl,
                    targetDatabase: etl.targetDatabase
                })
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

    const tablesConnected = (targetTable_id) => {
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
    const checkTable = e => {
        const targetTable_id = e.target.value[0];
        
        if (tablesConnected(targetTable_id)) {
            mappings.forEach(item => {
                if (item.end.id === targetTable_id && item.start.id === selectedTable.id) {
                    removeMapping(etl.id, item.id);
                }
            })
        } else {
            createArrow(selectedTable.id, targetTable_id);
        }
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

                            <Grid item xs={1} sm={1} md={1} lg={1}>
                                <Controls.Button variant="contained" size="medium" color="primary" text="Help " onClick={() => setShowHelpModal(true)}>
                                    <i className="fa fa-info"/>
                                </Controls.Button>
                                <HelpModal modalIsOpen={showHelpModal} closeModal={() => setShowHelpModal(false)}/>
                            </Grid>

                            { Object.keys(selectedMapping).length !== 0 ? (
                                <Grid item xs={3} sm={3} md={3} lg={3}>
                                    <Controls.Button 
                                        variant="contained" 
                                        size="medium" 
                                        color="secondary" 
                                        text="Remove" 
                                        onClick={removeTableMapping} 
                                    />
                                </Grid>
                            ) : (
                                <></>
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
                                            id={item.name} 
                                            table={item} 
                                            clicked={selectedTable.id === item.id}
                                            color={(Object.keys(selectedTable).length === 0 || selectedTable.id === item.id) ? '#FF9224' : '#FFD3A6'}
                                            border="#A10000"
                                            handleSelection={selectSourceTable} 
                                        />
                                    )
                                })}
                            </Grid>
                            
                            <Grid item xs={6} sm={6} md={6} lg={6}>                               
                                { etl.targetDatabase.tables.map(item => {
                                    return(
                                        <Controls.TooltipBox
                                            key={item.id} 
                                            id={item.name} 
                                            table={item} 
                                            clicked={item.id === selectedTable.id}
                                            color="#53ECEC"
                                            border="#000F73"
                                            handleSelection={selectTargetTable} 
                                        />
                                    )
                                })}
                            </Grid>
                            { mappings.map((ar, i) => (
                                <Xarrow key={i}
                                    start={ar.start.name}
                                    end={ar.end.name}
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
                                closeModal={() => setShowFieldMappingModal(false)}
                                mappingId={selectedMapping.id}
                                removeTableMapping={removeTableMapping}
                                changeMappingCompletion={changeCompleteStatus}
                            />
                        </Grid>
                    </Grid>

                    <Grid className={classes.tableDetails} item xs={6} sm={6} md={6} lg={6}>
                        { showTableDetails ? (
                            <div>
                                <h6><strong>Table: </strong>{selectedTable.name}</h6>

                                { sourceSelected ? (
                                    <h6><strong>Number of rows &gt;= </strong>{selectedTable.rowCount === null ? 0 : selectedTable.rowCount}</h6>
                                ) : (
                                    <>
                                    </>
                                ) }

                                <InfoTable columns={columns} data={tableDetails}/>
                                
                                <Controls.Input
                                    value={selectedTable.comment === null ? "" : selectedTable.comment}
                                    name="comment"
                                    disabled={enableEditCommentButton}
                                    fullWidth={true}
                                    label="Comment"
                                    placeholder="Edit table comment"
                                    rows={5}
                                    onChange={(e) => setSelectedTable({...selectedTable, comment: e.target.value })}
                                />
                                <Controls.Button
                                    className={enableEditCommentButton ? classes.showButton : classes.hiddenButton}
                                    text="Edit comment"
                                    size="medium"
                                    color="primary"
                                    variant="contained"
                                    onClick={() => setEnableEditCommentButton(false)}
                                />
                                <Controls.Button
                                    className={enableEditCommentButton ? classes.hiddenButton : classes.showButton}
                                    text="Save"
                                    size="medium"
                                    color="primary"
                                    variant="contained"
                                    onClick={saveComment}
                                />

                                <Controls.DropdownCheckbox
                                    value={[]}
                                    label="Connect to"
                                    options={etl.targetDatabase.tables}
                                    verifyMapping={tablesConnected}
                                    onChange={checkTable}
                                />                                
                            </div>
                        ) : (
                            <></>
                        )}
                    </Grid>
                </Grid>
            )}
        </div>
    )
}