/*
import React, { useState } from 'react'
import { makeStyles } from '@material-ui/core';

const useStyles = makeStyles(theme => ({
    tableSelected: {
        width: "200px",
        height: "50px",
        position: "relative",
        marginTop: theme.spacing(1),
        borderColor: "black",
        borderStyle: "dashed",
        backgroundColor: "lightskyblue",
        display: "flex",
        justifyContent: "center",
        alignContent: "center",
        flexDirection: "column",
        textAlign: "center"
    },
    tableUnselected: {
        width: "200px",
        height: "50px",
        position: "relative",
        marginTop: theme.spacing(1),
        borderColor: "darkblue",
        borderStyle: "solid",
        backgroundColor: "lightskyblue",
        display: "flex",
        justifyContent: "center",
        alignContent: "center",
        flexDirection: "column",
        textAlign: "center"
    }
}))


export default function CDMTable(props) {
    const classes = useStyles();
    const { handleCallback, table } = props;
    const [clicked, setClicked] = useState(false);


    const selectTable = () => {
        setClicked(!clicked);
        handleCallback(table);
    }


    return(
        <div id={table.name} className={clicked ? classes.tableSelected : classes.tableUnselected } onClick={selectTable}>
            { table.name }
        </div>
    )
}
*/


import React, {Component} from "react";
import "./CDMTable.css";

class CDMTable extends Component {

    constructor(props) {
        super(props);
        this.state = {
            table: props.table,
            clicked: false
        }
    }


    /**
     *
     * @param table
     */
    selectTargetTable = (table) => {
        this.setState( {
            clicked: !this.state.clicked
        });

        this.props.handleCallback(this);
    }


    unselect() {
        this.setState( {
            clicked: false
        })
    }

    render() {
        return(
            <div id={this.state.table.name} className={this.state.clicked ? "targetTableRectangleSelected" : "targetTableRectangle" } onClick={ () => this.selectTargetTable(this.state.table) }>
                { this.props.table.name }
            </div>
        )
    }

}

export default CDMTable;
