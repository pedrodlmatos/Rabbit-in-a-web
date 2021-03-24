import React from 'react'
import { TextField } from '@material-ui/core';

export default function Input(props) {

    const { variant, value, name, disabled, fullWidth, label, rows, size, type, error=null, onChange, ...other } = props;

    return (
        <TextField 
            variant="outlined"
            label={label}
            name={name}
            value={value}
            fullWidth={fullWidth}
            rows={rows}
            size={size}
            type={type}
            disabled={disabled}
            onChange={onChange}
            {...other}
            {...(error && {error:true, helperText:error })} 
        />
    )
}