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