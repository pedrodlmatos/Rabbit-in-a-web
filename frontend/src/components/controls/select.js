import React from 'react'
import { FormControl, InputLabel, Select as MSelect, MenuItem, FormHelperText } from '@material-ui/core';

export default function Select(props) {

    const { name, label, value, error=null, onChange, options } = props;

    return (
        <FormControl variant="outlined" {...(error && {error:true})}>
            <InputLabel>{ label }</InputLabel>
            <MSelect label={label} name={name} value={value} onChange={onChange}>
                <MenuItem value="">None</MenuItem>
                { options.map(item => (
                    <MenuItem key={item.id} value={value.id}>{item.name}</MenuItem>
                ))}
            </MSelect>
            {error && <FormHelperText>{error}</FormHelperText>}
        </FormControl>
    )
}