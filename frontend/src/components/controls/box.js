import React, { useRef } from 'react'
import { makeStyles, Tooltip } from '@material-ui/core';
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
    const { element, handler, clicked, help, position, handleSelection, createMapping } = props;
    const ref0 = useRef();
    const classes = useStyles(props);

    const selectTable = () => {
        console.log(element);
        handleSelection(element);
    }


    return(
        <Tooltip 
            title={help}
            placement={position}
            enterDelay={1000}
        >
            <div 
                id={element.name} 
                ref={ref0} 
                className={clicked ? classes.selected : classes.unselected} 
                onClick={selectTable}
                onDragOver={e => e.preventDefault()}
                onDrop={e => {
                    if (e.dataTransfer.getData("source") !== element.id) {
                        createMapping(e.dataTransfer.getData("source"), element.id);
                    }
                }}
            >
                { element.name }
                <ConnectPointsWrapper id={element.id} name={element.name} handler={handler} />
            </div> 
        </Tooltip>
    )
}
