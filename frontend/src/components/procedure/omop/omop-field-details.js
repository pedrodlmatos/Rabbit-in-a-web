import React from 'react'
import Controls from '../../controls/controls'
import { Grid, makeStyles } from '@material-ui/core'
import OMOPFieldTable from './omop-field-table'

const useStyles = makeStyles(theme => ({
    button: {
        marginTop: theme.spacing(1),
        marginBottom: theme.spacing(2),
    },
}))


export default function OMOPFieldDetails(props) {

    const classes = useStyles();
    const { field, fieldInfo, setFieldInfo, onCommentChange, saveComment, ehrFields, verify, connect } = props;
    const showFieldInfo = Object.keys(fieldInfo).length !== 0;

    return(
        <div>
            <Grid container>
                <Grid item xs={9} sm={9} md={9} lg={9}>
                    <h6><strong>Field name: </strong>{field.name}</h6>
                    <h6><strong>Field type: </strong>{field.type}</h6>
                </Grid>

                <Grid item xs={3} sm={3} md={3} lg={3}>
                    <Controls.DropdownCheckbox
                        value={[]}
                        label="Connect to"
                        options={ehrFields}
                        verifyMapping={verify}
                        onChange={connect}
                    />
                </Grid>
            </Grid>


            {showFieldInfo && (
                <OMOPFieldTable data={fieldInfo} setData={setFieldInfo} />
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
                onClick={saveComment}
            />
        </div>
    )
}