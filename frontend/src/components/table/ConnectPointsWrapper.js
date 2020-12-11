import React, {Component, Fragment} from "react";

const connectPointStyle = {
    position: "absolute",
    width: 15,
    height: 15,
    borderRadius: "50%",
    background: "black"
};

const connectPointOffset = {
    "left": { left: 0, top: "50%", transform: "translate(-50%, -50%)" },
    right: { left: "100%", top: "50%", transform: "translate(-50%, -50%)" },
    top: { left: "50%", top: 0, transform: "translate(-50%, -50%)" },
    bottom: { left: "50%", top: "100%", transform: "translate(-50%, -50%)" }
};

class ConnectPointsWrapper extends Component {

    constructor(props) {
        super(props);

        this.state = {
            'table_id': props.table,
            'handler': props.handler
        }
    }

    render() {
        const ref1 = React.createRef();

        return(
            <Fragment>
                <div ref={ref1} className="connectPoint" style={{ ...connectPointStyle, ...connectPointOffset[this.state.handler]}}
                     draggable onDragStart={e => {
                         e.dataTransfer.setData("arrow", this.state.table_id)}}>
                </div>
            </Fragment>
        )
    }
}

export default ConnectPointsWrapper

/**/