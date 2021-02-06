import React, {Component} from "react";
import "./EHRField.css"

class EHRField extends Component {

    constructor(props) {
        super(props);

        this.state = {
            field: this.props.field,
            clicked: false
        }
    }

    /**
     *
     * @param field
     */
    selectSourceField = (field) => {
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
            <div id={this.state.field.name} className={this.state.clicked ? "sourceFieldRectangleSelected" : "sourceFieldRectangle" } onClick={() => this.selectSourceField(this.state.field)}>
                <p className="sourceFieldText">
                    { this.state.field.name }
                </p>
            </div>
        )
    }

}

export default EHRField;