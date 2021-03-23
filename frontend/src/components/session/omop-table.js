import React, { useState } from 'react'
import { makeStyles } from '@material-ui/core';

const useStyles = makeStyles(theme => ({
    unselected: {
        width: 200,
        height: 50,
        position: 'relative',
        marginTop: theme.spacing(1),
        borderColor: 'darkblue',
        borderStyle: 'solid',
        backgroundColor: 'lightblue',
        fontSize: 15,
        display: 'flex',
        justifyContent: 'center',
        alignContent: 'center',
        flexDirection: 'column',
        textAlign: 'center',
        wordWrap: 'break-word'
    },
    selected: {
        width: 200,
        height: 50,
        position: 'relative',
        marginTop: theme.spacing(1),
        borderColor: 'black',
        borderStyle: 'dashed',
        backgroundColor: 'lightblue',
        fontSize: 15,
        display: 'flex',
        justifyContent: 'center',
        alignContent: 'center',
        flexDirection: 'column',
        textAlign: 'center',
        wordWrap: 'break-word'
    }
    

}))

export default function OMOPTable(props) {
    const classes = useStyles();
    const { id, table, clicked, handleTargetTableSelection } = props;

    const selectTable = () => {
        handleTargetTableSelection(table);
    }

    return(
        <div id={id} className={clicked ? classes.selected : classes.unselected} onClick={selectTable}>
            { table.name }
        </div>    
    )
}