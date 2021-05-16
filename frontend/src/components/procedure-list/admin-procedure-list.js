import React, { useState, useEffect } from 'react';
import { makeStyles, Grid, CircularProgress } from '@material-ui/core'
import ProcedureCard from "./procedure-card";
import ETLService from "../../services/etl-list-service";

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


export default function AdminProcedureList() {

    const classes = useStyles();
    const [loading, setLoading] = useState(true);
    const [procedures, setProcedures] = useState({ });


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
                </div>
            )}
        </div>
    )
}