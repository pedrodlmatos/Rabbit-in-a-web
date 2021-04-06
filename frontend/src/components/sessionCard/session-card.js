import React from "react";
import { makeStyles, Card, CardActions, CardContent, Button, Typography } from '@material-ui/core';
import { CDMVersions } from "../../services/CDMVersions";


const useStyles = makeStyles(theme => ({
    card: {
        marginTop: theme.spacing(2),
        marginBottom: theme.spacing(5),
        borderStyle: 'solid',
        borderColor: 'black',
        borderRadius: '0.25rem',
        borderWidth: 1,
        minWidth: 200,
        minHeight: 100,
        maxWidth: 300
    },
    title: {
        fontSize: 14
    },
    accessButton: {
        margin: '0 auto',
        display: 'block'
    }
}))


export default function SessionCard(props) {
    const classes = useStyles();
    const { id, name, omop, ehr } = props;
    const omopName = CDMVersions.filter(function(cdm) { return cdm.id === omop })[0].name;


    /**
     * Redirect to ETL session page
     * 
     * @param {*} session_id session's id
     */

    const redirect = (session_id) => {
        window.location.href = '/session/' + session_id;
    }

    return(
        <Card className={classes.card} variant="outlined">
            <CardContent>
                <Typography className={classes.title} color="textSecondary" gutterBottom>{ name }</Typography>

                <Typography variant="body2" component="p">
                    <Typography variant="body2" component="b">EHR: </Typography>
                    { ehr }
                </Typography>
                
                <Typography variant="body2" component="p">
                    <Typography variant="body2" component="b">OMOP CDM: </Typography>
                    { omopName }
                </Typography>
                
            </CardContent>

            <CardActions>
                <Button className={classes.accessButton} size="medium" variant="contained" color="primary" onClick={() => redirect(id)}>Access</Button>
            </CardActions>
        </Card>
    )
}