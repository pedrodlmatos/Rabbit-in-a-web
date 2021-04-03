import React from 'react'
import { makeStyles, Tooltip } from '@material-ui/core';

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
    const { id, table, clicked, handleSelection } = props;
    const classes = useStyles(props);

    const selectTable = () => {
        handleSelection(table);
    }


    return(
        <Tooltip 
            title="Select first an EHR table and then an OMOP CDM table" 
            placement="right-end"
            enterDelay={1000}
        >
            <div id={id} className={clicked ? classes.selected : classes.unselected} onClick={selectTable}>
                { table.name }
            </div> 
        </Tooltip>     
    )
}
