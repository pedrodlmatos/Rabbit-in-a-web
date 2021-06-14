import { Grid, CircularProgress, makeStyles, Menu, MenuItem, Checkbox, Divider } from '@material-ui/core'
import React, { useState, useEffect } from 'react'
import Xarrow from 'react-xarrows/lib';
import ETLService from '../../services/etl-list-service';
import TableService from '../../services/table-service';
import TableMappingService from '../../services/table-mapping-service';
import Controls from '../controls/controls';
import FieldMappingModal from '../modals/field-mapping-modal/field-mapping-modal';
import { CDMVersions } from '../../services/CDMVersions';
import TableMappingLogic from './table-mapping-logic';
import SourceTableDetails from './source-table-details';
import TargetTableDetails from './target-table-details';
import FilesMethods from './files-methods';
import TableOperations from './table-operations';
import MappingOperations from '../utilities/mapping-operations';
import DeleteModal from '../modals/delete-modal/delete-modal';

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
        ehrDatabase: { id: null, tables: [], databaseName: null },
        omopDatabase: { id: null, tables: [], databaseName: '' }
    }

    const columns = React.useMemo(() => [
        { Header: 'Field', accessor: 'field' },
        { Header: 'Type', accessor: 'type' },
        { Header: 'Description', accessor: 'description' }
    ], [])

    const [loading, setLoading] = useState(true);
    const [etl, setEtl] = useState(initialETLValues);
    const [disableETLProcedureName, setDisableETLProcedureName] = useState(true);
    const [disableEHRDatabaseName, setDisableEHRDatabaseName] = useState(true);
    const [omopName, setOmopName] = useState('');
    const [tableMappings, setTableMappings] = useState([]);
    const [selectedTableMapping, setSelectedTableMapping] = useState({});
    
    const [selectedTable, setSelectedTable] = useState({})
    const [sourceSelected, setSourceSelected] = useState(false);
    const [showTableDetails, setShowTableDetails] = useState(false);
    const [tableDetails, setTableDetails] = useState([]);
    const [loadingSaveTableComment, setLoadingSaveTableComment] = useState(false);

    // show/hide modals
    const [anchorEl, setAnchorEl] = useState(null);
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [showFieldMappingModal, setShowFieldMappingModal] = useState(false);
    const [loadingSaveTableMappingLogic, setLoadingSaveTableMappingLogic] = useState(false);
    
    useEffect(() => {
        const etlProcedureId = window.location.pathname.toString().replace("/procedure/", "");

        // make request to API
        ETLService
            .getETLById(etlProcedureId)
            .then(res => {
                setEtl({
                    id: res.data.id,
                    name: res.data.name,
                    ehrDatabase: res.data.ehrDatabase,
                    omopDatabase: res.data.omopDatabase
                });
                setOmopName(CDMVersions.filter(function(cdm) { return cdm.id === res.data.omopDatabase.databaseName })[0].name);
                // table mappings
                let maps = [];
                res.data.tableMappings.forEach(function(item) {
                    const arrow = {
                        id: item.id,
                        start:  item.ehrTable,
                        end: item.omopTable,
                        complete: item.complete,
                        logic: item.logic,
                        color: item.complete ? "black" : "grey"
                    }
                    maps.push(arrow);
                });
                setTableMappings(maps);
                setLoading(false);
            })
            .catch(res => { console.log(res) })
    }, []);


    /**
     * Sends request to API to change ETL procedure name and after receiving response,
     * disables input to change
     */

    const saveETLProcedureName = () => {
        ETLService
            .changeETLProcedureName(etl.id, etl.name)
            .then(() => {
                setDisableETLProcedureName(true);
            })
            .catch(e => console.log(e));
    }


    /**
     * Changes the name of the EHR database
     *
     * @param e change event
     */

    const changeEHRDatabaseName = e => {
        let newSourceDatabase = {...etl.ehrDatabase, databaseName: e.target.value}
        setEtl({...etl, ehrDatabase: newSourceDatabase});
    }

    /**
     * Sends request to API to change EHR database name and after receiving response,
     * disables input to change
     */

    const saveEHRDatabaseName = () => {
        ETLService
            .changeEHRDatabaseName(etl.ehrDatabase.id, etl.id, etl.ehrDatabase.databaseName)
            .then(() => {
                setDisableEHRDatabaseName(true);
            })
            .catch(e => console.log(e));
    }


    /**
     * Changes CDM database
     *
     * @param e event with new OMOP CDM version
     */

    const handleCDMChange = e => {
        setLoading(true);
        if (Object.keys(selectedTable).length > 0) {
            // clean state if any table is selected
            setSelectedTable({});
            setSourceSelected(false);
            setShowTableDetails(false);
            setTableDetails([]);
        }
        if (Object.keys(selectedTableMapping).length > 0) {
            // clean state if any table mapping is selected
            setSelectedTableMapping({});
        }

        // make API request
        ETLService
            .changeTargetDatabase(etl.id, e.target.value)
            .then(response => {
                setEtl({...etl, omopDatabase: response.data.omopDatabase });
                setOmopName(CDMVersions.filter(function(cdm) { return cdm.id === response.data.omopDatabase.databaseName })[0].name);
                setTableMappings([]);
                setLoading(false);
            }).catch(error => console.log(error));
    }


    /**
     * Delete (mark as deleted) the open ETL procedure and redirect to the user's
     * ETL procedures list page
     */

    const deleteETLProcedure = () => {
        ETLService.markETLProcedureAsDeleted(etl.id).then(() => { window.location.href = '/procedures' });
    }


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
        setSelectedTableMapping({});
        if (Object.keys(selectedTable).length === 0) {
            // all tables are unselected
            setSelectedTable(ehrTable);
            setSourceSelected(true);
            MappingOperations.selectMappingsFromSource(tableMappings, ehrTable);        // change color of mappings that comes from the selected table
            defineData(ehrTable);                                                       // define fields info
        } else if (selectedTable === ehrTable) {
            // select the same table -> unselect
            MappingOperations.resetMappingColor(tableMappings);                            // change color of arrows to grey
            setSourceSelected(false);                                                // unselect
            setShowTableDetails(false);
            setSelectedTable({});
            setTableDetails(null);
        } else {
            // select any other source table
            MappingOperations.resetMappingColor(tableMappings);                            // change color of arrows to grey
            setSelectedTable(ehrTable);                                                 // change selected table
            setSourceSelected(true);
            MappingOperations.selectMappingsFromSource(tableMappings, ehrTable);        // change color of mappings that comes from the selected table
            defineData(ehrTable);                                                       // change content of fields table
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
        setSelectedTableMapping({});
        if (Object.keys(selectedTable).length === 0) {
            // no table is selected
            MappingOperations.selectMappingsToTarget(tableMappings, omopTable);          // change color of mappings that goes to the selected table
            setSelectedTable(omopTable);                                                 // change select table information
            setSourceSelected(false);
            defineData(omopTable);                                                       // change content of fields table
        } else if (selectedTable === omopTable) {
            // select the same table -> unselect
            MappingOperations.resetMappingColor(tableMappings);                            // change color of arrows to grey
            setSourceSelected(false);
            setShowTableDetails(false);
            setSelectedTable({});                                                     // unselect
            setTableDetails(null);
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
            MappingOperations.resetMappingColor(tableMappings);                             // change color of arrows to grey
            setSelectedTable(omopTable);                                                  // change select table information
            setSourceSelected(false);
            MappingOperations.selectMappingsToTarget(tableMappings, omopTable);           // change color of mappings that comes from the selected table
            defineData(omopTable);                                                        // change content of fields table
        }
    }


    /**
     * Creates the stem tables on both source (EHR) and target (OMOP CDM) databases
     */
        // TODO: verify
    const addStemTable = () => {
        ETLService.addStemTables(etl.id).then(response => {
            setEtl({
                ...etl,
                sourceDatabase: response.data.sourceDatabase,
                targetDatabase: response.data.targetDatabase
            });
            // table mappings
            let maps = [];
            response.data.tableMappings.forEach(function(item) {
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
            setTableMappings(maps);
        })
    }


    /**
     * Removes stem table from both databases
     */
    // TODO: verify
    const removeStemTable = () => {
        ETLService.removeStemTables(etl.id).then(response => {
            console.log(response.data);
            setEtl({
                ...etl,
                sourceDatabase: response.data.sourceDatabase,
                targetDatabase: response.data.targetDatabase
            });
            // table mappings
            let maps = [];
            response.data.tableMappings.forEach(function(item) {
                const arrow = {
                    id: item.id,
                    start:  item.ehrTable,
                    end: item.omopTable,
                    complete: item.complete,
                    logic: item.logic,
                    color: item.complete ? "black" : "grey"
                }
                maps.push(arrow);
            });
            setTableMappings(maps);
        })
    }


    /**
     * Creates an arrow between a source table and a target table.
     *
     * @param ehrTableId source table's id
     * @param omopTableId target table's id
     */

    const createTableMapping = (ehrTableId, omopTableId) => {
        // verify if table mapping between those tables already exists
        let exists = false;
        tableMappings.forEach(function (item) {
            if (item.start.id === ehrTableId && item.end.id === omopTableId) exists = true;
        })

        // if doesn't exist -> create
        if (!exists) {
            TableMappingService
                .addTableMapping(etl.id, ehrTableId, omopTableId)
                .then(res => {
                    const arrow = {
                        id: res.data.id,
                        start: res.data.ehrTable,
                        end: res.data.omopTable,
                        complete: res.data.complete,
                        logic: res.data.logic,
                        color: MappingOperations.defineMappingColor(selectedTable, res.data.complete, res.data.ehrTable.id, res.data.omopTable.id)
                    }
                    setTableMappings([arrow].concat(tableMappings));
                })
                .catch(err => { console.log(err); })
        }
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
        setSelectedTable({});
        setSourceSelected(false);
        setShowTableDetails(false);
        setTableDetails([]);
        const index = tableMappings.indexOf(tableMapping);

        if (Object.keys(selectedTableMapping).length === 0) {
            // no arrow is selected
            let mappings = tableMappings;
            mappings[index].color = "red";                                              // change to red the selected table mapping
            setSelectedTableMapping(tableMapping);
            setTableMappings(mappings);
        } else if(selectedTableMapping === tableMapping) {
            // select the arrow previous selected to unselect
            setSelectedTableMapping({});
            MappingOperations.resetMappingColor(tableMappings);                         // change color of arrows to grey
        } else {
            // select any other unselected arrow
            MappingOperations.resetMappingColor(tableMappings);                         // change color of arrows to grey
            let mappings = tableMappings;                                               // select a new one
            mappings[index].color = "red";                                              // change to red the selected table mapping
            setSelectedTableMapping(tableMapping);
            setTableMappings(mappings);
        }
    }


    /**
     * Closes the field mapping modal and deletes the selected table mapping
     */

    const removeTableMapping = () => {
        // close field mapping modal
        setShowFieldMappingModal(false);
        // make request to API
        removeMapping(etl.id, selectedTableMapping.id);
        setSelectedTableMapping({});
    }


    /**
     * Makes a call to API to delete a table mapping and removes the one deleted
     * 
     * @param etl_id ETL procedure's
     * @param tableMappingId table mapping id
     */

    const removeMapping = (etl_id, tableMappingId) => {
        TableMappingService
            .removeTableMapping(etl_id, tableMappingId)
            .then(() => {
                let mappings = []
                tableMappings.forEach(function(item) {
                    if (item.id !== tableMappingId)
                        mappings = mappings.concat(item);
                });
                setTableMappings(mappings);
            }).catch(res => { console.log(res) })
    }


    /**
     * Changes state to close field mapping modal
     *
     * @param tableMapping selected table mapping
     */

    const openFieldMappingModal = (tableMapping) => {
        setSelectedTableMapping(tableMapping);
        setShowFieldMappingModal(true);
    }


    /**
     * Closes the field mapping modal and cleans state
     */

    const closeFieldMappingModal = () => {
        setShowFieldMappingModal(false)
        setSelectedTableMapping({});
    }


    /**
     * Changes table mapping color according to its completion status
     *
     * @param tableMappingId table mapping's id
     * @param completion table mapping completion status
     */

    const changeTableMappingCompletionStatus = (tableMappingId, completion) => {
        tableMappings.forEach(map => {
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
     * Save table comment
     */

    const saveComment = () => {
        setLoadingSaveTableComment(true);
        sourceSelected ? saveCommentEHRTable() : saveCommentTargetTable();
    }


    /**
     * Sends request to API to change the comment of a table from the EHR database
     */

    const saveCommentEHRTable = () => {
        TableService
            .changeEHRTableComment(selectedTable.id, selectedTable.comment, etl.id)
            .then(response => {
                const index = etl.ehrDatabase.tables.findIndex(x => x.id === response.data.id);
                etl.ehrDatabase.tables[index].comment = response.data.comment;
                setLoadingSaveTableComment(false);
            }).catch(error => { console.log(error) });
    }


    /**
     * Sends request to API to change the comment of a table from the OMOP CDM database
     */

    const saveCommentTargetTable = () => {
        TableService
            .changeOMOPTableComment(selectedTable.id, selectedTable.comment, etl.id)
            .then(response => {
                const index = etl.omopDatabase.tables.findIndex(x => x.id === response.data.id);
                etl.omopDatabase.tables[index].comment = response.data.comment;
                setLoadingSaveTableComment(false);
            }).catch(error => { console.log(error) });
    }


    /**
     * Verifies if a source table is connect to a target table
     *
     * @param targetTableId target table's id
     * @returns true if are connect, false otherwise
     */

    const connectedToTargetTable = (targetTableId) => {
        let result = false;
        tableMappings.forEach(item => {
            if (item.end.id === targetTableId && item.start.id === selectedTable.id) result = true
        })
        return result;
    }


    /**
     * Creates a table mapping between two tables or removes it if already exists
     * 
     * @param e check event
     */

    const connectToTargetTable = e => {
        const targetTableId = e.target.value[0];

        if (connectedToTargetTable(targetTableId)) {
            tableMappings.forEach(item => {
                if (item.end.id === targetTableId && item.start.id === selectedTable.id)
                    removeMapping(etl.id, item.id);
            })
        } else
            createTableMapping(selectedTable.id, targetTableId);
    }


    /**
     * Verifies if a target table is connected to a source table
     *
     * @param sourceTableId source table id
     * @returns true if they are connected, false otherwise
     */
    
    const connectedToSourceTable = (sourceTableId) => {
        let result = false;
        tableMappings.forEach(item => {
            if (item.start.id === sourceTableId && item.end.id === selectedTable.id) result = true
        })
        return result;
    }


    /**
     * Creates a table mapping between two tables or removes it if already exists
     * 
     * @param e check event
     */

    const connectToSourceTable = e => {
        const sourceTableId = e.target.value[0];

        if (connectedToSourceTable(sourceTableId)) {
            tableMappings.forEach(item => {
                if (item.start.id === sourceTableId && item.end.id === selectedTable.id)
                    removeMapping(etl.id, item.id);
            })
        } else
            createTableMapping(sourceTableId, selectedTable.id);
    }


    /**
     * Saves changed table mapping logic
     */

    const saveTableMappingLogic = () => {
        setLoadingSaveTableMappingLogic(true);
        // make request to API
        TableMappingService
            .editMappingLogic(selectedTableMapping.id, selectedTableMapping.logic, etl.id)
            .then(response => {
                let index = tableMappings.findIndex(x => x.id === response.data.id);
                tableMappings[index].logic = response.data.logic;
                setLoadingSaveTableMappingLogic(false);
            }).catch(error => { console.log(error) });
    }


    /**
     * Updates table mapping logic when field mapping modal is open
     *
     * @param tableMappingId table mapping id
     * @param logic logic to update to
     */

    const updateTableMappingLogic = (tableMappingId, logic) => {
        let index = tableMappings.findIndex(x => x.id === tableMappingId);
        tableMappings[index].logic = logic;
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
                                <Controls.Input
                                    label="ETL procedure name"
                                    placeholder="ETL procedure name"
                                    value={etl.name}
                                    size="small"
                                    disabled={disableETLProcedureName}
                                    onChange={e => setEtl({...etl, name: e.target.value})}
                                />
                                <Controls.Button
                                    text={disableETLProcedureName ? "Edit" : "Save"}
                                    size="small"
                                    onClick={disableETLProcedureName ? () => setDisableETLProcedureName(false) : () => saveETLProcedureName()}
                                />
                            </Grid>

                            {/* Menu (with files, add/remove stem tables) */}
                            <Grid item xs={2} sm={2} md={2} lg={2}>
                                <Controls.Button text="Menu" aria-controls="simple-menu" aria-haspopup={true} onClick={(event) => setAnchorEl(event.currentTarget)} />
                                <Menu id="simple-menu" anchorEl={anchorEl} keepMounted open={Boolean(anchorEl)} onClose={(event) => setAnchorEl(null)}>
                                    {/* Stem Tables (add/remove) */}
                                    <MenuItem>
                                        Stem tables
                                        <Checkbox
                                            edge="end"
                                            checked={TableOperations.hasStemTable(etl.ehrDatabase.tables)}
                                            onChange={TableOperations.hasStemTable(etl.ehrDatabase.tables) ? () => removeStemTable() : () => addStemTable()}
                                        />
                                    </MenuItem>
                                    <Divider />
                                    {/*  */}
                                    <MenuItem onClick={() => FilesMethods.fetchSourceFieldsFile(etl.id)}>Source fields list</MenuItem>
                                    <MenuItem onClick={() => FilesMethods.fetchTargetFieldsFile(etl.id)}>Target field list</MenuItem>
                                    <MenuItem onClick={() => FilesMethods.fetchSummaryFile(etl.id)}>Summary</MenuItem>
                                    <Divider />
                                    <MenuItem onClick={() => FilesMethods.fetchSaveFile(etl.id)}>Save session to file</MenuItem>
                                    <Divider />
                                    <MenuItem style={{color: 'red'}} onClick={() => setShowDeleteModal(true)}>Delete procedure</MenuItem>
                                    <DeleteModal show={showDeleteModal} setShow={setShowDeleteModal} deleteProcedure={() => deleteETLProcedure()}/>
                                </Menu>
                            </Grid>

                            {/* If a table mapping is selected, allow to remove */}
                            { Object.keys(selectedTableMapping).length !== 0 && (
                                <Grid item xs={2} sm={2} md={2} lg={2}>
                                    <Controls.Button
                                        color="secondary"
                                        text="Remove"
                                        onClick={removeTableMapping}
                                    />
                                </Grid>
                            )}
                        </Grid>
                            
                        <Grid className={classes.databaseNames} container>
                            <Grid item xs={6} sm={6} md={6} lg={6}>
                                <Controls.Input
                                    label="EHR database name"
                                    placeholder="EHR database name"
                                    value={etl.ehrDatabase.databaseName}
                                    size="small"
                                    disabled={disableEHRDatabaseName}
                                    onChange={e => changeEHRDatabaseName(e)}
                                />
                                <Controls.Button
                                    text={disableEHRDatabaseName ? "Edit" : "Save"}
                                    size="small"
                                    onClick={disableEHRDatabaseName ? () => setDisableEHRDatabaseName(false) : () => saveEHRDatabaseName()}
                                />
                            </Grid>
                            
                            <Grid item xs={6} sm={6} md={6} lg={6}>
                                <Controls.Select 
                                    name={omopName} 
                                    label="OMOP CDM" 
                                    value={etl.omopDatabase.databaseName}
                                    onChange={handleCDMChange}
                                    options={CDMVersions} 
                                />
                            </Grid>
                        </Grid>

                        <Grid container>
                            <Grid item xs={6} sm={6} md={6} lg={6}>                                
                                { etl.ehrDatabase.tables.map(item => {
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
                            
                            <Grid item xs={6} sm={6} md={6} lg={6}>                               
                                { etl.omopDatabase.tables.map(item => {
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
                                        onDoubleClick: () => openFieldMappingModal(ar)
                                    }}
                                />
                            ))}
                            <FieldMappingModal 
                                openModal={showFieldMappingModal}
                                closeModal={closeFieldMappingModal}
                                etl_id={etl.id}
                                tableMappingId={selectedTableMapping.id}
                                removeTableMapping={removeTableMapping}
                                changeMappingCompletion={changeTableMappingCompletionStatus}
                                updateTableMappingLogic={updateTableMappingLogic}
                            />
                        </Grid>
                    </Grid>

                    <Grid className={classes.tableDetails} item xs={6} sm={6} md={6} lg={6}>
                        { showTableDetails && (
                            <div>
                                { sourceSelected ? (
                                    <SourceTableDetails
                                        table={selectedTable}
                                        columns={columns}
                                        data={tableDetails}
                                        onChange={(e) => setSelectedTable({...selectedTable, comment: e.target.value })}
                                        disabled={loadingSaveTableComment}
                                        save={saveComment}
                                        omopTables={etl.omopDatabase.tables}
                                        verify={connectedToTargetTable}
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
                                        ehrTables={etl.ehrDatabase.tables}
                                        verify={connectedToSourceTable}
                                        connect={connectToSourceTable}
                                    />
                                    
                                ) }
                            </div>
                        )}

                        { Object.keys(selectedTableMapping).length !== 0 && (
                            <TableMappingLogic
                                value={selectedTableMapping.logic === null ? '' : selectedTableMapping.logic}
                                disabled={loadingSaveTableMappingLogic}
                                onChange={(e) => setSelectedTableMapping({...selectedTableMapping, logic: e.target.value})}
                                save={() => saveTableMappingLogic()}
                            />
                        )}
                    </Grid>
                </Grid>
            )}
        </div>
    )
}