import React, {Component} from "react";
import "./EHRTable.css"

class EHRTable extends Component {

    constructor(props) {
        super(props);

        this.state = {
            table: this.props.table,
            clicked: false
        }
    }

    /**
     *
     * @param table
     */
    selectSourceTable = (table) => {
        /*console.log(table);*/
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
            <div id={this.state.table.name} className={this.state.clicked ? "sourceTableRectangleSelected" : "sourceTableRectangle" } onClick={() => this.selectSourceTable(this.state.table)}>
                { this.state.table.name }
            </div>
        )
    }

}

export default EHRTable;