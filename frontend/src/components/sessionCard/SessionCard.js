import React, {Component} from "react";
import {Card, Button } from 'react-bootstrap';
import './SessionCard.css';


class SessionCard extends Component {

    constructor(props) {
        super(props);
        this.state = {
            session_id: props.session.id,
            name: props.session.name,
            targetDatabase: props.session.targetDatabase,
            sourceDatabase: props.session.sourceDatabase,
        }
    }

    redirect(session_id) {
        window.location.href = '/session/' + session_id;
    }

    render() {
        return(
            <Card className="card">
                <Card.Body>
                    <Card.Title>
                        { this.state.name }
                    </Card.Title>

                    <Card.Text>
                        EHR: { this.state.sourceDatabase.databaseName }
                        <br/>
                        CDM: { this.state.targetDatabase.version }
                    </Card.Text>

                    <Button className="accessButton btn btn-primary" onClick={() => { this.redirect(this.state.session_id); }}>Access</Button>
                </Card.Body>
            </Card>
        );
    }
}

export default SessionCard