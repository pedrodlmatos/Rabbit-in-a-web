import React from 'react'
import { FormControl, FormControlLabel, FormLabel, Radio, RadioGroup as MRadioGroup } from '@material-ui/core'

export default function RadioGroup(props) {

    const { name, label, value, onChange, items } = props;

    return (
        <FormControl>
            <FormLabel>{ label }</FormLabel>
            <MRadioGroup row name={name} value={value} onChange={onChange}>
                { items.map(item => (
                    <FormControlLabel key={item.id} value={item.id} control={ <Radio /> } label={item.name} />
                ))}
            </MRadioGroup>
        </FormControl>
    )
}