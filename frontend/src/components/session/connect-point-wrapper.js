import React, { useRef, useState } from 'react'
import { Fragment } from 'react';
import Xarrow from 'react-xarrows/lib';


const connectPointStyle = {
    position: "absolute",
    width: 15,
    height: 15,
    borderRadius: "50%",
    background: "grey"
}

const connectPointOffset = {
    left: { left: 0, top: "50%", transform: "translate(-50%, -50%)" },
    right: { left: "100%", top: "50%", transform: "translate(-50%, -50%)" },
    top: { left: "50%", top: 0, transform: "translate(-50%, -50%)" },
    bottom: { left: "50%", top: "100%", transform: "translate(-50%, -50%)" }
};


export default function ConnectPointsWrapper(props) {

    const { id, name, handler } = props;
    const ref1 = useRef();
    const [position, setPosition] = useState({});
    const [beingDragged, setBeingDragged] = useState(false);

    return(
        <Fragment>
            <div 
                ref={ref1}
                className="connectPoint"
                style={{...connectPointStyle, ...connectPointOffset[handler], ...position}}
                draggable
                onMouseDown={e => e.stopPropagation()}
                onDragStart={e => {
                    setBeingDragged(true);
                    e.dataTransfer.setData("source", id);
                }}
                onDrag={e => {
                    setPosition({
                        position: "fixed",
                        left: e.clientX,
                        top: e.clientY,
                        transform: "none",
                        opacity: 0
                    });
                }}
                onDragEnd={e => {
                    setPosition({});
                    setBeingDragged(false);
                }}
            />
            {beingDragged ? <Xarrow start={name} end={ref1} color="grey" /> : null}
        </Fragment>
    )
    
}
