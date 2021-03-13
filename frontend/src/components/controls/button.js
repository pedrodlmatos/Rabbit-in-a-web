import React from 'react'
import { Button as MButton, makeStyles } from '@material-ui/core';

const useStyles = makeStyles(theme => ({
    root: {
        margin: theme.spacing(0.5)
    },
    label: {
        textTransform: 'none'
    }
}))

export default function Button(props) {
    const { text, size, color, children, variant, disabled, onClick, ...other } = props;
    const classes = useStyles();

    return (
        <MButton 
            variant={ variant || "contained" }
            size={ size || "medium" }
            color={ color || "primary" }
            disabled={disabled || false}
            onClick={ onClick }
            { ...other }
            classes={{ root: classes.root, label: classes.label }}>{ text } { children }</MButton>
    )
}