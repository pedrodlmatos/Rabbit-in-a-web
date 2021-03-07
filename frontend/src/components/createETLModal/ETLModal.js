import React, {Component} from "react";
import {Button, Col, Form, Modal, Spinner} from "react-bootstrap";
import {CDMVersions} from "../session/CDMVersions";
import ETLService from '../../services/etl-list-service';
import "./ETLModal.css";

class ETLModal extends Component {

    constructor(props) {
        super(props);
        this.state = {
            cdm: CDMVersions[0].id,
            file: null,
            loading: false
        }
    }

    /**
     * Changes OMOP CDM version chosen
     * 
     * @param {*} event 
     */

    handleCDMSelect(event) {
        this.setState({
            cdm: event.target.value
        });
    }


    /**
     * Saves Scan report file
     * 
     * @param {*} event on change event
     */

    handleSave(event) {
        this.setState({
            file: event.target.files[0],
        });
    }

    
    /**
     * 
     */
    createSession = async () => {
        // disables button to create session
        this.props.createSession();
        
        // changes state to disable buttons and activate spinner 
        this.setState({ loading: true })        
        
        // sends request to API and then redirects to created session
        ETLService.createETL(this.state.file, this.state.cdm).then(res => {
            window.location.href = '/session/' + res.data.id;
        }).then(res => {
            console.log(res);
        })
        
    }

    render() {
        return(
            <div>
                <Modal show={this.props.modalIsOpen} onHide={this.props.closeModal} size={"md"}>
                    <Modal.Header closeButton>
                        <Modal.Title id="contained-modal-title-vcenter">
                            <p>Create ETL Session</p>
                        </Modal.Title>
                    </Modal.Header>

                    <Modal.Body className="modalBody">
                        <Form>
                            <Form.Group>
                                <Form.Row>
                                    <Form.Label>EHR: </Form.Label>
                                    <Col><Form.File as="div" type="file" onChange={this.handleSave.bind(this)} /></Col>
                                </Form.Row>
                            </Form.Group>

                            <Form.Group>
                                <Form.Row>
                                    <Form.Label>OMOP CDM: </Form.Label>
                                    <Col>
                                        <Form.Control className="cdm_dropdown" as="select" defaultValue={this.state.cdm} onChange={this.handleCDMSelect.bind(this)} custom>
                                            { CDMVersions.map(item => {
                                                return (
                                                    <option key={item.id} value={item.id}>{item.name}</option>
                                                )
                                            }) }
                                        </Form.Control>
                                    </Col>
                                    
                                </Form.Row>
                            </Form.Group>

                            { this.state.loading ? 
                                (
                                    <div>
                                        <Button variant="primary" disabled>
                                            Creating <Spinner as="span" animation="border" size="sm" role="status" aria-hidden="true"/>
                                        </Button>

                                        <Button variant="danger" disabled>Close</Button>
                                    </div>
                                ) : (
                                    <div>
                                        <Button className="button" variant="primary" onClick={this.createSession}>Create</Button>
                                        <Button className="button" variant="danger" onClick={this.props.closeModal}>Close</Button>
                                    </div>
                                ) 
                            }
                        </Form>
                    </Modal.Body>
                </Modal>
            </div>
        )
    }
}

export default ETLModal;