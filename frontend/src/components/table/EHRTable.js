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
     * Changes state both in this component and in its parent
     * 
     * @param table clicked EHR table
     */

    selectSourceTable = (table) => {
        this.setState( { clicked: !this.state.clicked });
        this.props.handleCallback(this);
    }


    /**
     * Changes state to unselect table 
     */

    unselect() { this.setState( { clicked: false }) }


    render() {
        return(
            <div id={this.state.table.name} className={this.state.clicked ? "sourceTableRectangleSelected" : "sourceTableRectangle" } onClick={() => this.selectSourceTable(this.state.table)}>
                { this.state.table.name }
            </div>
        )
    }

}

export default EHRTable;