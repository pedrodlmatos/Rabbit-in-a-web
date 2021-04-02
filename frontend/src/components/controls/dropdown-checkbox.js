import React from 'react'
import { FormControl, FormLabel, RadioGroup as MRadioGroup, FormControlLabel, Radio } from '@material-ui/core';

export default function DropdownCheckbox(props) {

    const { name, label, value, onChange, items } = props;

    return (
        <FormControl>
            <FormLabel>{ label }</FormLabel>
            <MRadioGroup row name={name} value={value} onChange={onChange}>
                { items.map(item => (
                    <FormControlLabel key={item.id} value={item.id} control={ <Radio /> } label={item.title} />
                ))}
            </MRadioGroup>
        </FormControl>
    )
}