import React from 'react'
import { TextField } from '@material-ui/core';

export default function Input(props) {

    const { variant, value, name, disabled, fullWidth, label, placeholder, rows, size, type, error=null, onChange, ...other } = props;

    return (
        <TextField
            multiline
            variant={ variant || "outlined" }
            label={ label }
            placeholder={ placeholder }
            name={ name }
            value={ value }
            fullWidth={ fullWidth || false }
            rows={ rows || 1}
            size={ size || "medium"}
            type={ type || "string" }
            disabled={disabled || false}
            onChange={onChange}
            {...other}
            {...(error && {error:true, helperText:error })} 
        />
    )
}