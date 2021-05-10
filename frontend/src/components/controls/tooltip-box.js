import React, { useRef } from 'react'
import { makeStyles, Tooltip } from '@material-ui/core';
import ConnectPointsWrapper from '../procedure/connect-point-wrapper';

const useStyles = makeStyles(theme => ({
    unselected: {
        width: 200,
        height: 50,
        position: 'relative',
        marginTop: theme.spacing(1),
        borderColor: props => props.border,
        borderStyle: 'solid',
        backgroundColor: props => props.color,
        display: 'table'
    },
    selected: {
        width: 200,
        height: 50,
        position: 'relative',
        marginTop: theme.spacing(1),
        borderColor: 'black',
        borderStyle: 'dashed',
        backgroundColor: props => props.color,
        display: 'table'
    },
    text: {
        textAlign: 'center',
        verticalAlign: 'middle',
        display: 'table-cell',
        justifyContent: 'center',
        fontSize: 15,
        flexDirection: 'column',
        alignContent: 'center',
        wordBreak: "break-all"
    }
}))

export default function TooltipBox(props) {
    const { element, id, handler, clicked, help, position, handleSelection, createMapping } = props;
    const ref0 = useRef();
    const classes = useStyles(props);

    const selectTable = () => {
        handleSelection(element);
    }


    return(
        <Tooltip 
            title={help}
            placement={position}
            enterDelay={1000}
        >
            <div 
                id={id}
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
                <p className={classes.text}>{ element.name }</p>
                <ConnectPointsWrapper id={element.id} name={element.name} handler={handler} />
            </div> 
        </Tooltip>
    )
}
