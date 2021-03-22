import React, { useState, useEffect } from 'react';
import { makeStyles, Grid, CircularProgress } from '@material-ui/core';
import SessionCard from "../sessionCard/session-card";
import ETLService from "../../services/etl-list-service";
import ETLModal from "../modals/createETLModal/etl-modal";
import Controls from '../controls/controls';
import CreateETLForm from '../forms/create-etl-form/create-etl-form';

const useStyles = makeStyles(theme => ({
    pageContainer: {
        margin: theme.spacing(1),
        padding: theme.spacing(1)
    },
    title: {
        marginBottom: theme.spacing(5),
        fontSize: "12"
    }
}))


export default function SessionList() {
    const classes = useStyles();
    const [openModal, setOpenModal] = useState(false);
    const [loading, setLoading] = useState(false);
    const [disabled, setDisabled] = useState(false);
    const [sessions, setSessions] = useState({ });

    
    /**
     * Sends GET request to API to retrieve all ETL sessions
     */
    
    useEffect(() => {
        ETLService.getAllETL().then(response => {
            setSessions(response.data);
            setLoading(true);
        }).catch(response => {
            console.log(response);
        })
    }, []);


    /**
     * Sends POST request to API to create a new ETL session
     * Disables buttons
     * 
     * @param {*} values form values (file and OMOP CDM)
     * @param {*} resetForm function to reset form
     */

    const createETLSession = (values, resetForm) => {
        // sends request to API and then redirects to created session
        ETLService.createETL(values.ehrFile, values.omop).then(res => {
            window.location.href = '/session/' + res.data.id;
        }).then(res => {
            console.log(res);
        })

        setDisabled(true);
        resetForm();
    }


    /**
     * Closes ETL session creation modal and reset its form
     * 
     * @param {*} resetForm function to reset form
     */

    const closeModal = (resetForm) => {
        resetForm();
        setOpenModal(false);
    }
    


    return(
        <div className={classes.pageContainer}>
            { loading ? (
                <div>
                    <h1 className={classes.title}>ETL Sessions</h1>

                    <Grid container spacing={4}>
                        { sessions.map(session => 
                            <Grid key={session.id} item xs={12} sm={12} md={2} lg={2}>
                                <SessionCard id={session.id} name={session.name} ehr={session.sourceDatabase.databaseName} omop={session.targetDatabase.databaseName}/>
                            </Grid>
                        )}
                    </Grid>

                    <Controls.Button 
                        variant="contained" 
                        size="medium" 
                        color="primary" 
                        text="Create ETL Session"
                        disabled={disabled}
                        onClick={() => {setOpenModal(true)}} />
                    
                    <ETLModal title="Create ETL session" openModal={openModal} setOpenModal={setOpenModal}>
                        <CreateETLForm addSession={createETLSession} close={closeModal} />
                    </ETLModal>
                </div>
            ) : (
                <CircularProgress color="primary" variant="indeterminate" size={40} />
            )}
        </div>
    )
}