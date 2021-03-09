import React, {Component} from "react";
import {Col, Row, Table, Dropdown, DropdownButton, Button, Form} from "react-bootstrap";
import Xarrow from "react-xarrows";
import "./Session.css";
import EHRTable from "../table/EHRTable";
import CDMTable from "../table/CDMTable";
import FieldMappingModal from "../fieldMappingModal/FieldMappingModal";
import ETLService from "../../services/etl-list-service";
import TableMappingService from "../../services/table-mapping-service";
import { CDMVersions } from "./CDMVersions";
import HelpModal from "./HelpModal";


class Session extends Component {

    constructor(props) {
        super(props);
        this.state = {
            etl: {
                id: null, name: null,
                targetDatabase: { id: null, tables: [], databaseName: null },
                sourceDatabase: { id: null, tables: [], databaseName: null }
            },
            cdmName: "",

            /* selection info */
            selectedTable: null, sourceSelected: false,

            /* table info */
            comment: "", commentDisabled: true,
            columns: ['Field', "Type", "Description"], data: [], showTable: false, tableName: "",

            /* arrows */
            arrows: [], selectedArrow: null, arrow_id: null, modalIsOpen: false,

            showHelpModal: false
        }

        this.openModal = this.openModal.bind(this);
        this.closeModal = this.closeModal.bind(this);
        this.handleCDMSelect = this.handleCDMSelect.bind(this);
        this.openHelpModal = this.openHelpModal.bind(this);
        this.closeHelpModal = this.closeHelpModal.bind(this);
        this.editComment = this.editComment.bind(this);
        this.saveComment = this.saveComment.bind(this);
    }

    /**
     * Gets data (databases, tables, fields and table mappings) from API
     */

    componentDidMount() {
        const session_id = window.location.pathname.toString().replace("/session/", "");

        /* get data from API */
        ETLService.getETLById(session_id)
            .then(res => {
                let maps = []

                res.data.tableMappings.forEach(function(item) {
                    const arrow = {
                        id: item.id,
                        start: item.source,
                        end: item.target,
                        color: "grey",
                    }
                    maps = maps.concat(arrow);
                })

                this.setState({
                    etl: {
                        id: res.data.id,
                        name: res.data.name,
                        sourceDatabase: res.data.sourceDatabase,
                        targetDatabase: res.data.targetDatabase
                    }, 
                    arrows: maps,
                    cdmName: CDMVersions.filter(function(cdm) { return cdm.id === res.data.targetDatabase.databaseName })[0].name
                });
            }).catch(res => {
                console.log(res);
            });
    }


    /**
     * Changes CDM database 
     *
     * @param e event
     */

    handleCDMSelect(cdm_id) {
        ETLService.changeTargetDatabase(this.state.etl, cdm_id)
            .then(response => {
                this.setState({
                    etl: { 
                        id: response.data.id,
                        name: response.data.name,
                        sourceDatabase: response.data.sourceDatabase,
                        targetDatabase: response.data.targetDatabase 
                    },
                    cdmName: CDMVersions.filter(function(cdm) { return cdm.id === response.data.targetDatabase.databaseName })[0].name,
                    selectedTable: null, sourceSelected: false,
                    arrows: [], selectedArrow: null, modalShow: false
                });
            }).catch(error => {
                console.log(error);
            });
    }


    selectSourceArrows(table) {
        this.state.arrows.forEach(element => {
            if (element.start.name === table.name) {
                element.color = 'orange';
            }
        });
    }


    selectTargetArrows(table) {
        this.state.arrows.forEach(element => {
            if (element.end.name === table.name) {
                element.color = 'blue';
            }
        });
    }


    /**
     * Defines the content of fields table (field name, type and description)
     *
     * @param table table with data
     */

    defineData(table) {
        let data = []
        table.fields.map((item, index) => {
            return (
                data.push({
                    field: item.name,
                    type: item.type,
                    description: item.description,
                })
            )
        });
        this.setState({ 
            data: data, 
            showTable: true,
            tableName: table.name,
            comment: table.comment
        });
    }


    /**
     * Defines the selected table and changes state of current and previous selected table.
     *
     *  - If no table is selected, only changes the state of the selected table
     *  - If there is a table selected, unselect it and then select the new table changing states
     *  - If select the table that was previous selected, unselects it
     *
     * @param element
     */

    setSelectedSourceTable = (element) => {
        if (this.state.selectedTable === null) {
            // all tables were unselected
            
            // change select table information
            this.setState({
                selectedTable: element,
                sourceSelected: true,
            });

            // change color of mappings that comes from the selected table
            this.selectSourceArrows(element.props.table);
            
            // change content of fields table
            this.defineData(element.props.table);
        } else if (this.state.selectedTable === element) {
            // select the same table
            
            // change color of arrows to grey
            this.cleanClickedArrows();

            // unselect
            this.setState({
                selectedTable: null,
                sourceSelected: false,
                data: [],
                showTable: false
            });
        } else {
            // other table was selected

            // unselect previous selected table
            this.state.selectedTable.setState({clicked: false});

            // change color of arrows to grey
            this.cleanClickedArrows();

            // change select table information
            this.setState( {
                selectedTable: element,
                sourceSelected: true
            });

            // change color of mappings that comes from the selected table
            this.selectSourceArrows(element.props.table);

            // change content of fields table
            this.defineData(element.props.table);
        }
    }


    /**
     * Defines the selected table and changes state of current and previous selected table
     *
     * - If no table is selected, only changes the state of the selected table
     * - If theres is a source table selected, creates arrow
     * - If select the same table, unselect
     * - Else selects a different target table
     *
     * @param element
     */

    setSelectedTargetTable = (element) => {

        if (this.state.selectedTable === null) {
            // no table is selected

            // change color of mappings that comes from the selected table
            this.selectTargetArrows(element.props.table);

            // change select table information
            this.setState( {
                selectedTable: element,
                sourceSelected: false
            });

            // change content of fields table
            this.defineData(element.props.table);
        } else if (this.state.selectedTable === element) {
            // select the same table

            // change color of arrows to grey
            this.cleanClickedArrows();

            // unselect
            this.setState( {
                selectedTable: null,
                sourceSelected: false,
                data: [],
                showTable: false
            });
        } else if (this.state.sourceSelected === true) {
            // source table is selected -> create arrow

            this.createArrow(this.state.selectedTable.props.table, element.props.table)

            // unselects tables
            this.state.selectedTable.setState({clicked: false});
            element.setState({clicked: false});

            // clean state
            this.setState( {
                selectedTable: null,
                sourceSelected: false,
                data: [],
                showTable: false
            });
        } else {
            // other target table is selected

            // unselects previous selected table
            this.state.selectedTable.setState({clicked: false});

            // change color of arrows to grey
            this.cleanClickedArrows();

            // clean state
            this.setState( {
                selectedTable: element,
                sourceSelected: false,
                data: []
            });

            // change color of mappings that comes from the selected table
            this.selectTargetArrows(element.props.table);

            // define fields table
            this.defineData(element.props.table)
        }
    }


    /**
     * Creates an arrow between a source table and a target table.
     *
     * @param startTable
     * @param endTable
     */

    createArrow = (startTable, endTable) => {
        TableMappingService.addTableMapping(this.state.etl.id, startTable.id, endTable.id).then(res => {
            const arrow = {
                id: res.data.id,
                start: startTable,
                end: endTable,
                color: "grey",
            }

            this.setState({ 
                arrows: this.state.arrows.concat(arrow)
            });
        }).catch(res => {
            console.log(res);
        });
    }


    /**
     * Unselects all arrows (changes color to grey)
     */

    cleanClickedArrows() { 
        this.state.arrows.forEach(element => {
            element.color = 'grey';
        });
        //this.setState({ arrows: this.state.arrows.map(ar => ar.color = "grey") }) 
    }


    /**
     * Selects an arrow (changes its color to red)
     *  - If no arrow is previously selected, only selects an arrow
     *  - If selects the arrow previously selected, unselect it
     *  - If selects other arrow, unselects previous and selects the new one
     */

    selectArrow = (arrow) => {
        const index = this.state.arrows.indexOf(arrow);

        if (this.state.selectedArrow === null) {
            // no arrow is selected
            let arrows = this.state.arrows
            arrows[index].color = "red";

            this.setState({
                selectedArrow: arrow,
                arrows: arrows
            });
        } else if(this.state.selectedArrow === arrow) {
            // select the arrow previous selected to unselect
            let arrows = this.state.arrows
            arrows[index].color = "grey";

            this.setState({
                selectedArrow: null,
                arrows: arrows
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
                arrows: arrows
            });
        }
    }


    /**
     * Removes an arrow
     *
     * @param arrow arrow to remove
     */

    removeArrow = (arrow) => {
        this.setState({ modalIsOpen: false });

        TableMappingService.removeTableMapping(this.state.etl.id, this.state.arrow_id).then(res => {
            let maps = []
            res.data.forEach(function(item) {
                const arrow = {
                    id: item.id,
                    start: item.source,
                    end: item.target,
                    color: "grey",
                }
                maps = maps.concat(arrow);
            })

            this.setState({
                arrow_id: null,
                arrows: maps
            })
        }).catch(res => {
            console.log(res);
        })
    }


    /**
     * Changes state to open field mapping modal
     * 
     * @param {*} arrow selected table mapping
     */

    openModal(arrow) {
        this.setState({
            arrow_id: arrow.id,
            modalIsOpen: true
        });
    }

    closeModal(){ this.setState({ modalIsOpen: false }); }

    openHelpModal() { this.setState({ showHelpModal: true }); }

    closeHelpModal() { this.setState({ showHelpModal: false }); }

    editComment() { this.setState({ commentDisabled: false}); }

    saveComment() { 
        this.setState({ commentDisabled: true });

        ETLService.changeComment(this.state.etl.id, this.state.selectedTable.props.table.id, this.state.comment).then(response => {
            console.log(response.data)
            let maps = []
            response.data.tableMappings.forEach(function(item) {
                const arrow = {
                    id: item.id,
                    start: item.source,
                    end: item.target,
                    color: "grey",
                }
                maps = maps.concat(arrow);
            })

            this.setState({
                etl: {
                    id: response.data.id,
                    name: response.data.name,
                    sourceDatabase: response.data.sourceDatabase,
                    targetDatabase: response.data.targetDatabase
                }, 
                arrows: maps,
                cdmName: CDMVersions.filter(function(cdm) { return cdm.id === response.data.targetDatabase.databaseName })[0].name
            });
        }).catch(error => {
            console.log(error);
        });
    }



    
    
    updateComment(event) { 
        this.setState({ comment : event.target.value });
    }



    render() {
        return(
            <div className="tablesArea">
                <Row>
                    <Col sm={4} md={4} lg={4}>
                        <h1>{ this.state.etl.name }</h1>
                    </Col>

                    <Button variant="info" size={"md"} onClick={this.openHelpModal}>Help <i className="fa fa-info"/></Button>
                    <Button variant="warning" size="sm">File</Button>
                </Row>

                <Row>
                    <Col sm={3} md={3} lg={3}>
                        <div className="databaseNameArea">
                            <h4>{this.state.etl.sourceDatabase.databaseName}</h4>
                        </div>
                        <div>
                            { this.state.etl.sourceDatabase.tables.map((item, index) => {
                                return (
                                    <EHRTable key={index} id={item.name} handleCallback={this.setSelectedSourceTable} table={item} />
                                )
                            })}
                        </div>
                    </Col>

                    <Col sm={3} md={3} lg={3}>
                        <div className="databaseNameArea">
                            <DropdownButton alignRight variant={"secondary"} title={this.state.cdmName} id="dropdown">
                                { CDMVersions.map((item, index) => {
                                    return (
                                        <Dropdown.Item key={index} eventKey={[item.id, item.name]} onSelect={() => this.handleCDMSelect(item.id)}>{item.name}</Dropdown.Item>
                                    )
                                }) }
                            </DropdownButton>
                        </div>
                        <div>
                            { this.state.etl.targetDatabase.tables.map((item, index) => {
                                return (
                                    <CDMTable key={index} id={item.name} handleCallback={this.setSelectedTargetTable} table={item} />
                                )
                            })}
                        </div>
                    </Col>
                    { this.state.arrows.map((ar, i) => (
                        <Xarrow start={ar.start.name} end={ar.end.name} key={i} startAnchor="right" endAnchor="left" color={ar.color} strokeWidth={7.5} curveness={0.5}
                            passProps={{ onClick: () => this.selectArrow(ar), onDoubleClick: () => this.openModal(ar) }}/>
                    ))}

                    <Col sm={6} md={6} lg={6}>
                        <div className={this.state.showTable ? "tableShow" : "tableHidden"}>
                            <h6><strong>Table name: </strong>{this.state.tableName}</h6>

                            <div className="table">
                                <Table striped bordered hover>
                                    <thead>
                                    <tr>
                                        {this.state.columns.map((item, index) => {
                                            return (
                                                <th key={index}>{item}</th>
                                            )
                                        })}
                                    </tr>
                                    </thead>

                                    <tbody>
                                    {this.state.data.map((item, index) => {
                                        return (
                                            <tr key={index}>
                                                <td>{ item.field }</td>
                                                <td>{ item.type }</td>
                                                <td>{ item.description }</td>
                                            </tr>
                                        )
                                    })}
                                    </tbody>
                                </Table>
                            </div>

                            <Form>
                                <Form.Group controlId="formComment">
                                    <Form.Label>Comment</Form.Label>
                                    <Form.Control as="input" value={this.state.comment} onChange={this.updateComment.bind(this)} disabled={this.state.commentDisabled} />
                                </Form.Group>

                                <Button className="button" variant="primary" onClick={this.saveComment} disabled={this.state.commentDisabled}>Save</Button>
                                <Button className="button" variant="warning" onClick={this.editComment} disabled={!this.state.commentDisabled}>Edit comment</Button>
                            </Form>
                        </div>
                    </Col>
                </Row>
                <FieldMappingModal modalIsOpen={this.state.modalIsOpen} closeModal={this.closeModal}
                                       data={this.state.arrow_id} remove={this.removeArrow}/>

                <HelpModal modalIsOpen={this.state.showHelpModal} closeModal={this.closeHelpModal}/>
            </div>
        )
    }
}

export default Session;