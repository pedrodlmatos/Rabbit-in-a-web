import React, {Component} from "react";
import "./CDMField.css";

class CDMField extends Component {

    constructor(props) {
        super(props);
        this.state = {
            field: props.field,
            clicked: false
        }
    }


    /**
     *
     * @param field
     */
    selectTargetField = (field) => {
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
            <div id={this.state.field.name} className={this.state.clicked ? "targetFieldRectangleSelected" : "targetFieldRectangle" } onClick={ () => this.selectTargetField(this.state.field) }>
                <p className="cdmFieldText">
                    { this.props.field.name }
                </p>
            </div>
        )
    }

}

export default CDMField;