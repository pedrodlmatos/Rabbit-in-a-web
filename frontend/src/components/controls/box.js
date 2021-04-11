import React, { useRef } from 'react'
import { makeStyles, Tooltip } from '@material-ui/core';
import Draggable from 'react-draggable';
import ConnectPointsWrapper from '../session/connect-point-wrapper';

const useStyles = makeStyles(theme => ({
    unselected: {
        width: 200,
        height: 50,
        position: 'relative',
        marginTop: theme.spacing(1),
        borderColor: props => props.border,
        borderStyle: 'solid',
        backgroundColor: props => props.color,
        fontSize: 15,
        display: 'flex',
        justifyContent: 'center',
        alignContent: 'center',
        flexDirection: 'column',
        textAlign: 'center',
        wordWrap: 'break-work'
        
    },
    selected: {
        width: 200,
        height: 50,
        position: 'relative',
        marginTop: theme.spacing(1),
        borderColor: 'black',
        borderStyle: 'dashed',
        backgroundColor: props => props.color,
        fontSize: 15,
        display: 'flex',
        justifyContent: 'center',
        alignContent: 'center',
        flexDirection: 'column',
        textAlign: 'center',
        wordWrap: 'break-work'
    }
}))

export default function TooltipBox(props) {
    const { id, table, handler, clicked, handleSelection, createMapping } = props;
    const dragRef = useRef();
    const boxRef = useRef();
    const classes = useStyles(props);
    const tableId = table.id;

    const selectTable = () => {
        handleSelection(table);
    }


    return(
        <Draggable ref={dragRef} onDrag={e => {console.log(e)}}>
            <Tooltip 
                title="Select first an EHR table and then an OMOP CDM table" 
                placement="right-end"
                enterDelay={1000}
            >
                <div 
                    id={id} 
                    ref={boxRef} 
                    className={clicked ? classes.selected : classes.unselected} 
                    onClick={selectTable}
                    onDragOver={e => {
                        if (e.dataTransfer.getData("arrow") === id) {
                            console.log(e.dataTransfer.getData("arrow"), id);
                        } else {
                            const refs = { start: e.dataTransfer.getData("table"), end: table.id }
                            console.log(refs);
                            createMapping(e.dataTransfer.getData("table"), table.id);
                        }
                    }}
                >
                    { table.name }
                    <ConnectPointsWrapper {...{ id, tableId, handler, dragRef, boxRef }} />
                </div> 
                
            </Tooltip>
        </Draggable>
        /*
        
            
        </Tooltip>     
        */
    )
}
