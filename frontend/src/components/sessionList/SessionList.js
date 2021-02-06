import React, { Component } from "react";
import {CardDeck} from 'react-bootstrap';
import SessionCard from "../sessionCard/SessionCard";
import './SessionList.css';
import Button from "react-bootstrap/Button";
import ETLService from '../../services/etl-list-service';
import ETLModal from "../createETLModal/ETLModal";

class SessionList extends Component {

    constructor(props) {
        super(props);
        this.state = {
            sessions: [],
            modalIsOpen: false
        };

        this.openModal = this.openModal.bind(this);
        this.closeModal = this.closeModal.bind(this);
    }


    componentDidMount() {
        ETLService.getAllETL()
            .then(response => {
                this.setState({
                    sessions: response.data
                });
            }).catch(response => {
                console.log(response);
            });
    }

    openModal() {
        this.setState({
            modalIsOpen: true
        });
    }

    closeModal() {
        this.setState({
            modalIsOpen: false
        });
    }

    /*
    createSession(event) {
        ETLService.createETL()
        .then((response) => {
            this.setState(this.state.sessions.concat(response.data), () => window.location.reload());
        }).catch(error => {
            console.log(error.response)
        });      
    }*/

    createSession() {

    }


    render() {
        const sessions = this.state.sessions.map(
            session => <SessionCard key={session.id} session={session}/>
        )
        

        return (
            <div className="sessionsContainer">
                <h1>ETL Sessions</h1>

                <CardDeck className="sessionCard">
                    { sessions }
                </CardDeck>

                <Button type="btn btn-primary" onClick={() => this.openModal()}>Create Session</Button>
                <ETLModal modalIsOpen={this.state.modalIsOpen} closeModal={this.closeModal} />
            </div>
        )
    }
}

export default SessionList