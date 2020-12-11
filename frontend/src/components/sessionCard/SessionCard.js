import React, {Component} from "react";
import {Card, Button } from 'react-bootstrap';
import './SessionCard.css';


class SessionCard extends Component {

    constructor(props) {
        super(props);
        this.state = {
            //session: props.session,
            session_id: props.session.id,
            //source_id: props.session.etl.sourceDB.id,
            //target_id: props.session.etl.targetDB.id
        }
    }

    redirect(session_id) {
        window.location.href = '/session/' + session_id;
    }

    render() {
        return(
            <Card className="card">
                <Card.Body className="cardBody">
                    <Card.Title className="cardTitle">
                        { this.state.session_id }
                    </Card.Title>

                    <Button className="btn btn-primary" onClick={() => { this.redirect(this.state.session_id); }}>Access</Button>
                </Card.Body>
            </Card>
        );
    }
}

export default SessionCard