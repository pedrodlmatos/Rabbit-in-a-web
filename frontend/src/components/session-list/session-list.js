import React, { useState, useEffect } from 'react';
import { makeStyles, Grid, CircularProgress } from '@material-ui/core';
import AddIcon from '@material-ui/icons/Add';
import AttachFileIcon from '@material-ui/icons/AttachFile';
import SessionCard from "../sessionCard/session-card";
import ETLService from "../../services/etl-list-service";
import ETLModal from "../modals/create-etl-modal/etl-modal";
import Controls from '../controls/controls';
import CreateETLForm from '../forms/create-etl-form/create-etl-form';
import FileETLModal from '../modals/create-from-file/create-etl-from-file';
import FileETLForm from '../forms/create-from-file-form/create-from-file-form';

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
    }
}))


export default function SessionList() {
    const classes = useStyles();

    const [loading, setLoading] = useState(true);
    const [disabled, setDisabled] = useState(false);
    const [procedures, setProcedures] = useState({ });

    const [openCreateModal, setOpenCreateModal] = useState(false);
    const [openCreateFromFileModal, setOpenCreateFromFileModal] = useState(false);

    
    /**
     * Sends GET request to API to retrieve all ETL procedures
     */
    
    useEffect(() => {
        ETLService.getAllETL().then(response => {
            setProcedures(response.data);
            setLoading(false);
        }).catch(response => {
            console.log(response);
        })
    }, []);


    /**
     * Sends POST request to API to create a new ETL procedure
     * Disables buttons
     * 
     * @param {*} values form values (file and OMOP CDM)
     * @param {*} resetForm function to reset form
     */

    const createETLProcedure = (values, resetForm) => {
        // sends request to API and then redirects to created session
        ETLService.createETL(values.ehrName, values.ehrFile, values.omop).then(res => {
            window.location.href = '/session/' + res.data;
        }).then(res => {
            console.log(res);
        })

        setDisabled(true);
        resetForm();
    }


    /**
     * Creates ETL procedure from JSON file and redirects to page of created procedure
     *
     * @param values form values
     * @param resetForm function to reset from
     */
    const createETLProcedureFromJSONFile = (values, resetForm) => {
        // sends request to API and then redirects to created session
        ETLService.createETLFromFile(values.file).then(res => {
            window.location.href = '/session/' + res.data;
        }).catch(res => {
            console.log(res);
        })
        setDisabled(true);
        resetForm();
    }


    /**
     * Closes ETL procedure creation modal and reset its form
     * 
     * @param {*} resetForm function to reset form
     */

    const closeCreateModal = (resetForm) => {
        resetForm();
        setOpenCreateModal(false);
    }

    /**
     * Closes ETL procedure creation modal (with JSON file) and reset form
     *
     * @param resetForm function to reset form
     */

    const closeCreateFromFileModal = (resetForm) => {
        resetForm();
        setOpenCreateFromFileModal(false);
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
                                <SessionCard
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
                        text="Create ETL Session"
                        disabled={disabled}
                        onClick={() => {setOpenCreateModal(true)}}
                    >
                        <AddIcon fontSize="large"/>
                    </Controls.Button>

                    <ETLModal title="Create ETL session" openModal={openCreateModal} setOpenModal={setOpenCreateModal}>
                        <CreateETLForm addSession={createETLProcedure} close={closeCreateModal} />
                    </ETLModal>

                    <Controls.Button
                        className={classes.button}
                        text="Create from file"
                        disabled={disabled}
                        onClick={() => {setOpenCreateFromFileModal(true)}}
                    >
                        <AttachFileIcon fontSize="large" />
                    </Controls.Button>

                    <FileETLModal title="Create from file" openModal={openCreateFromFileModal} setOpenModal={setOpenCreateFromFileModal}>
                        <FileETLForm addSession={createETLProcedureFromJSONFile} close={closeCreateFromFileModal}/>
                    </FileETLModal>
                </div>
            )}
        </div>
    )
}