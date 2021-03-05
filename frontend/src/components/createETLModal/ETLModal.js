import React, {Component} from "react";
import {Button, Col, Dropdown, DropdownButton, Form, Modal, Row, Spinner} from "react-bootstrap";
import { FilePicker } from "react-file-picker";
import {CDMVersions} from "../session/CDMVersions";
import ETLService from '../../services/etl-list-service';
import "./ETLModal.css";

class ETLModal extends Component {

    constructor(props) {
        super(props);
        this.state = {
            cdm: CDMVersions.filter(function(cdm) { return cdm.id === "CDMV60" })[0],
            file: null, fileName: "",
            showFile: true,
            loading: false
        }
    }

    handleCDMSelect(cdm) {
        this.setState({
            cdm: cdm
        });
    }

    handleSave = file => {
        this.setState({
            file: file,
            fileName: file.name
        });
    }

    /**
     *
     * @returns {Promise<void>}
     */
    createSession = async () => {
        //
        this.props.createSession();

        //
        this.setState({ loading: true })
        ETLService.createETL(this.state.file, this.state.cdm.id).then(res => {
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
                                    <Col><Form.File/></Col>
                                </Form.Row>
                            </Form.Group>
                            <Form.Group inline>
                                <Form.Row>
                                    <Form.Label className="my-1 mr-2" htmlFor="inlineFormCustomSelectPref">OMOP CDM: </Form.Label>
                                    <Form.Control as="select" className="my-1 mr-sm-2" id="inlineFormCustomSelectPref" custom>
                                        { CDMVersions.map((item, index) => {
                                            return (
                                                <option value={item.id}>{item.name}</option>
                                            )
                                        }) }
                                    </Form.Control>
                                </Form.Row>
                            </Form.Group>
                        </Form>
                    </Modal.Body>

                    { this.state.loading ?
                        (
                            <Modal.Footer>
                                <Button onClick={this.createSession} disabled>
                                    <Spinner as="span" animation="border" size="sm" role="status" aria-hidden="true"/>
                                </Button>
                                <Button className="btn-danger" onClick={this.props.closeModal} disabled>Close</Button>
                            </Modal.Footer>
                        ) : (
                            <Modal.Footer>
                                <Button onClick={this.createSession}>Create</Button>
                                <Button className="btn-danger" onClick={this.props.closeModal}>Close</Button>
                            </Modal.Footer>
                        )
                    }
                </Modal>
            </div>
        )
    }
}

export default ETLModal;

/*
<div className="bodyEntries">
                            <Row>
                                <p><strong>EHR:</strong></p>
                                <p className={this.state.fileName === "" ? "hideFileName" : "showFileName"}> {this.state.fileName} </p>
                            </Row>
                            <Row>
                                <FilePicker extensions={["xlsx"]} onChange={this.handleSave} onError={err => console.log(err)}>
                                    <Button disabled={this.state.loading}>Upload</Button>
                                </FilePicker>
                            </Row>
                        </div>

                        <div className="bodyEntries">
                            <Row>
                                <p><strong>CDM: </strong></p>
                                <DropdownButton className="CDMdropdown" alignRight variant={"secondary"} title={this.state.cdm.name} id="dropdown">
                                    { CDMVersions.map((item, index) => {
                                        return (
                                            <Dropdown.Item key={index} eventKey={[item.id, item.name]} onSelect={() => this.handleCDMSelect(item)}>{item.name}</Dropdown.Item>
                                        )
                                    }) }
                                </DropdownButton>
                            </Row>
                        </div>
 */