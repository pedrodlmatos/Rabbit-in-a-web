import React, { Component } from "react";
import axios from 'axios';
import {CardDeck, Row} from 'react-bootstrap';
import SessionCard from "../sessionCard/SessionCard";
import './SessionList.css';
import Button from "react-bootstrap/Button";

class SessionList extends Component {

    constructor(props) {
        super(props);
        this.state = { sessions: [] };
    }


    componentDidMount() {
        const url = 'http://localhost:8081/sessions/all';

        axios.get(url)
            .then(res => {
                this.setState({ sessions: res.data });
            });
    }

    createSession(event) {
        const url = 'http://localhost:8081/sessions/create';
        const data = { 'cdm': 'db-cdmv60' }

        axios.post(url, data)
            .then((response) => {
                this.setState(this.state.sessions.concat(response.data), () => window.location.reload());
            }).catch(error => {
                console.log(error.response)
        });
    }


    render() {
        const sessions = this.state.sessions.map(
            session => <SessionCard key={session.id} session={session}/>
        )
        

        return (
            <div className="container">
                <Row>
                    <h1>Sessions</h1>
                </Row>

                <Row>
                    <CardDeck className="cardDeck">
                        { sessions }
                    </CardDeck>
                </Row>


                <Button className="addButton" type="btn btn-primary" onClick={this.createSession.bind(this)}>Create Session</Button>
            </div>
        )
    }
}

export default SessionList