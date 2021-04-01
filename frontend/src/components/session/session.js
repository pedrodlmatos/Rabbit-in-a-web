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
    showFieldsInfo: {
        visibility: 'hidden'
    },
    hideFieldsInfo: {
        visibility: 'false'
    },
    fieldInfo: {
        marginLeft: theme.spacing(-20)
    },
    hiddenButton: {
        visibility: 'hidden'
    },
    showButton: {
        visibility: 'false'
    }
}))


export default function Session() {
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

    const classes = useStyles();
    const [loading, setLoading] = useState(true);
    const [etl, setEtl] = useState(initialETLValues);
    const [mappings, setMappings] = useState([]);
    const [selectedMapping, setSelectedMapping] = useState({});
    const [omopName, setOmopName] = useState('');
    const [showHelpModal, setShowHelpModal] = useState(false); 
    const [showFieldsInfo, setShowFieldsInfo] = useState(false);
    const [fieldsInfo, setFieldsInfo] = useState([]);
    const [selectedTable, setSelectedTable] = useState({})
    const [sourceSelected, setSourceSelected] = useState(false);
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
                    color: item.complete ? 'black' : 'grey'
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
     * Changes CDM database 
     *
     * @param e change OMOP CDM event
     */
    
    const handleCDMChange = e => {
        setLoading(true);
        // clean state
        if (selectedTable !== {}) {
            setSelectedTable({});
            setSourceSelected(false);
            setShowFieldsInfo(false);
            setFieldsInfo([]);
        }

        const cdm = e.target.value;
        ETLService.changeTargetDatabase(etl.id, cdm).then(response => {
            setEtl({...etl, targetDatabase: response.data.targetDatabase });
            setOmopName(CDMVersions.filter(function(cdm) { return cdm.id === response.data.targetDatabase.databaseName })[0].name);
            setMappings([]);
            setSelectedMapping({});
            setLoading(false);
        });
    }


    /**
     * Creates an arrow between a source table and a target table.
     *
     * @param sourceTable table from EHR database
     * @param targetTable table from OMOP CDM database
     */

    const createArrow = (sourceTable, targetTable) => {
        TableMappingService.addTableMapping(etl.id, sourceTable.id, targetTable.id).then(res => {
            const arrow = {
                id: res.data.id,
                start: sourceTable,
                end: targetTable,
                complete: res.data.complete,
                color: res.data.complete ? 'black' : 'grey'
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
        TableMappingService.removeTableMapping(etl.id, selectedMapping.id).then(res => {
            let maps = []
            res.data.forEach(function(item) {
                const arrow = {
                    id: item.id,
                    start: item.source,
                    end: item.target,
                    complete: item.complete,
                    color: item.complete ? "black" : "grey",
                }
                maps = maps.concat(arrow);
            });
            setSelectedMapping({});
            setMappings(maps);
        }).catch(res => {
            console.log(res);
        })
    }

    /**
     * Changes color from arrows that start in selected table and makes the
     * other lighter
     * 
     * @param {*} table selected table
     */

    const selectArrowsFromSource = (table) => {
        mappings.forEach(element => {
            if (element.start.name === table.name)
                element.color = 'orange';
            else
                element.color = 'lightgrey'
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
            if (element.end.name === table.name ? element.color = 'blue' : element.color = 'lightgrey');
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
        // change color to grey
        resetArrowsColor();
        const index = mappings.indexOf(arrow);
        if (selectedMapping === {}) {
            // no arrow is selected
            let arrows = mappings;
            arrows[index].color = "red";
            setSelectedMapping(arrow);
            setMappings(arrows);
        } else if(selectedMapping === arrow) {
            // select the arrow previous selected to unselect
            setSelectedMapping({});
            resetArrowsColor();
        } else {
            // select any other unselected arrow
            // unselect previous
            resetArrowsColor();
            // select a new one
            let arrows = mappings;
            arrows[index].color = "red";
            setSelectedMapping(arrow);
            setMappings(arrows);
        }
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
        setFieldsInfo(data);
        setShowFieldsInfo(true);
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
        setEnableEditCommentButton(true);        
        if (selectedTable === {}) {
            // all tables are unselected
            setSelectedTable(table);
            setSourceSelected(true);
            // change color of mappings that comes from the selected table
            selectArrowsFromSource(table);
            // define fields info
            defineData(table);
        } else if (selectedTable === table) {
            // select the same table
            // change color of arrows to grey
            resetArrowsColor();
            // unselect
            setSelectedTable({});
            setSourceSelected(false);
            setShowFieldsInfo(false);
            setFieldsInfo(null);
        } else {
            // select any other source table
            // change color of arrows to grey
            resetArrowsColor();
            // change select table information
            setSelectedTable(table);
            setSourceSelected(true);
            // change color of mappings that comes from the selected table
            selectArrowsFromSource(table);
            // change content of fields table
            defineData(table);
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
        if (selectedTable === {}) {
            // no table is selected
            // change color of mappings that goes to the selected table
            selectArrowsFromTarget(table);
            // change select table information
            setSelectedTable(table);
            setSourceSelected(false);
            // change content of fields table
            defineData(table);
        } else if (selectedTable === table) {
            // select the same table -> unselect
            // change color of arrows to grey
            resetArrowsColor();
            // unselect
            setSelectedTable({});
            setSourceSelected(false);
            setShowFieldsInfo(false);
            setFieldsInfo(null);
        } else if (sourceSelected) {
            // source table is selected -> create arrow
            // change arrows color to grey
            resetArrowsColor();
            // create arrow
            createArrow(selectedTable, table)
            // unselects tables
            setSelectedTable({});
            // clean state
            setSourceSelected(false);
            setShowFieldsInfo(false);
            setFieldsInfo(null);
        } else {
            // other target table is selected
            // change color of arrows to grey
            resetArrowsColor();
            // change select table information
            setSelectedTable(table);
            setSourceSelected(false);
            // change color of mappings that comes from the selected table
            selectArrowsFromTarget(table);
            // change content of fields table
            defineData(table);
        } 
    }


    /**
     * Changes state to close field mapping modal
     */

    const openFieldMappingModal = (mapping) => {
        setShowFieldMappingModal(true);
        setSelectedMapping(mapping);
    }


    /**
     * 
     */
    const saveComment = () => {
        setEnableEditCommentButton(true);

        if (sourceSelected) {
            TableService.changeSourceTableComment(selectedTable.id, selectedTable.comment).then(response => {
                let tables = []
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
                let tables = []
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


    return(
        <div>
            { loading ? (
                <CircularProgress color="primary" variant="indeterminate" size={40} />
            ) : (
                <div className={classes.tablesArea}>
                    <Grid container>
                        <Grid item xs={4} sm={4} md={4} lg={4}>
                            <h1>{ etl.name }</h1>
                        </Grid>

                        <Grid item xs={1} sm={1} md={1} lg={1}>
                            <Controls.Button variant="contained" size="medium" color="primary" text="Help " onClick={() => setShowHelpModal(true)}>
                                <i className="fa fa-info"/>
                            </Controls.Button>
                        </Grid>
                        <HelpModal modalIsOpen={showHelpModal} closeModal={() => setShowHelpModal(false)}/>

                        <Grid item xs={6} sm={6} md={6} lg={6}>
                            { showFieldsInfo && fieldsInfo !== [] ? (
                                <div>
                                    <h6><strong>Table: </strong>{selectedTable.name}</h6>

                                    <InfoTable columns={columns} data={fieldsInfo}/>
                                    <br />

                                    <Controls.Input 
                                        variant="outlined" 
                                        value={selectedTable.comment === null ? "" : selectedTable.comment}
                                        name="comment"
                                        disabled={enableEditCommentButton}
                                        fullWidth={true}
                                        label="Comment"
                                        placeholder="Edit table comment"
                                        rows={3}
                                        size="medium"
                                        type="string" 
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
                                </div>
                            ) : (
                                <></>
                            )}
                        </Grid>
                    </Grid>

                    <Grid className={classes.databaseNames} container>
                        <Grid item xs={3} sm={3} md={3} lg={3}>
                            <h4>{ etl.sourceDatabase.databaseName }</h4>
                        </Grid>
                        
                        <Grid item xs={3} sm={3} md={3} lg={3}>
                            <Controls.Select 
                                name={omopName} 
                                label="OMOP CDM" 
                                value={etl.targetDatabase.databaseName}
                                onChange={handleCDMChange}
                                options={CDMVersions} 
                            />
                        </Grid>

                        <Grid item xs={6} sm={6} md={6} lg={6}></Grid>
                    </Grid>

                    <Grid container>
                        <Grid item xs={3} sm={3} md={3} lg={3}>
                            <div>
                                { etl.sourceDatabase.tables.map(item => {
                                    return(
                                        <ElementBox
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
                            </div>
                        </Grid>
                            
                        <Grid item xs={3} sm={3} md={3} lg={3}>
                            <div>
                                { etl.targetDatabase.tables.map(item => {
                                    return(
                                        <ElementBox
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
                            </div>
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
                            changeMappingCompletion={changeCompleteStatus}/>
                        
                        <Grid item xs={6} sm={6} md={6} lg={6}></Grid>
                        
                        
                    </Grid>
                </div>
            )}
        </div>
    )
}