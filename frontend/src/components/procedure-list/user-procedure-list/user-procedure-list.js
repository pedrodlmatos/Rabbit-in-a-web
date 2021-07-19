import React, { useState, useEffect } from 'react';
import {
    makeStyles,
    Grid,
    CircularProgress,
    IconButton,
    Divider,
    Paper,
    Table,
    TableRow, TableBody, TableContainer, TableCell
} from '@material-ui/core'
import AddIcon from '@material-ui/icons/Add';
import AttachFileIcon from '@material-ui/icons/AttachFile';
import ETLService from "../../../services/etl-list-service";
import Controls from '../../controls/controls';
import CreateETLForm from '../../forms/create-etl/create-new-etl-form';
import ETLModal from '../../modals/create-etl/etl-modal'
import CreateETLFromFileForm from '../../forms/create-etl/create-from-file-form'
import { CDMVersions } from '../../../services/CDMVersions'

const useStyles = makeStyles(theme => ({
    pageContainer: {
        margin: theme.spacing(1),
        padding: theme.spacing(1)
    },
    title: {
        marginBottom: theme.spacing(5),
        fontSize: "12"
    },
    button: {
        marginLeft: theme.spacing(1)
    },
    divider: {
        marginLeft: "-1px"
    },
    iconButton: {
        width: "50px",
        height: "50px"
    },
    table: {
        maxHeight: 500,
        minWidth: 700,
        marginTop: theme.spacing(3),
        marginBottom: theme.spacing(5)
    }
}))


export default function UserProcedureList() {

    const classes = useStyles();
    const [loading, setLoading] = useState(true);
    const [disabled, setDisabled] = useState(false);
    const [procedures, setProcedures] = useState({ });
    const [showETLCreationModal, setShowETLCreationModal] = useState(false);
    const [showCreateNewETLModal, setShowCreateNewETLModal] = useState(false);
    const [showCreateETLFromFileModal, setShowCreateETLFromFileModal] = useState(false);



    /**
     * Sends GET request to API to retrieve all ETL procedures
     */

    useEffect(() => {
        ETLService.getUserETL().then(response => {
            setProcedures(response.data);
            setLoading(false);
        }).catch(response => {
            console.log(response);
        })
    }, []);


    /**
     * Closes the modal to choose type of creation and opens modal to create a new ETL procedure
     */

    const openCreateNewETLModal = () => {
        setShowETLCreationModal(false);
        setShowCreateNewETLModal(true);
    }


    /**
     * Closes the modal to choose type of creation and opens modal to create ETL procedure from JSON file
     */

    const openCreateETLFromFileModal = () => {
        setShowETLCreationModal(false);
        setShowCreateETLFromFileModal(true);
    }


    /**
     * Closes the creation modal and opens the method creation modal
     */

    const backToMethodSelection = () => {
        if (showCreateNewETLModal)
            setShowCreateNewETLModal(false);
        else if (showCreateETLFromFileModal)
            setShowCreateETLFromFileModal(false);

        setShowETLCreationModal(true);
    }


    /**
     * Sends POST request to API to create a new ETL procedure
     * Disables buttons
     *
     * @param {*} values form values (file and OMOP CDM)
     */

    const createNewETLProcedure = (values) => {
        // disables button
        setDisabled(true);
        // sends request to API and then redirects to created procedure
        ETLService.createETL(values.ehrName, values.ehrFile, values.omop).then(res => {
            window.location.href = '/procedure/' + res.data.id;
        }).catch(res => {
            console.log(res);
        })
    }


    /**
     * Creates ETL procedure from JSON file and redirects to page of created procedure
     *
     * @param values form values
     */

    const createETLProcedureFromJSONFile = (values) => {
        // disables button
        setDisabled(true);
        // sends request to API and then redirects to created procedure
        ETLService.createETLFromFile(values.file).then(res => {
            window.location.href = '/procedure/' + res.data.id;
        }).catch(res => {
            console.log(res);
        })
    }


    /**
     * Closes ETL procedure creation modal and reset its form
     *
     * @param {*} resetForm function to reset form
     */

    const closeCreateModal = (resetForm) => {
        resetForm();
        setShowCreateNewETLModal(false);
    }

    /**
     * Closes ETL procedure creation modal (with JSON file) and reset form
     *
     * @param resetForm function to reset form
     */

    const closeCreateFromFileModal = (resetForm) => {
        resetForm();
        setShowCreateETLFromFileModal(false);
    }


    return(
        <div className={classes.pageContainer}>
            { loading ? (
                <CircularProgress color="primary" variant="indeterminate" size={40} />
            ) : (
                <div>
                    <Grid container>
                        <Grid item xs={12} sm={6} md={3} lg={3}>
                            <h1 className={classes.title}>ETL Procedures</h1>
                        </Grid>

                        <Grid style={{ textAlign: "right"}} item xs={12} sm={6} md={9} lg={9}>
                            <Controls.Button
                                className={classes.button}
                                text="Create Procedure"
                                disabled={disabled}
                                onClick={() => {setShowETLCreationModal(true)}}
                            >
                                <AddIcon fontSize="large" />
                            </Controls.Button>
                        </Grid>
                    </Grid>


                    <TableContainer className={classes.table} component={Paper}>
                        <Table stickyHeader aria-label="customized table">
                            <TableBody>
                                {procedures.map((procedure, i) => {
                                    return(
                                        <TableRow key={i}>
                                            <TableCell component="th" scope="row" align="left">
                                                {procedure.name}
                                            </TableCell>

                                            <TableCell component="th" scope="row" align="left">
                                                {procedure.ehrDatabase.databaseName}
                                            </TableCell>

                                            <TableCell component="th" scope="row" align="left">
                                                {CDMVersions.filter(function(cdm) { return cdm.id === procedure.omopDatabase.databaseName })[0].name}
                                            </TableCell>

                                            <TableCell component="th" scope="row" align="left">
                                                {procedure.creationDate}
                                            </TableCell>

                                            <TableCell component="th" scope="row" align="left">
                                                {procedure.modificationDate}
                                            </TableCell>
                                            {/*
                                            <TableCell component="th" scope="row" align="left">
                                                {procedure.users.map((user, i) => { return(<div key={i}>{user.username}</div>)})}
                                            </TableCell>
                                            */}

                                            <TableCell component="th" scope="row" align="left">
                                                <Controls.Button
                                                    text="Access"
                                                    onClick={() => window.location.href = '/procedure/' + procedure.id}
                                                />
                                            </TableCell>
                                        </TableRow>
                                    )
                                })}
                            </TableBody>
                        </Table>
                    </TableContainer>

                    {/* Modal to choose ETL procedure creation method*/}
                    <ETLModal
                        title="Create ETL procedure"
                        show={showETLCreationModal}
                        setShow={setShowETLCreationModal}
                    >
                        <Grid container>
                            <Grid item xs={6} sm={6} md={6} lg={6} align="center">
                                <IconButton color="inherit" onClick={openCreateNewETLModal}>
                                    <AddIcon className={classes.iconButton} />
                                </IconButton>
                                <p>Create a new ETL procedure</p>
                            </Grid>
                            <Divider orientation="vertical" flexItem className={classes.divider} />
                            <Grid item xs={6} sm={6} md={6} lg={6} align="center">
                                <IconButton color="inherit" onClick={openCreateETLFromFileModal}>
                                    <AttachFileIcon className={classes.iconButton} />
                                </IconButton>
                                <p>Create from file</p>
                            </Grid>
                        </Grid>
                    </ETLModal>

                    {/* Modal to create a new ETL procedure */}
                    <ETLModal
                        title={"Create new ETL procedure"}
                        show={showCreateNewETLModal}
                        setShow={setShowCreateNewETLModal}
                    >
                        <CreateETLForm addSession={createNewETLProcedure} back={backToMethodSelection} close={closeCreateModal} />
                    </ETLModal>

                    {/* Modal to create ETL procedure from file */}
                    <ETLModal
                        title={"Create ETL procedure from file"}
                        show={showCreateETLFromFileModal}
                        setShow={setShowCreateETLFromFileModal}
                    >
                        <CreateETLFromFileForm addSession={createETLProcedureFromJSONFile} back={backToMethodSelection} close={closeCreateFromFileModal} />
                    </ETLModal>
                </div>
            )}
        </div>
    )
}