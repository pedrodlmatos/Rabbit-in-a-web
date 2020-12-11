import React, {Component} from "react";
import {Col, Row, Table} from "react-bootstrap";
import axios from "axios";
import Xarrow from "react-xarrows";
import "./Session.css";
import EHRTable from "../table/EHRTable";
import CDMTable from "../table/CDMTable";


class Session extends Component {

    constructor(props) {
        super(props);
        this.state = {
            i: 0, etl_id: "",
            sourceDB_id: "", targetDB_id: "",
            sourceDB_tables: [], targetDB_tables: [],

            /* selection info */
            selectedTable: null, sourceSelectedTable: null, targetSelectedTable: false,

            /* table info */
            columns: ['Field', "Type", "Description"], data: [],

            /* arrows */
            arrows: [], selectedArrow: null
        }
    }

    /**
     * Gets data (databases, tables, fields) from API
     */
    componentDidMount() {
        const session_id = window.location.pathname.toString().replace("/session/", "");

        /* get data from API */
        const url = 'http://localhost:8081/sessions/' + session_id;
        axios.get(url)
            .then(res => {
                //this.setState({ sessions: res.data });
                this.setState({
                    etl_id: res.data.etl.id,
                    sourceDB_id: res.data.etl.sourceDB.id,
                    targetDB_id: res.data.etl.targetDB.id,
                    sourceDB_tables: res.data.etl.sourceDB.tables,
                    targetDB_tables: res.data.etl.targetDB.tables,
                })
            })
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
        this.setState({ data: data });
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
            /* all tables were unselected */

            /* change select table information */
            this.setState( {
                selectedTable: element,
                sourceSelectedTable: element.props.table,
                targetSelectedTable: null
            }, () => {
                //console.log(this.state.selectedTable);
            });

            /* change content of fields table */
            this.defineData(element.props.table);
        } else if (this.state.selectedTable === element) {
            /* select the same table */

            /* unselect */
            this.setState( {
                selectedTable: null,
                sourceSelectedTable: null,
                targetSelectedTable: null,

                data: []
            }, () => {
                //console.log(this.state.selectedTable);
            });
        } else {
            /* other table was selected */

            /* unselect previous selected table */
            this.state.selectedTable.setState({clicked: false});

            /* change select table information */
            this.setState( {
                selectedTable: element,
                sourceSelectedTable: element.props.table,
                targetSelectedTable: null
            }, () => {
                //console.log(this.state.selectedTable);
            });

            /* change content of fields table */
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
            /* no table is selected */

            /* change select table information */
            this.setState( {
                selectedTable: element,
                sourceSelectedTable: null,
                targetSelectedTable: element.props.table
            }, () => {
                //console.log(this.state.selectedTable);
            });

            /* change content of fields table */
            this.defineData(element.props.table);
        } else if (this.state.selectedTable === element) {
            /* select the same table */

            /* unselect */
            this.setState( {
                selectedTable: null,
                sourceSelectedTable: null,
                targetSelectedTable: null,
                data: []
            }, () => {
                //console.log(this.state.selectedTable);
            });
        } else if (this.state.sourceSelectedTable !== null) {
            /* source table is selected -> create arrow */

            this.createArrow(this.state.sourceSelectedTable.id, element.props.table.id)

            /* unselects tables */
            this.state.selectedTable.setState({clicked: false});
            element.setState({clicked: false});

            /* clean state */
            this.setState( {
                selectedTable: null,
                sourceSelectedTable: null,
                targetSelectedTable: null,
                data: []
            }, () => {
                //console.log(this.state.selectedTable);
            });
        } else {
            /* other target table is selected */

            /* unselects previous selected table */
            this.state.selectedTable.setState({clicked: false});

            /* clean state */
            this.setState( {
                selectedTable: element,
                sourceSelectedTable: null,
                targetSelectedTable: element.props.table,
                data: []
            }, () => {
                //console.log(this.state.selectedTable);
            });

            /* define fields table */
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
        const arrow = {
            id: 'arrow-' + this.state.i,
            start: startTable,
            end: endTable,
            color: "grey"
        }
        this.setState({ i: this.state.i + 1, arrows: this.state.arrows.concat(arrow)}, () => { /*console.log(this.state.arrows)*/})
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
        //console.log(arrow);
        const index = this.state.arrows.indexOf(arrow);

        if (this.state.selectedArrow === null) {
            /* no arrow is selected */

            let arrows = this.state.arrows
            arrows[index].color = "red";

            this.setState({
                selectedArrow: arrow,
                arrows: arrows
            });
        } else if(this.state.selectedArrow === arrow) {
            /* select the arrow previous selected to unselect */

            let arrows = this.state.arrows
            arrows[index].color = "grey";

            this.setState({
                selectedArrow: null,
                arrows: arrows
            });
        } else {
            /* select any other unselected arrow */

            /* unselect previous */
            this.cleanClickedArrows();

            /* select a new one */
            let arrows = this.state.arrows
            arrows[index].color = "grey";

            this.setState({
                selectedArrow: arrow,
                arrows: arrows
            });
        }
    }


    render() {
        return(
            <div>
                <Row>
                    <Col sm={3} md={3} lg={3}>
                        <h4>EHR Database</h4>

                        <div>
                            { this.state.sourceDB_tables.map((item, index) => {
                                return (
                                    <div key={index} >
                                        <EHRTable id={item.id} handleCallback={this.setSelectedSourceTable} table={item} />
                                    </div>
                                )
                            })}
                        </div>
                    </Col>

                    <Col sm={3} md={3} lg={3}>
                        <h4>CDM Database</h4>

                        { this.state.targetDB_tables.map((item, index) => {
                            return (
                                <div key={index}>
                                    <CDMTable id={item.id} handleCallback={this.setSelectedTargetTable} table={item} />
                                </div>
                            )
                        })}
                    </Col>
                    {
                        this.state.arrows.map(ar => (
                            <Xarrow start={ar.start} end={ar.end} key={ar.id}
                                    startAnchor="right" endAnchor="left" color={ar.color} strokeWidth={7.5} curveness={0.5}
                                    passProps={{
                                        onClick: () => this.selectArrow(ar)
                                        }}/>
                        ))
                    }

                    <Col sm={6} md={6} lg={6}>
                        <h4>Fields</h4>

                        <div>
                            <Table>
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
            </div>
        )
    }
}

export default Session;