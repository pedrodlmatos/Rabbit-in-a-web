import Controls from '../controls/controls';
import { makeStyles } from '@material-ui/core';

const useStyles = makeStyles(theme => ({
    box: {
        marginTop: theme.spacing(2)
    },
    button: {
        marginTop: theme.spacing(1),
        marginBottom: theme.spacing(2),
    }
}))

export default function TableMappingLogic(props) {

    const {value, disabled, onChange, save} = props;
    const classes = useStyles();

    return(
        <div className={classes.box}>
            <Controls.Input 
                value={value === null ? '' : value}
                name="comment"
                fullWidth={true}
                label="Table mapping logic"
                placeholder="Edit mapping logic"
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