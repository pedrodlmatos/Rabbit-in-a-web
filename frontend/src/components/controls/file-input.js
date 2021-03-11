import React from 'react'
import { Input } from '@material-ui/core';

export default function FileInput(props) {
    
    const { name, label, value, error=null, onChange, ...other } = props;

    return (
        <Input 
            label={label} 
            name={name} 
            value={value} 
            onChange={onChange} 
            {...other} 
            {...(error && {error:true, helperText:error})}
        />
    )
}