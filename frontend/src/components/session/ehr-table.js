import React from 'react'
import { makeStyles } from '@material-ui/core';

const useStyles = makeStyles(theme => ({
    unselected: {
        width: 200,
        height: 50,
        position: 'relative',
        marginTop: theme.spacing(1),
        borderColor: 'darkred',
        borderStyle: 'solid',
        backgroundColor: 'orange',
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
        backgroundColor: 'orange',
        fontSize: 15,
        display: 'flex',
        justifyContent: 'center',
        alignContent: 'center',
        flexDirection: 'column',
        textAlign: 'center',
        wordWrap: 'break-work'
    }
}))

export default function EHRTable(props) {
    const classes = useStyles();
    const { id, table, clicked, handleSourceTableSelection } = props;

    const selectTable = () => {
        handleSourceTableSelection(table);
    }


    return(
        <div id={id} className={clicked ? classes.selected : classes.unselected} onClick={selectTable}>
            { table.name }
        </div>    
    )
}
