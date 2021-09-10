import React from 'react'
import { Button, Input, InputLabel, makeStyles } from '@material-ui/core'

const useStyles = makeStyles(theme => ({
    fileInput: {
        marginTop: theme.spacing(1),
        marginLeft: theme.spacing(1),
        width: "250px",
        height: "50px"
    }
}))


export default function FileInput(props) {
    
    const { name, text, type, placeholder, onChange, ...other } = props;
    const classes = useStyles();

    return ( 
        <InputLabel>
            <Input
                name={name}
                type={type}
                placeholder={placeholder}
                onChange={onChange}
                hidden
                {...other} />
            <Button
                className={classes.fileInput}
                variant="outlined"
                component="span"
            >
                {text}
            </Button>
        </InputLabel>
        
    )
}