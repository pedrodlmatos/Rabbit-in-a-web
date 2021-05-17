import React, { useState, useEffect } from 'react';
import { makeStyles, Grid, CircularProgress, IconButton, Divider } from '@material-ui/core'
import AddIcon from '@material-ui/icons/Add';
import AttachFileIcon from '@material-ui/icons/AttachFile';
import ProcedureCard from "./procedure-card";
import ETLService from "../../../services/etl-list-service";
import Controls from '../../controls/controls';
import CreateETLForm from '../../forms/create-etl/create-new-etl-form';
import ETLModal from '../../modals/create-etl/etl-modal'
import CreateETLFromFileForm from '../../forms/create-etl/create-from-file-form'

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
        marginRight: theme.spacing(1)
    },
    divider: {
        marginLeft: "-1px"
    },
    iconButton: {
        width: "50px",
        height: "50px"
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
            window.location.href = '/procedure/' + res.data;
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
            window.location.href = '/procedure/' + res.data;
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
                    <h1 className={classes.title}>ETL Procedures</h1>
                    <Grid container spacing={4}>
                        { procedures.map(session =>
                            <Grid key={session.id} item xs={12} sm={12} md={2} lg={2}>
                                <ProcedureCard
                                    id={session.id}
                                    name={session.name}
                                    ehr={session.sourceDatabase.databaseName}
                                    omop={session.targetDatabase.databaseName}
                                />
                            </Grid>
                        )}
                    </Grid>

                    <Controls.Button
                        className={classes.button}
                        text="Create Procedure"
                        disabled={disabled}
                        onClick={() => {setShowETLCreationModal(true)}}
                    >
                        <AddIcon fontSize="large" />
                    </Controls.Button>

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