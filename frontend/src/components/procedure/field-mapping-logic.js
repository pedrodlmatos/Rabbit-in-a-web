import Controls from '../controls/controls';
import { makeStyles } from '@material-ui/core';

const useStyles = makeStyles(theme => ({
    button: {
        marginTop: theme.spacing(1),
        marginBottom: theme.spacing(2),
    }
}))

export default function FieldMappingLogic(props) {

    const {value, disabled, onChange, save} = props;
    const classes = useStyles();

    return(
        <div>
            <Controls.Input 
                value={value === null ? '' : value}
                name="comment"
                fullWidth={true}
                label="Field mapping logic"
                placeholder="Edit field mapping logic"
                rows={3} 
                onChange={onChange}
            />
            <Controls.Button
                className={classes.button}
                text="Save"
                disabled={disabled}
                onClick={save}
            />
        </div>
        
    )
}