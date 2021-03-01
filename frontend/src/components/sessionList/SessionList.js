import React, { Component } from "react";
import {CardDeck, Spinner} from 'react-bootstrap';
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
            modalIsOpen: false,
            loading: false,
            createSessionLoading: false
        };

        this.openModal = this.openModal.bind(this);
        this.closeModal = this.closeModal.bind(this);
        this.createETLSession = this.createETLSession.bind(this);
    }


    componentDidMount = async () => {
        ETLService.getAllETL()
            .then(response => {
                this.setState({
                    sessions: response.data,
                    loadingSessions: true
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

    createETLSession() {
        this.setState({
            createSessionLoading: true
        })
    }


    render() {
        const sessions = this.state.sessions.map(
            session => <SessionCard key={session.id} id={session.id} name={session.name} ehr={session.sourceDatabase.databaseName} omop={session.targetDatabase.databaseName}/>
        )
        

        return (
            <div className="sessionsContainer">
                { this.state.loadingSessions ?
                    (
                        <div>
                            <h1>ETL Sessions</h1>
                            <CardDeck className="sessionCard">
                                { sessions }
                            </CardDeck>
                            <Button type="btn btn-primary" onClick={() => this.openModal()} disabled={this.state.createSessionLoading}>Create Session</Button>
                            <ETLModal modalIsOpen={this.state.modalIsOpen} createSession={this.createETLSession} closeModal={this.closeModal} />
                        </div>
                    ) :
                    (
                        <div>
                            <Spinner animation="border"/>
                        </div>
                    )
                }
            </div>
        )
    }
}

export default SessionList