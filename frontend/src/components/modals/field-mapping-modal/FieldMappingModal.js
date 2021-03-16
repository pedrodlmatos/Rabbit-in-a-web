import React, { Component } from "react";
import {Modal, Row, Button, Col} from "react-bootstrap";
import { FormGroup, FormControlLabel, Switch } from "@material-ui/core";
import Xarrow from "react-xarrows";
import "./FieldMappingModal.css";
import EHRField from "./EHRField";
import CDMField from "./CDMField";
import TableMappingService from "../../../services/table-mapping-service"
import FieldMappingService from "../../../services/field-mapping-service";

class FieldMappingModal extends Component {

    constructor(props) {
        super(props);
        this.state = {
            map_id: null,
            sourceTable: { name: "", fields: [] },
            targetTable: { name: "", fields: [] },
            complete: false,
            selectedField: null,
            field: { id:null, name: "", description: "", type: "" }, 
            selectedSource: false,
            showDetails: false, showDeleteButton: false,
            arrows: [], selectedArrow: "",
        }
    }

    getInformation() {
        if (this.props.data != null) {
            TableMappingService.getMapping(this.props.data).then(res => {
                let maps = []
                res.data.fieldMappings.forEach(function(res) {
                    const arrow = {
                        id: 'arrow-' + res.id,
                        map_id: res.id,
                        start: res.source.name,
                        end: res.target.name,
                        color: "grey",

                        startField: res.source,
                        endField: res.target
                    }
                    maps = maps.concat(arrow);
                })

                this.setState({
                    map_id: res.data.id,
                    sourceTable: res.data.source,
                    targetTable: res.data.target,
                    complete: res.data.complete,
                    arrows: maps,
                })
            })
        }
    }


    setSelectedSourceField = (field) => {
        if (this.state.selectedField === null) {
            // no field is selected
            this.setState({
                selectedField: field,
                field: field.props.field,
                selectedSource: true,
                showDetails: true
            });
        } else if (this.state.selectedField === field) {
            // unselect
            this.setState({
                selectedField: null,
                field: {},
                selectedSource: false,
                showDetails: false,
            });
        } else {
            // other source field is selected
            this.state.selectedField.setState({
                clicked: false,
            });

            this.setState({
                selectedField: field,
                field: field.props.field,
                selectedSource: true,
                showDetails: true
            });
        }

    }

    createFieldMapping = (startField, endField) => {
        FieldMappingService.addFieldMapping(this.state.map_id, startField.id, endField.id)
            .then(res => {
                const arrow = {
                    id: 'arrow' + res.data.id,
                    map_id: res.data.id,
                    start: startField.name,
                    end: endField.name,
                    color: 'grey',
                    startField: startField,
                    endField: endField
                }
                this.setState({
                    arrows: this.state.arrows.concat(arrow)
                });
            }).catch(res => {
                console.log(res);
            });
    }


    setSelectedTargetField = (field) => {
        if (this.state.selectedField === null) {
            // no field is selected
            this.setState({
                selectedField: field,
                field: field.props.field,
                selectedSource: false,
                showDetails: true
            });
        } else if (this.state.selectedField === field) {
            // unselect
            this.setState({
                selectedField: null,
                field: {},
                showDetails: false
            });
        } else if (this.state.selectedSource) {
            // create arrow
            this.createFieldMapping(this.state.field, field.props.field);

            // unselect field
            this.state.selectedField.setState({clicked: false});
            field.setState({clicked: false});

            // clean state
            this.setState({
                selectedField: null,
                field: {},
                showDetails: false,
                selectedSource: false
            });

        } else {
            // other target field is selected
            this.state.selectedField.setState({
                clicked: false
            });

            this.setState({
                selectedField: field,
                field: field.props.field,
                showDetails: true
            });
        }
    }


    selectArrow = (arrow) => {
        const index = this.state.arrows.indexOf(arrow);

        if (this.state.selectedArrow === null) {
            // no arrow is selected
            let arrows = this.state.arrows
            arrows[index].color = "red";

            this.setState({
                selectedArrow: arrow,
                arrows: arrows,
                showDeleteButton: true
            });
        } else if(this.state.selectedArrow === arrow) {
            // select the arrow previous selected to unselect
            let arrows = this.state.arrows
            arrows[index].color = "grey";

            this.setState({
                selectedArrow: null,
                arrows: arrows,
                showDeleteButton: false
            });
        } else {
            // select any other unselected arrow

            // unselect previous
            this.cleanClickedArrows();

            // select a new one
            let arrows = this.state.arrows
            arrows[index].color = "red";

            this.setState({
                selectedArrow: arrow,
                arrows: arrows,
                showDeleteButton: true
            });
        }
    }

    /**
     * Unselects all arrows (changes color to gray)
     */
    cleanClickedArrows() {
        this.setState({
            arrows: this.state.arrows.map(ar => ar.color = "grey")
        })
    }


    removeFieldMapping = () => {
        FieldMappingService.removeFieldMapping(this.state.map_id, this.state.selectedArrow.map_id).then(
            res => {
                let maps = []
                res.data.forEach(
                    function(item) {
                        const arrow = {
                            id: 'arrow-' + item.id,
                            map_id: item.id,
                            start: item.source.name,
                            end: item.target.name,
                            color: "grey",

                            startTable: item.source,
                            endTable: item.target
                        }
                        maps = maps.concat(arrow);
                    }
                )

                this.setState({
                    arrows: maps,
                    selectedArrow: null,
                    showDeleteButton: false
                })
            }
        )
    }

    
    handleCompletionChange = (event) => {
        TableMappingService.editCompleteMapping(this.state.map_id, !this.state.complete)
            .then(res => { 
                this.setState({
                    complete: res.data.complete
                });
                this.props.changeMapCompletion(this.state.map_id, res.data.complete)
            }).catch(res => {
                console.log(res);
            })
    }


    render() {
        return(
            <Modal show={this.props.modalIsOpen} onShow={() => this.getInformation()} onHide={this.props.closeModal} size={"xl"}>
                <Modal.Header>
                    <Modal.Title id="contained-modal-title-vcenter">
                        <p>
                            { this.state.sourceTable.name } <i className="fa fa-long-arrow-alt-right"></i> {this.state.targetTable.name}
                        </p>

                        <FormGroup row>
                            <FormControlLabel 
                                control={<Switch checked={this.state.complete} onChange={this.handleCompletionChange} color='primary' />}
                                label="Complete" />
                        </FormGroup>

                    </Modal.Title>
                </Modal.Header>

                <Modal.Body>
                    <Row>
                        <Col md={5}>
                            { this.state.sourceTable.fields.map((item, index) => {
                                return (
                                    <EHRField key={index} id={item.name} handleCallback={this.setSelectedSourceField} field={item} />
                                )
                            })}
                        </Col>
                        
                        <Col md={5}>
                            { this.state.targetTable.fields.map((item, index) => {
                                return (
                                    <CDMField key={index} id={item.name} handleCallback={this.setSelectedTargetField} field={item} />
                                )
                            })}
                        </Col>

                        {this.state.arrows.map((ar, i) => (
                            <Xarrow start={ar.start} end={ar.end} key={i}
                                    startAnchor="right" endAnchor="left" color={ar.color} strokeWidth={7.5} curveness={0.5}
                                    passProps={{
                                        onClick: () => this.selectArrow(ar),
                                    }}/>
                        ))}

                        <Col md={2}>
                            <div className={this.state.showDetails ? "showDetails" : "hideDetails"}>
                                <p><strong>Field name: </strong>{this.state.field.name}</p>
                                <p><strong>Description: </strong>{this.state.field.description}</p>
                                <p><strong>Data type: </strong>{this.state.field.type}</p>
                            </div>
                        </Col> 
                    </Row>               
                </Modal.Body>

                <Modal.Footer>
                    <Button className={this.state.showDeleteButton ? "btn-danger showButton" : "hideButton"} onClick={() => this.removeFieldMapping()}>Remove Field Mapping</Button>
                    <Button className="btn-danger" onClick={this.props.remove}>Remove Table Mapping</Button>
                    <Button onClick={this.props.closeModal}>Close</Button>
                </Modal.Footer>
            </Modal>
        )
    }
}

export default FieldMappingModal;

/*
*/