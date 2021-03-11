import React, {Component} from "react";
import { Card, CardActions, CardContent, Button, Typography } from '@material-ui/core';
import './session-card.css';
import { CDMVersions } from "../session/CDMVersions";


export default class SessionCard extends Component {

    constructor(props) {
        super(props);
        this.state = {
            session_id: props.id,
            name: props.name,
            targetDatabase: CDMVersions.filter(function(cdm) { return cdm.id === props.omop })[0].name,
            sourceDatabase: props.ehr,
        }
    }

    /**
     * Redirect to ETL session page
     * 
     * @param {*} session_id session's id
     */

    redirect(session_id) {
        window.location.href = '/session/' + session_id;
    }


    render() {
        return (
            <Card className="card" variant="outlined">
                <CardContent>
                    <Typography className="title" color="textSecondary" gutterBottom>{ this.state.name }</Typography>

                    <Typography variant="body2" component="p">
                        <Typography variant="body2" component="b">EHR: </Typography>
                        { this.state.sourceDatabase }
                    </Typography>
                    
                    <Typography variant="body2" component="p">
                        <Typography variant="body2" component="b">OMOP CDM: </Typography>
                        { this.state.targetDatabase }
                    </Typography>
                    
                </CardContent>

                <CardActions>
                    <Button className="accessButton" size="medium" variant="contained" color="primary" onClick={() => { this.redirect(this.state.session_id); }}>Access</Button>
                </CardActions>
            </Card>
        )
    }
}