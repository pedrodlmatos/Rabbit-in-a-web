import React, {Component} from "react";
import {Col, Row, Table, Dropdown, DropdownButton} from "react-bootstrap";
import Xarrow from "react-xarrows";
import "./Session.css";
import EHRTable from "../table/EHRTable";
import CDMTable from "../table/CDMTable";
import FieldMappingModal from "../fieldMappingModal/FieldMappingModal";
import ETLService from "../../services/etl-list-service";
import TableMappingService from "../../services/table-mapping-service";
import { CDMVersions } from "./CDMVersions";


class Session extends Component {

    constructor(props) {
        super(props);
        this.state = {
            cdmName: "",  etl_id: "",
            sourceDB_id: "", targetDB_id: "", targetDB: "",
            sourceDB_tables: [], targetDB_tables: [],

            /* selection info */
            selectedTable: null, sourceSelectedTable: null, targetSelectedTable: false,

            /* table info */
            columns: ['Field', "Type", "Description"], data: [], showTable: false, tableName: "",

            /* arrows */
            arrows: [], selectedArrow: null, arrow_id: null, modalIsOpen: false
        }

        this.openModal = this.openModal.bind(this);
        this.closeModal = this.closeModal.bind(this);
        this.handleCDMSelect = this.handleCDMSelect.bind(this);
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
                        id: 'arrow-' + item.id,
                        map_id: item.id,
                        start: item.source.name,
                        end: item.target.name,
                        color: "grey",

                        startTable: item.source,
                        endTable: item.target
                    }
                    maps = maps.concat(arrow);
                })

                this.setState({
                    etl_id: res.data.id,
                    name: res.data.name,
                    sourceDB_id: res.data.sourceDatabase.id,
                    targetDB_id: res.data.targetDatabase.id,
                    sourceDB_tables: res.data.sourceDatabase.tables,
                    targetDB_tables: res.data.targetDatabase.tables,
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

        const etl = { 
            id: this.state.etl_id,
            sourceDatabase: this.state.sourceDatabase,
            targetDatabase: this.state.targetDatabase
        }
        ETLService.changeTargetDatabase(etl, cdm_id)
            .then(response => {
                this.setState({
                    targetDB_id: response.data.targetDatabase.id,
                    targetDB_tables: response.data.targetDatabase.tables,
                    cdmName: CDMVersions.filter(function(cdm) { return cdm.id === response.data.targetDatabase.databaseName })[0].name,
                    
                    selectedTable: null, sourceSelectedTable: null, targetSelectedTable: false,
                    arrows: [], selectedArrow: null, modalShow: false
                });
            }).catch(error => {
                console.log(error);
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
                    description: item.description
                })
            )
        });
        this.setState({ 
            data: data, 
            showTable: true,
            tableName: table.name
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
            this.setState( {
                selectedTable: element,
                sourceSelectedTable: element.props.table,
                targetSelectedTable: null
            }, 
                // () => { console.log(this.state.selectedTable); }
            );

            // change content of fields table
            this.defineData(element.props.table);
        } else if (this.state.selectedTable === element) {
            // select the same table

            // unselect
            this.setState( {
                selectedTable: null,
                sourceSelectedTable: null,
                targetSelectedTable: null,

                data: [],
                showTable: false
            }, 
                //() => { console.log(this.state.selectedTable); }
            );
        } else {
            // other table was selected

            // unselect previous selected table
            this.state.selectedTable.setState({clicked: false});

            // change select table information
            this.setState( {
                selectedTable: element,
                sourceSelectedTable: element.props.table,
                targetSelectedTable: null
            }, 
                //() => { console.log(this.state.selectedTable); }
            );

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

            // change select table information
            this.setState( {
                selectedTable: element,
                sourceSelectedTable: null,
                targetSelectedTable: element.props.table
            }, 
                //() => { console.log(this.state.selectedTable); }
            );

            // change content of fields table
            this.defineData(element.props.table);
        } else if (this.state.selectedTable === element) {
            // select the same table

            // unselect
            this.setState( {
                selectedTable: null,
                sourceSelectedTable: null,
                targetSelectedTable: null,
                data: [],
                showTable: false
            }, 
                // () => { console.log(this.state.selectedTable); }
            );
        } else if (this.state.sourceSelectedTable !== null) {
            // source table is selected -> create arrow

            this.createArrow(this.state.sourceSelectedTable, element.props.table)

            // unselects tables
            this.state.selectedTable.setState({clicked: false});
            element.setState({clicked: false});

            // clean state
            this.setState( {
                selectedTable: null,
                sourceSelectedTable: null,
                targetSelectedTable: null,
                data: [],
                showTable: false
            }, 
                //() => { console.log(this.state.selectedTable); }
            );
        } else {
            // other target table is selected

            // unselects previous selected table
            this.state.selectedTable.setState({clicked: false});

            // clean state
            this.setState( {
                selectedTable: element,
                sourceSelectedTable: null,
                targetSelectedTable: element.props.table,
                data: []
            }, 
                // () => { console.log(this.state.selectedTable); }
            );

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
        TableMappingService.addTableMapping(this.state.etl_id, startTable.id, endTable.id)
            .then(res => {
                const arrow = {
                    id: 'arrow-' + res.data.id,
                    map_id: res.data.id,
                    start: startTable.name,
                    end: endTable.name,
                    color: "grey",
        
                    startTable: startTable,
                    endTable: endTable
                }

                this.setState({ 
                    arrows: this.state.arrows.concat(arrow)
                });
            }).catch(res => {
                console.log(res);
            });
    }


    /**
     * Unselects all arrows (changes color to gray)
     */
    cleanClickedArrows() {
        this.setState({
            arrows: this.state.arrows.map(ar => ar.color = "grey")
        })
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
        this.setState({
            modalIsOpen: false
        });

        TableMappingService.removeTableMapping(this.state.etl_id, this.state.arrow_id)
            .then(res => {
                let maps = []
                res.data.forEach(function(item) {
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
                })

                this.setState({
                    arrow_id: null,
                    arrows: maps
                })

            }).catch(res => {
                console.log(res);
        })
    }

    openModal(arrow) {
        this.setState({
                arrow_id: arrow.map_id,
                modalIsOpen: true
            },
            /*() => console.log(this.state)*/
        );
    }

    closeModal(){
        this.setState({ modalIsOpen: false });
    }

    render() {

        return(
            <div className="tablesArea">
                <h1>{ this.state.name }</h1>
                <Row>
                    <Col sm={3} md={3} lg={3}>
                        <div>
                            <h4>EHR Database</h4>

                            { this.state.sourceDB_tables.map((item, index) => {
                                return (
                                    <EHRTable key={index} id={item.name} handleCallback={this.setSelectedSourceTable} table={item} />
                                )
                            })}
                        </div>
                    </Col>

                    <Col sm={3} md={3} lg={3}>
                        <DropdownButton alignRight variant={"secondary"} title={this.state.cdmName} id="dropdown">
                            { CDMVersions.map((item, index) => {
                                return (
                                    <Dropdown.Item key={index} eventKey={[item.id, item.name]} onSelect={() => this.handleCDMSelect(item.id)}>{item.name}</Dropdown.Item>
                                )
                            }) }
                        </DropdownButton>

                        { this.state.targetDB_tables.map((item, index) => {
                            return (
                                <CDMTable key={index} id={item.name} handleCallback={this.setSelectedTargetTable} table={item} />
                            )
                        })}
                    </Col>
                    {
                        this.state.arrows.map((ar, i) => (
                            <Xarrow start={ar.start} end={ar.end} key={i}
                                    startAnchor="right" endAnchor="left" color={ar.color} strokeWidth={7.5} curveness={0.5}
                                    passProps={{
                                        onClick: () => this.selectArrow(ar),
                                        onDoubleClick: () => this.openModal(ar)
                                        }}/>
                        ))
                    }

                    <Col sm={6} md={6} lg={6}>
                        <div className={this.state.showTable ? "tableShow" : "tableHidden"}>
                            <h6><strong>Table name: </strong>{this.state.tableName}</h6>
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
                    </Col>
                </Row>
                <FieldMappingModal modalIsOpen={this.state.modalIsOpen} closeModal={this.closeModal}
                                       data={this.state.arrow_id} remove={this.removeArrow}/>
            </div>
        )
    }
}

export default Session;