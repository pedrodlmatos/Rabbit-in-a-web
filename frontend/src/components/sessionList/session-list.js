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
    }
}))

export default function SessionList() {
    const classes = useStyles();
    const [openModal, setOpenModal] = useState(false);
    const [loading, setLoading] = useState(false);
    const [sessions, setSessions] = useState({ });

    const [recordForEdit, setRecordForEdit] = useState(null);

    
    useEffect(() => {
        ETLService.getAllETL().then(response => {
            setSessions(response.data);
            setLoading(true);
        }).catch(response => {
            console.log(response);
        })
    }, []);


    const addOrEdit = (resetForm) => {
        resetForm();
        setRecordForEdit(null);
        setOpenModal(false);
    }
    


    return(
        <div>
            { loading ? 
                (
                    <div className={classes.pageContainer}>
                        <h1>ETL Sessions</h1>

                        <Grid container spacing={4}>
                            { sessions.map(session => 
                                <Grid key={session.id} item xs={12} sm={12} md={2} lg={2}>
                                    <SessionCard id={session.id} name={session.name} ehr={session.sourceDatabase.databaseName} omop={session.targetDatabase.databaseName}/>
                                </Grid>
                            )}
                        </Grid>

                        <Controls.Button variant="contained" size="medium" color="primary" text="Create ETL Session" onClick={() => {setOpenModal(true)}}></Controls.Button>
                        
                        <ETLModal title="Create ETL session" openModal={openModal} setOpenModal={setOpenModal}>
                            <CreateETLForm recordForEdit={recordForEdit} addOrEdit={addOrEdit}/>
                        </ETLModal>
                    </div>
                ) : (
                    <CircularProgress color="primary" variant="determinate" size={40} />
                )
            }
        </div>
    )
}