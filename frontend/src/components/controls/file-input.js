import React from 'react'
import { Input, Button, InputLabel, makeStyles } from '@material-ui/core'

const useStyles = makeStyles(theme => ({
    fileInput: {
        marginTop: theme.spacing(1),
        width: "250px"
    }
}))


export default function FileInput(props) {
    
    const { name, type, placeholder, error=null, onChange, ...other } = props;
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
                Choose EHR Scan
            </Button>
        </InputLabel>
        
    )
}