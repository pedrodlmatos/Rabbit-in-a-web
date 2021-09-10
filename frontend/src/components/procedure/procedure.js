import React, { useEffect, useState } from 'react'
import {
  Checkbox,
  CircularProgress,
  Divider,
  FormControlLabel,
  FormGroup,
  Grid,
  makeStyles,
  Menu,
  MenuItem,
  Switch
} from '@material-ui/core'
import EditIcon from '@material-ui/icons/Edit'
import SaveIcon from '@material-ui/icons/Save'
import ETLService from '../../services/etl-list-service'
import TableService from '../../services/table-service'
import TableMappingService from '../../services/table-mapping-service'
import FieldService from '../../services/field-service'
import FieldMappingService from '../../services/field-mapping-service'
import TableOperations from './table-operations'
import MappingOperations from '../utilities/mapping-operations'
import FilesMethods from './files-methods'
import ETLOperations from './etl-operations'
import { CDMVersions } from '../../services/CDMVersions'
import Controls from '../controls/controls'
import InviteCollaboratorModal from '../modals/collaborators/manage-collaborator'
import DeleteModal from '../modals/delete-modal/delete-modal'
import TableMappingPanel from './table-mapping-panel'
import EHRTableDetails from './ehr/ehr-table-details'
import OMOPTableDetails from './omop/omop-table-details'
import TableMappingLogic from './table-mapping-logic'
import FieldMappingPanel from './field-mapping-panel'
import EHRFieldDetails from './ehr/ehr-field-details'
import OMOPFieldDetails from './omop/omop-field-details'
import FieldMappingLogic from './field-mapping-logic'

const useStyles = makeStyles((theme) => ({
    container: {
        marginTop: theme.spacing(3),
        marginBottom: theme.spacing(3),
        marginRight: theme.spacing(6),
        marginLeft: theme.spacing(6)
    },
    iconButton: {
        border: "solid 0px #ffffff",
        color: "#000000",
        backgroundColor: "#ffffff",
        '&:hover': {
            color: "#000000",
            backgroundColor: "#ffffff",
        }
    },
    databaseName: {
        height: 100,
        alignItems: 'center',
    },
}))

export default function Procedure() {
    const classes = useStyles();

    const initialETLValues = {
        id: null, name: '',
        ehrDatabase: { id: null, tables: [], databaseName: '' },
        omopDatabase: { id: null, tables: [], databaseName: '' }
    }

    const columns = React.useMemo(() => [
        { Header: 'Field', accessor: 'field' },
        { Header: 'Type', accessor: 'type' },
        { Header: 'Description', accessor: 'description' }
    ], [])

    /* GENERAL ETL INFO */
    const [loading, setLoading] = useState(true);                                   // loading page
    const [etl, setEtl] = useState(initialETLValues);                                         // ETL data
    const [ETLUsers, setETLUsers] = useState([]);                                   // users with access (beside logged user)
    const [omopName, setOmopName] = useState('');                                   // OMOP CDM name according to its version
    const [tableMappings, setTableMappings] = useState([]);                         // list of mappings between tables

    /* SELECTED TABLE INFO */
    const [selectedTable, setSelectedTable] = useState({});                         // selected table info
    const [ehrTableSelected, setEhrTableSelected] = useState(false);                // flag if table from EHR database is selected
    const [showTableDetails, setShowTableDetails] = useState(false);                // flag to show selected table details
    const [tableDetails, setTableDetails] = useState([]);                           // table details (fields and their types and description)
    const [loadingSaveTableComment, setLoadingSaveTableComment] = useState(false);  // flag if selected table comment is being saved

    const [showTableMappingOptions, setShowTableMappingOptions] = useState(false);
    const [selectedTableMapping, setSelectedTableMapping] = useState({});
    const [loadingSaveTableMappingLogic, setLoadingSaveTableMappingLogic] = useState(false);

    const [fieldMappings, setFieldMappings] = useState([]);
    const [showFieldMappingPanel, setShowFieldMappingPanel] = useState(false);
    const [selectedField, setSelectedField] = useState({});
    const [ehrFieldSelected, setEhrFieldSelected] = useState(false);
    const [showFieldDetails, setShowFieldDetails] = useState(false);
    const [fieldDetails, setFieldDetails] = useState([]);
    const [loadingSaveFieldComment, setLoadingSaveFieldComment] = useState(false);
    const [selectedFieldMapping, setSelectedFieldMapping] = useState({});
    const [showFieldMappingOptions, setShowFieldMappingOptions] = useState(false);
    const [loadingSaveFieldMappingLogic, setLoadingSaveFieldMappingLogic] = useState(false);

    const [disableETLProcedureName, setDisableETLProcedureName] = useState(true);
    const [disableEHRDatabaseName, setDisableEHRDatabaseName] = useState(true);

    const [anchorEl, setAnchorEl] = useState(null);
    const [showInviteCollaboratorModal, setShowInviteCollaboratorModal] = useState(false);
    const [showDeleteModal, setShowDeleteModal] = useState(false);


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
                    omopDatabase: res.data.omopDatabase,
                });
                setOmopName(CDMVersions.filter(function(cdm) { return cdm.id === res.data.omopDatabase.databaseName })[0].name);

                // get users with access to ETL procedure (excluding the user in session)
                const username = JSON.parse(localStorage.getItem('user')).username;
                let users = [];
                res.data.users.forEach(user => {
                    if (user.username !== username) users.push(user);
                })
                setETLUsers(users);

                // table mappings
                let maps = [];
                res.data.tableMappings.forEach(function(item) {
                    const arrow = {
                        id: item.id,
                        start:  item.ehrTable,
                        end: item.omopTable,
                        complete: item.complete,
                        logic: item.logic,
                        color: item.complete ? "black" : "grey",
                        fieldMappings: item.fieldMappings,
                    }
                    maps.push(arrow);
                });
                setTableMappings(maps);
                setLoading(false);
            })
            .catch(res => { console.log(res) })
    }, []);


    /*********************************
     *                               *
     *  TABLE MAPPING PANEL METHODS  *
     *                               *
     *********************************/

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
     * Changes CDM database
     *
     * @param e event with new OMOP CDM version
     */

    const handleCDMChange = e => {
        setLoading(true);
        if (Object.keys(selectedTable).length > 0) {
            // clean state if any table is selected
            setSelectedTable({});
            setEhrTableSelected(false);
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
     * Invites a list of user to the ETL procedure
     *
     * @param usersList users list
     */

    const inviteCollaborators = (usersList) => {
        let usernames = []
        usersList.forEach(user => usernames.push(user.username));

        ETLService
            .inviteCollaboratorsToETL(usernames.toString(), etl.id)
            .then(response => {
                // get users with access to ETL procedure (excluding the user in session)
                const username = JSON.parse(localStorage.getItem('user')).username;
                let users = [];
                response.data.users.forEach(user => {
                    if (user.username !== username) users.push(user);
                })
                setETLUsers(users);
            })
    }


    /**
     * Removes a user from the list of users with access to the ETL procedure
     *
     * @param userToRemove user to remove
     */

    const removeCollaborator = (userToRemove) => {
        ETLService
            .removeUserFromCollaborators(userToRemove.username, etl.id)
            .then(response => {
                console.log(response.data)
                // get users with access to ETL procedure (excluding the user in session)
                const username = JSON.parse(localStorage.getItem('user')).username;
                let users = [];
                response.data.users.forEach(user => {
                    if (user.username !== username) users.push(user);
                })
                setETLUsers(users);
            })
    }


    /**
     * Selects a table and show its details
     *
     * @param show flag to show or hide
     * @param table selected table
     * @param tableInfo selected table details (fields and their data types and descriptions)
     * @param ehrTableSelected flag if selected table is from EHR database
     */

    const selectTable = (show, table, tableInfo, ehrTableSelected) => {
        if (show) {
            // table is selected
            setSelectedTable(table);
            setTableDetails(tableInfo);
            setEhrTableSelected(ehrTableSelected);
            setShowTableDetails(true);
        } else {
            // selected table is unselected
            setShowTableDetails(false);
            setSelectedTable({});
            setEhrTableSelected(false);
        }
    }

    /**
     *
     * @param ehrTableId
     * @param omopTableId
     */

    const tablesAreConnected = (ehrTableId, omopTableId) => {
        let connected = false;
        tableMappings.forEach(function (item) {
            if (item.start.id === ehrTableId && item.end.id === omopTableId) connected = true;
        })
        return connected;
    }


    /**
     * Creates an arrow between a source table and a target table.
     *
     * @param ehrTableId source table's id
     * @param omopTableId target table's id
     */

    const createTableMapping = (ehrTableId, omopTableId) => {
        // verify if table mapping between those tables already exists
        let exists = tablesAreConnected(ehrTableId, omopTableId);

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
                        color: MappingOperations.defineMappingColor(selectedTable, res.data.complete, res.data.ehrTable.id, res.data.omopTable.id),
                        fieldMappings: res.fieldMappings,
                    }
                    setTableMappings([arrow].concat(tableMappings));
                })
                .catch(err => { console.log(err); })
        }
    }


    /**
     * Verifies if a source table is connect to a target table
     *
     * @param omopTableId target table's id
     * @returns true if are connect, false otherwise
     */

    const connectedToOMOPTable = (omopTableId) => {
        return tablesAreConnected(selectedTable.id, omopTableId)
    }


    /**
     * Creates a table mapping between two tables or removes it if already exists
     *
     * @param e check event
     */

    const connectToOMOPTable = e => {
        const omopTableId = e.target.value[0];

        if (tablesAreConnected(selectedTable.id, omopTableId)) {
            tableMappings.forEach(item => {
                if (item.end.id === omopTableId && item.start.id === selectedTable.id)
                    removeMapping(etl.id, item.id);
            })
        } else
            createTableMapping(selectedTable.id, omopTableId);
    }


    /**
     * Verifies if a target table is connected to a source table
     *
     * @param ehrTableId source table id
     * @returns true if they are connected, false otherwise
     */

    const connectedToEHRTable = (ehrTableId) => {
        return tablesAreConnected(ehrTableId, selectedTable.id)
    }


    /**
     * Creates a table mapping between two tables or removes it if already exists
     *
     * @param e check event
     */

    const connectToEHRTable = e => {
        const ehrTableId = e.target.value[0];

        if (tablesAreConnected(ehrTableId, selectedTable.id)) {
            tableMappings.forEach(item => {
                if (item.start.id === ehrTableId && item.end.id === selectedTable.id)
                    removeMapping(etl.id, item.id);
            })
        } else
            createTableMapping(ehrTableId, selectedTable.id);
    }


    /**
     * Shows the edition table mapping options
     *
     * @param show flag to show or hide
     * @param tableMapping selected table mapping
     */

    const showTableMapping = (show, tableMapping) => {
        if (show) {
            setSelectedTableMapping(tableMapping);
            setShowTableMappingOptions(true);
        } else {
            setSelectedTableMapping({});
            setShowTableMappingOptions(false);
        }
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
     * Change the completion status of the table mapping
     */

    const handleCompletionChange = () => {
        TableMappingService
            .editCompleteMapping(selectedTableMapping.id, !selectedTableMapping.complete, etl.id)
            .then(response => {
                let index = tableMappings.findIndex(x => x.id === response.data.id);
                tableMappings[index].complete = response.data.complete;

                setSelectedTableMapping({
                    ...selectedTableMapping,
                    complete: response.data.complete
                })
            }).catch(res => { console.log(res) })
    }


    /**
     * Closes the field mapping modal and deletes the selected table mapping
     */

    const removeTableMapping = () => {
        // make request to API
        removeMapping(etl.id, selectedTableMapping.id);
        setShowTableMappingOptions(false);
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
     * Sends request to API to change the comment of a table from the EHR database
     */

    const saveEHRTableComment = () => {
        setLoadingSaveTableComment(true);
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

    const saveOMOPTableComment = () => {
        setLoadingSaveTableComment(true);
        TableService
            .changeOMOPTableComment(selectedTable.id, selectedTable.comment, etl.id)
            .then(response => {
                const index = etl.omopDatabase.tables.findIndex(x => x.id === response.data.id);
                etl.omopDatabase.tables[index].comment = response.data.comment;
                setLoadingSaveTableComment(false);
            }).catch(error => { console.log(error) });
    }


    /*********************************
     *                               *
     *  FIELD MAPPING PANEL METHODS  *
     *                               *
     *********************************/

    /**
     * Change the view for the Field Mapping panel
     *
     * @param tableMapping selected table mapping
     */

    const openFieldMappingPanel = (tableMapping) => {
        setSelectedTableMapping(tableMapping);
        setShowTableMappingOptions(true);
        setShowFieldMappingPanel(true);
        // defines field mappings
        let maps = [];
        tableMapping.fieldMappings.forEach(item => {
            const arrow = {
                id: item.id,
                start: item.ehrField,
                end: item.omopField,
                logic: item.logic,
                color: 'grey'
            }
            maps = maps.concat(arrow);
        })
        setFieldMappings(maps);
    }


    /**
     * Closes the field mapping panel, showing the table mapping panel and updating states
     */

    const closeFieldMappingPanel = () => {
        setShowTableMappingOptions(false);
        setShowFieldMappingPanel(false);
        setFieldMappings([]);
        setSelectedTableMapping({});
        setShowFieldDetails(false);
        setFieldDetails([]);
        setShowFieldMappingOptions(false);
    }


    /**
     *
     * @param show
     * @param field
     * @param fieldData
     * @param ehrField
     */

    const selectField = (show, field, fieldData, ehrField) => {
        if (show && ehrField) {
            // ehr field is selected
            setFieldDetails(fieldData);
            setSelectedField(field);
            setEhrFieldSelected(true);
            setShowFieldDetails(true);
        } else if (show && !ehrField) {
            // omop field is selected
            setFieldDetails(fieldData)
            setSelectedField(field);
            setEhrFieldSelected(false);
            setShowFieldDetails(true);
        } else {
            // table is unselected
            setShowFieldDetails(false);
            setSelectedField({});
            setFieldDetails(fieldData);
            setEhrFieldSelected(false);
        }
    }


    /**
     * Sends request to save comment of field from EHR database
     */

    const saveEHRFieldComment = () => {
        setLoadingSaveFieldComment(true);
        FieldService
            .changeEHRFieldComment(selectedField.id, selectedField.comment, etl.id)
            .then(response => {
                const index = selectedTableMapping.start.fields.findIndex(x => x.id === response.data.id);
                selectedTableMapping.start.fields[index].comment = response.data.comment;
                setLoadingSaveFieldComment(false);
            }).catch(error => { console.log(error) });
    }


    /**
     * Sends request to save comment of field from OMOP CDM database
     */

    const saveOMOPFieldComment = () => {
        FieldService
            .changeTargetFieldComment(selectedField.id, selectedField.comment, etl.id)
            .then(response => {
                const index = selectedTableMapping.end.fields.findIndex(x => x.id === response.data.id);
                selectedTableMapping.end.fields[index].comment = response.data.comment;
            }).catch(error => console.log(error));
    }


    /**
     * Sends request to API to create a mapping between two fields
     *
     * @param ehrFieldId source field id
     * @param omopFieldId target field id
     */

    const createFieldMapping = (ehrFieldId, omopFieldId) => {
        // verify if table mapping between those tables already exists
        let exists = false;
        fieldMappings.forEach(function (item) {
            if (item.start.id === ehrFieldId && item.end.id === omopFieldId) exists = true;
        })

        // if doesn't exist -> create
        if (!exists) {
            FieldMappingService
                .addFieldMapping(selectedTableMapping.id, ehrFieldId, omopFieldId, etl.id)
                .then(res => {
                    const arrow = {
                        id: res.data.id,
                        start: res.data.ehrField,
                        end: res.data.omopField,
                        logic: res.data.logic,
                        color: MappingOperations.defineMappingColor(selectedField, false, res.data.ehrField.id, res.data.omopField.id),
                    }
                    setFieldMappings(fieldMappings.concat(arrow));
                }).catch(res => { console.log(res) });
        }
    }


    /**
     * Verifies if a source field is connected to a target field
     *
     * @param omopFieldId target table's id
     * @returns true if are connected, false otherwise
     */

    const connectedToOMOPField = (omopFieldId) => {
        let result = false;
        fieldMappings.forEach(item => {
            if (item.end.id === omopFieldId && item.start.id === selectedField.id) result = true;
        })
        return result;
    }


    /**
     * Creates a field mapping between the selecte source field and the checked target field or removes it if already exists
     *
     * @param e check event with selected target field
     */

    const connectToOMOPField = e => {
        const omopFieldId = e.target.value[0];

        if (connectedToOMOPField(omopFieldId)) {
            fieldMappings.forEach(item => {
                if (item.end.id === omopFieldId && item.start.id === selectedField.id) removeFieldMapping(item.id);
            })
        } else {
            createFieldMapping(selectedField.id, omopFieldId);
        }
    }


    /**
     * Verifies if a target field is connected to a source field
     *
     * @param ehrFieldId source table id
     * @returns true if they are connected, false otherwise
     */

    const connectedToEHRField = (ehrFieldId) => {
        let result = false;
        fieldMappings.forEach(item => {
            if (item.start.id === ehrFieldId && item.end.id === selectedField.id) result = true;
        })
        return result;
    }


    /**
     * Creates a field mapping between the selected target field and the checked source field or removes it if already exists
     *
     * @param {*} e check event with checked source field
     */

    const connectToEHRField = e => {
        const ehrFieldId = e.target.value[0];

        if (connectedToEHRField(ehrFieldId)) {
            fieldMappings.forEach(item => {
                if (item.start.id === ehrFieldId && item.end.id === selectedField.id) removeFieldMapping(item.id);
            })
        } else
            createFieldMapping(ehrFieldId, selectedField.id);
    }


    /**
     *
     * @param show
     * @param fieldMapping
     */

    const showFieldMapping = (show, fieldMapping) => {
        if (show) {
            setSelectedFieldMapping(fieldMapping);
            setShowFieldMappingOptions(true);
        } else {
            setShowFieldMappingOptions(false);
            setSelectedFieldMapping({});
        }
    }


    /**
     * Makes a call to API to delete a field mapping and replace the previous with ones received
     *
     * @param fieldMappingId table mapping id
     */

    const removeFieldMapping = (fieldMappingId) => {
        FieldMappingService.removeFieldMapping(fieldMappingId, etl.id).then(() => {
            let maps = []
            fieldMappings.forEach(function(item) {
                if (item.id !== fieldMappingId)
                    maps = maps.concat(item);
            });
            setFieldMappings(maps);
        }).catch(res => {
            console.log(res);
        })
    }


    /**
     * Makes request to API to remove the selected field mapping
     */

    const removeSelectedFieldMapping = () => {
        FieldMappingService
            .removeFieldMapping(selectedFieldMapping.id, etl.id)
            .then(() => {
                const index = fieldMappings.findIndex(x => x.id === selectedFieldMapping.id);
                fieldMappings.splice(index);
                setSelectedFieldMapping({});
                setShowFieldMappingOptions(false);
            })
    }


    /**
     * Sends request to API to save field mapping logic
     */

    const saveFieldMappingLogic = () => {
        setLoadingSaveFieldMappingLogic(true);

        // make request to API
        FieldMappingService
            .editMappingLogic(selectedFieldMapping.id, selectedFieldMapping.logic, etl.id)
            .then(response => {
                let index = fieldMappings.findIndex(x => x.id === response.data.id);
                fieldMappings[index].logic = response.data.logic;
                setLoadingSaveFieldMappingLogic(false);
            }).catch(error => { console.log(error) });
    }



    return (
        <div className={classes.container}>
            { loading ? (
                <CircularProgress color="primary" variant="indeterminate" size={40} />
            ) : (
                <Grid container>
                    <Grid item xs={6} sm={6} md={6} lg={6}>
                        <Grid container>
                            {/* ETL procedure name */}
                            <Grid item xs={6} sm={6} md={6} lg={6}>
                                <Controls.Input
                                    label="ETL procedure name"
                                    placeholder="ETL procedure name"
                                    value={etl.name}
                                    size="small"
                                    disabled={disableETLProcedureName}
                                    onChange={e => setEtl({...etl, name: e.target.value})}
                                />
                                {disableETLProcedureName ? (
                                    <Controls.Button className={classes.iconButton} variant="outlined" color="inherit">
                                        <EditIcon onClick={() => setDisableETLProcedureName(false)} />
                                    </Controls.Button>
                                ) : (
                                    <Controls.Button className={classes.iconButton} variant="outlined" color="inherit">
                                        <SaveIcon onClick={() => ETLOperations.saveETLProcedureName(etl, setDisableETLProcedureName)} />
                                    </Controls.Button>
                                )}
                            </Grid>

                            {/* Options menu */}
                            <Grid item xs={6} sm={6} md={6} lg={6}>
                                <Controls.Button
                                    text="Options"
                                    aria-controls="simple-menu"
                                    aria-haspopup={true}
                                    onClick={(event) => setAnchorEl(event.currentTarget)}
                                />
                                <Menu
                                    id="simple-menu"
                                    anchorEl={anchorEl}
                                    keepMounted
                                    open={Boolean(anchorEl)}
                                    onClose={(event) => setAnchorEl(null)}
                                >
                                    {/* Stem Tables (add/remove) */}
                                    <MenuItem disabled={true}>
                                        Stem tables
                                        <Checkbox
                                            edge="end"
                                            checked={TableOperations.hasStemTable(etl.ehrDatabase.tables)}
                                            //onChange={TableOperations.hasStemTable(etl.ehrDatabase.tables) ? () => removeStemTable() : () => addStemTable()}
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
                                    <MenuItem onClick={() => setShowInviteCollaboratorModal(true)}>Invite collaborator</MenuItem>
                                    <InviteCollaboratorModal
                                        show={showInviteCollaboratorModal}
                                        setShow={setShowInviteCollaboratorModal}
                                        etlUsers={ETLUsers}
                                        invite={inviteCollaborators}
                                        remove={removeCollaborator}
                                    />

                                    <MenuItem style={{color: 'red'}} onClick={() => setShowDeleteModal(true)}>Delete procedure</MenuItem>
                                    <DeleteModal show={showDeleteModal} setShow={setShowDeleteModal} deleteProcedure={() => ETLOperations.deleteETLProcedure(etl)}/>
                                </Menu>
                            </Grid>
                        </Grid>

                        <Grid className={classes.databaseName} container>
                            {/* EHR database name */}
                            <Grid item xs={6} sm={6} md={6} lg={6}>
                                <Controls.Input
                                    label="EHR database name"
                                    placeholder="EHR database name"
                                    value={etl.ehrDatabase.databaseName}
                                    size="small"
                                    disabled={disableEHRDatabaseName}
                                    onChange={e => changeEHRDatabaseName(e)}
                                />
                                {disableEHRDatabaseName ? (
                                    <Controls.Button className={classes.iconButton} variant="outlined" color="inherit">
                                        <EditIcon onClick={() => setDisableEHRDatabaseName(false)} />
                                    </Controls.Button>
                                ) : (
                                    <Controls.Button className={classes.iconButton} variant="outlined" color="inherit">
                                        <SaveIcon onClick={() => ETLOperations.saveEHRDatabaseName(etl, setDisableEHRDatabaseName)} />
                                    </Controls.Button>
                                )}
                            </Grid>

                            {/* OMOP CDM dropdown*/}
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
                            {showFieldMappingPanel ? (
                                <FieldMappingPanel
                                    ehrTable={selectedTableMapping.start}
                                    omopTable={selectedTableMapping.end}
                                    complete={selectedTableMapping.complete}
                                    ehrFields={selectedTableMapping.start.fields}
                                    omopFields={selectedTableMapping.end.fields}
                                    fieldMappings={fieldMappings}
                                    selectField={selectField}
                                    createFieldMapping={createFieldMapping}
                                    showFieldMapping={showFieldMapping}
                                />
                            ) : (
                                <TableMappingPanel
                                    ehrTables={etl.ehrDatabase.tables}
                                    omopTables={etl.omopDatabase.tables}
                                    tableMappings={tableMappings}
                                    selectTable={selectTable}
                                    createTableMapping={createTableMapping}
                                    showTableMapping={showTableMapping}
                                    openFieldMappingPanel={openFieldMappingPanel}
                                />
                            )}
                        </Grid>
                    </Grid>

                    <Grid item xs={6} sm={6} md={6} lg={6}>
                        <Grid container>
                            {/* Remove table mapping button */}
                            <Grid item xs={3} sm={3} md={3} lg={3}>
                                {(showTableMappingOptions || showFieldMappingPanel) && (
                                    <Controls.Button
                                        color="secondary"
                                        text="Remove table mapping"
                                        onClick={removeTableMapping}
                                    />
                                )}
                            </Grid>

                            {/* Complete slider */}
                            <Grid item xs={3} sm={3} md={3} lg={3}>
                                {(showTableMappingOptions || showFieldMappingPanel) && (
                                    <FormGroup>
                                        <FormControlLabel
                                            control={<Switch checked={selectedTableMapping.complete} onChange={handleCompletionChange} color='primary'/>}
                                            label="Complete"
                                        />
                                    </FormGroup>
                                )}
                            </Grid>

                            {/* Remove field mapping button */}
                            <Grid item xs={3} sm={3} md={3} lg={3}>
                                {showFieldMappingPanel && showFieldMappingOptions && (
                                    <Controls.Button
                                        color="secondary"
                                        text="Remove Field Mapping"
                                        onClick={removeSelectedFieldMapping}
                                    />
                                )}
                            </Grid>

                            {/* Close/back to table mapping panel */}
                            <Grid item xs={3} sm={3} md={3} lg={3}>
                                {showFieldMappingPanel && (
                                    <Controls.Button
                                        color="inherit"
                                        text="Close"
                                        onClick={() => closeFieldMappingPanel()}
                                    />
                                )}
                            </Grid>
                        </Grid>

                        <Grid container>
                            <Grid item xs={12} sm={12} md={12} lg={12}>
                                {/* EHR table selected */}
                                {showTableDetails && ehrTableSelected && (
                                    <EHRTableDetails
                                        table={selectedTable}
                                        columns={columns}
                                        data={tableDetails}
                                        onChange={(e) => setSelectedTable({...selectedTable, comment: e.target.value })}
                                        disabled={loadingSaveTableComment}
                                        save={saveEHRTableComment}
                                        omopTables={etl.omopDatabase.tables}
                                        verify={connectedToOMOPTable}
                                        connect={connectToOMOPTable}
                                    />
                                )}

                                {/* OMOP table selected */}
                                {showTableDetails && !ehrTableSelected && (
                                    <OMOPTableDetails
                                        table={selectedTable}
                                        columns={columns}
                                        data={tableDetails}
                                        onChange={(e) => setSelectedTable({...selectedTable, comment: e.target.value })}
                                        disabled={loadingSaveTableComment}
                                        save={saveOMOPTableComment}
                                        ehrTables={etl.ehrDatabase.tables}
                                        verify={connectedToEHRTable}
                                        connect={connectToEHRTable}
                                    />
                                )}

                                {/* Table mapping logic */}
                                {showTableMappingOptions && (
                                    <div>
                                        <TableMappingLogic
                                            value={selectedTableMapping.logic === null ? '' : selectedTableMapping.logic}
                                            disabled={loadingSaveTableMappingLogic}
                                            onChange={(e) => setSelectedTableMapping({...selectedTableMapping, logic: e.target.value})}
                                            save={() => saveTableMappingLogic()}
                                        />

                                        {/* EHR field details*/}
                                        {showFieldDetails && ehrFieldSelected  && (
                                            <EHRFieldDetails
                                                field={selectedField}
                                                fieldInfo={fieldDetails}
                                                setFieldInfo={setFieldDetails}
                                                onCommentChange={(e) => setSelectedField({...selectedField, comment: e.target.value })}
                                                disabled={loadingSaveFieldComment}
                                                saveComment={saveEHRFieldComment}
                                                omopFields={selectedTableMapping.end.fields}
                                                verify={connectedToOMOPField}
                                                connect={connectToOMOPField}
                                            />
                                        )}

                                        {/* OMOP field details */}
                                        {showFieldDetails && !ehrFieldSelected && (
                                            <OMOPFieldDetails
                                                field={selectedField}
                                                fieldInfo={fieldDetails}
                                                setFieldInfo={setFieldDetails}
                                                onCommentChange={(e) => setSelectedField({...selectedField, comment: e.target.value })}
                                                saveComment={saveOMOPFieldComment}
                                                ehrFields={selectedTableMapping.start.fields}
                                                verify={connectedToEHRField}
                                                connect={connectToEHRField}
                                            />
                                        )}

                                        {/* Field mapping logic */}
                                        {showFieldMappingOptions && (
                                            <FieldMappingLogic
                                                value={selectedFieldMapping.logic}
                                                disabled={loadingSaveFieldMappingLogic}
                                                onChange={(e) => setSelectedFieldMapping({...selectedFieldMapping, logic: e.target.value})}
                                                save={saveFieldMappingLogic}
                                            />
                                        )}
                                    </div>
                                )}
                            </Grid>
                        </Grid>
                    </Grid>
                </Grid>
            )}
        </div>
    )
}