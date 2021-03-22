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
        display: 'flex',
        justifyContent: 'center',
        alignContent: 'center',
        flexDirection: 'column',
        textAlign: 'center'
    },
    selected: {
        width: 200,
        height: 50,
        position: 'relative',
        marginTop: theme.spacing(1),
        borderColor: 'black',
        borderStyle: 'dashed',
        backgroundColor: 'blue',
        display: 'flex',
        justifyContent: 'center',
        alignContent: 'center',
        flexDirection: 'column',
        textAlign: 'center'
    }
    

}))

export default function OMOPTable(props) {
    const classes = useStyles();
    const { id, table, handleSourceTableSelection } = props;
    const [clicked, setClicked] = useState(false);


    return(
        <div id={table.name} className={clicked ? classes.selected : classes.unselected}>
            { table.name }
        </div>    
    )
}