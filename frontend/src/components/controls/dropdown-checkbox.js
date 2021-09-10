import React from 'react'
import {
  Checkbox,
  FormControl,
  InputLabel,
  ListItemText,
  makeStyles,
  MenuItem,
  Select as MSelect
} from '@material-ui/core'

const useStyles = makeStyles(theme => ({
    formControl: {
        margin: theme.spacing(1),
        minWidth: 200,
        maxWidth: 400,
    }
}))


export default function DropdownCheckBox(props) {

    const { value, label, error=null, onChange, options, verifyMapping } = props;
    const classes = useStyles();

    return (
        <FormControl className={classes.formControl} variant="filled" {...(error && {error:true})}>
            <InputLabel>{ label }</InputLabel>
            <MSelect multiple value={value} label={label} onChange={onChange}>
                { options.map(item => (
                    <MenuItem key={item.id} value={item.id}>
                        <ListItemText primary={item.name} />
                        <Checkbox checked={verifyMapping(item.id)} />
                    </MenuItem>
                ))}
            </MSelect>
        </FormControl>
    )
}