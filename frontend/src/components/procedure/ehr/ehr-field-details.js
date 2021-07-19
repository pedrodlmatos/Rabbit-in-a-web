import React from 'react'
import EHRFieldTable from './ehr-field-table'
import Controls from '../../controls/controls'
import { Grid, makeStyles } from '@material-ui/core'

const useStyles = makeStyles(theme => ({
    container: {
        marginTop: theme.spacing(2)
    },
    button: {
        marginTop: theme.spacing(1),
        marginBottom: theme.spacing(2),
    },
}))


export default function EHRFieldDetails(props) {

    const classes = useStyles();
    const { field, fieldInfo, setFieldInfo, onCommentChange, disabled, saveComment, omopFields, verify, connect } = props;
    const showFieldInfo = Object.keys(fieldInfo).length !== 0;

    return(
        <div className={classes.container}>
            <Grid container>
                {/* field name and data type */}
                <Grid item xs={9} sm={9} md={9} lg={9}>
                    <h6><strong>Field name: </strong>{field.name}</h6>
                    <h6><strong>Field type: </strong>{field.type}</h6>
                </Grid>

                {/* dropdown */}
                <Grid item xs={3} sm={3} md={3} lg={3}>
                    <Controls.DropdownCheckbox
                        value={[]}
                        label="Connect to"
                        options={omopFields}
                        verifyMapping={verify}
                        onChange={connect}
                    />
                </Grid>
            </Grid>


            {showFieldInfo && (
                <EHRFieldTable data={fieldInfo} setData={setFieldInfo} />
            )}


            <Controls.Input
                value={field.comment === null ? "" : field.comment}
                name="comment"
                fullWidth={true}
                label="Comment"
                placeholder="Edit field comment"
                rows={3}
                onChange={onCommentChange}
            />
            <Controls.Button
                className={classes.button}
                text="Save"
                disabled={disabled}
                onClick={saveComment}
            />
        </div>
    )
}