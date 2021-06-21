import React from 'react'
import Controls from '../../controls/controls'
import { makeStyles } from '@material-ui/core'
import TargetFieldTable from './target-field-table'

const useStyles = makeStyles(theme => ({
    button: {
        marginTop: theme.spacing(1),
        marginBottom: theme.spacing(2),
    },
}))


export default function TargetFieldDetails(props) {

    const classes = useStyles();
    const { field, fieldInfo, setFieldInfo, onCommentChange, saveComment, ehrFields, verify, connect } = props;
    const showFieldInfo = Object.keys(fieldInfo).length !== 0;

    return(
        <div>
            <h6><strong>Field name: </strong>{field.name}</h6>
            <h6><strong>Field type: </strong>{field.type}</h6>

            {showFieldInfo && (
                <TargetFieldTable data={fieldInfo} setData={setFieldInfo} />
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

            <Controls.DropdownCheckbox
                value={[]}
                label="Connect to"
                options={ehrFields}
                verifyMapping={verify}
                onChange={connect}
            />
        </div>

    )
}