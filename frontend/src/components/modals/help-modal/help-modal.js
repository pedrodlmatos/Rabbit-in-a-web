import React, {Component} from "react";
import {Button, Modal} from "react-bootstrap";

class HelpModal extends Component {
    render() {
        return(
            <div>
                <Modal show={this.props.modalIsOpen} onHide={this.props.closeModal} size={"sm"}>
                    <Modal.Header closeButton>
                        <Modal.Title id="contained-modal-title-vcenter">
                            <p>Help</p>
                        </Modal.Title>
                    </Modal.Header>

                    <Modal.Body>
                        <p>
                            To create a mapping between tables and fields, first you must select a table or field from the EHR database and then a table or field from the OMOP CDM.
                        </p>
                    </Modal.Body>

                    <Modal.Footer>
                        <Button className="btn-danger" onClick={this.props.closeModal}>Close</Button>
                    </Modal.Footer>
                </Modal>
            </div>
        )
    }
}

export default HelpModal;