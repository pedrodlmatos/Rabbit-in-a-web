import React, {Component} from "react";
import {Card, Button } from 'react-bootstrap';
import './SessionCard.css';
import {CDMVersions} from "../session/CDMVersions";


class SessionCard extends Component {

    constructor(props) {
        super(props);
        this.state = {
            session_id: props.id,
            name: props.name,
            targetDatabase: CDMVersions.filter(function(cdm) { return cdm.id === props.omop })[0].name,
            sourceDatabase: props.ehr,
        }
    }

    redirect(session_id) {
        window.location.href = '/session/' + session_id;
    }

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

                        <Button className="accessButton btn btn-primary" onClick={() => { this.redirect(this.state.session_id); }}>Access</Button>
                    </Card.Body>
                </Card>
            </div>

        );
    }
}

export default SessionCard