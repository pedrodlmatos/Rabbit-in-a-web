import React, {Component} from "react";
//import {Card, Button } from 'react-bootstrap';
import { Card, CardActions, CardContent, Button, Typography } from '@material-ui/core';
import './SessionCard.css';
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

        console.log("ASDASD");
    }

    redirect(session_id) {
        window.location.href = '/session/' + session_id;
    }

    render() {
        return (
            <Card className="card" variant="outlined">
                <CardContent>
                    <Typography className="title" color="textSecondary" gutterBottom>{ this.state.name }</Typography>

                    <Typography variant="body2" component="p">{ this.state.sourceDatabase }</Typography>
                    <Typography variant="body2" component="p">{ this.state.targetDatabase }</Typography>
                    
                </CardContent>

                <CardActions>
                    <Button size="small" variant="contained" color="primary" onClick={() => { this.redirect(this.state.session_id); }}>Access</Button>
                </CardActions>
            </Card>
        )
    }

    /*
    render() {
        
        
        return(
            <div className="col-sm-3 col-md-2">
                <Card className="card">
                    <Card.Body>
                        <Card.Title>
                            { this.state.name }
                        </Card.Title>

                        <Card.Text>
                            EHR: { this.state.sourceDatabase }
                            <br/>
                            CDM: { this.state.targetDatabase }
                        </Card.Text>

                        <Button className="accessButton btn btn-primary" >Access</Button>
                    </Card.Body>
                </Card>
            </div>

        );
    }*/
}