import React from 'react'
import { FormControl, InputLabel, MenuItem, Select as MSelect } from '@material-ui/core'

export default function Select(props) {

    const { name, label, value, error=null, onChange, options } = props;

    return (
        <FormControl variant="outlined" {...(error && {error:true})}>
            <InputLabel>{ label }</InputLabel>
            <MSelect
                label={label} name={name} value={value} onChange={onChange}>
                { options.map(item => (
                    <MenuItem key={item.id} value={item.id}>{item.name}</MenuItem>
                ))}
            </MSelect>
        </FormControl>
    )
}