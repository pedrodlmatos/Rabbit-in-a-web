import React from 'react'
import { Input, Button, InputLabel } from '@material-ui/core';

export default function FileInput(props) {
    
    const { name, type, placeholder, error=null, onChange, ...other } = props;

    return ( 
        <InputLabel>
            <Input 
                name={name}
                type={type}
                accept=".xlsx"
                placeholder={placeholder}
                onChange={onChange}
                hidden
                {...other} />
            <Button 
                className="btn-choose"
                variant="outlined"
                component="span" 
                error={error}
            >
                    Choose EHR Scan
            </Button>
        </InputLabel>
        
    )
}