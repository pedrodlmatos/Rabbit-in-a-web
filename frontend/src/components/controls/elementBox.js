import React from 'react'
import { makeStyles } from '@material-ui/core';

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
        
    }
}))

export default function ElementBox(props) {
    const { id, element } = props;
    const classes = useStyles(props);


    return(
        <div id={id} className={classes.unselected} >
            { element.name }
        </div> 
    )
}
