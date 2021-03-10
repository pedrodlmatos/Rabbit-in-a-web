import React, { Component } from "react";
import { Spinner } from 'react-bootstrap';
import { Grid, Button } from '@material-ui/core';
import './SessionList.css';
import SessionCard from '../sessionCard/SessionCard';
// import Button from "react-bootstrap/Button";
import ETLService from '../../services/etl-list-service';
import ETLModal from "../createETLModal/ETLModal";

export default class SessionList extends Component {

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
        this.disableCreateButton = this.disableCreateButton.bind(this);
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


    /**
     * Changes state to show modal 
     */

    openModal() { this.setState({ modalIsOpen: true }); }


    /**
     * Changes state to close modal
     */

    closeModal() { this.setState({ modalIsOpen: false }); }


    /**
     * Changes state to disable create session button
     */

    disableCreateButton() { this.setState({ createSessionLoading: true }) }


    render() {
        return (
            <div>
                { this.state.loadingSessions ? 
                    (
                        <div className="pageContainer">
                            <h1>ETL Sessions</h1>

                            <Grid className="gridContainer" container spacing={4}>
                                { this.state.sessions.map(session => 
                                    <Grid item xs={12} sm={6} md={2} lg={2}>
                                        <SessionCard key={session.id} id={session.id} name={session.name} ehr={session.sourceDatabase.databaseName} omop={session.targetDatabase.databaseName}/>
                                    </Grid>
                                )}
                            </Grid>

                            <Button type="small" onClick={() => this.openModal()} disabled={this.state.createSessionLoading}>Create Session</Button>
                            <ETLModal modalIsOpen={this.state.modalIsOpen} createSession={this.disableCreateButton} closeModal={this.closeModal} />
                        </div>
                    ) : (
                        <Spinner animation="border"/>
                    )
                }
            </div>
        )
    }

    /*
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
                            <ETLModal modalIsOpen={this.state.modalIsOpen} createSession={this.disableCreateButton} closeModal={this.closeModal} />
                        </div>
                    ) : (
                        <div>
                            <Spinner animation="border"/>
                        </div>
                    )
                }
            </div>
        )
    }*/
}