import React from 'react'
import { Button as MButton, makeStyles } from '@material-ui/core'

const useStyles = makeStyles(theme => ({
    label: {
        textTransform: 'none'
    }
}))

export default function DownloadButton(props) {
    const { text, size, color, children, variant, disabled, onClick, download, ...other } = props;
    const classes = useStyles();

    return (
        <MButton 
            variant={ variant || "contained" }
            size={ size || "medium" }
            color={ color || "primary" }
            disabled={disabled || false}
            onClick={ onClick }
            { ...other }
            download={download}
            classes={{ label: classes.label }}>{ children } { text }</MButton>
    )
}