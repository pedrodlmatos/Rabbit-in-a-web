import React from 'react'
import { Input, Button, InputLabel } from '@material-ui/core';

export default function FileInput(props) {
    
    const { name, type, onChange, ...other } = props;

    return ( 
        <InputLabel>
            <Input 
                name={name}
                type={type}
                onChange={onChange}
                hidden
                {...other} />
            <Button 
                className="btn-choose"
                variant="outlined"
                component="span" >
                    Choose EHR Scan
            </Button>
        </InputLabel>
        
    )
}