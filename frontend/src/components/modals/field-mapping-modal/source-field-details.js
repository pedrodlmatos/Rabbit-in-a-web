import React from 'react'
import SourceFieldTable from './source-field-table'
import Controls from '../../controls/controls'
import { makeStyles } from '@material-ui/core'

const useStyles = makeStyles(theme => ({
    button: {
        marginTop: theme.spacing(1),
        marginBottom: theme.spacing(2),
    },
}))


export default function SourceFieldDetails(props) {

    const classes = useStyles();
    const { field, fieldInfo, setFieldInfo, onCommentChange, saveComment, omopFields, verify, connect } = props;
    const showFieldInfo = Object.keys(fieldInfo).length !== 0;
    console.log(omopFields)

    return(
        <div>
            <h6><strong>Field name: </strong>{field.name}</h6>
            <h6><strong>Field type: </strong>{field.type}</h6>

            {showFieldInfo && (
                <SourceFieldTable data={fieldInfo} setData={setFieldInfo} />
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
                options={omopFields}
                verifyMapping={verify}
                onChange={connect}
            />
        </div>

    )
}