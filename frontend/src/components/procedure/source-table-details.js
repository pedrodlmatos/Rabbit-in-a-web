/**
 * Table with information about a table from the EHR database (name, number of rows)
 * and its fields (field names, data type and description)
 */

import { makeStyles } from '@material-ui/core';
import React from 'react'
import Controls from '../controls/controls';
import InfoTable from '../info-table/info-table';

const useStyles = makeStyles(theme => ({
    showButton: {
        marginTop: theme.spacing(1),
        visibility: 'false'
    }
}))

export default function SourceTableDetails(props) {
    const classes = useStyles();
    const { table, columns, data, onChange, disabled, save, omopTables, verify, connect } = props;

    return(
        <div>
            <h6><strong>Table: </strong>{ table.name}</h6>
            <h6><strong>Number of rows &gt;= </strong>{ table.rowCount === null ? 0 : table.rowCount }</h6>

            <InfoTable columns={columns} data={data} />

            <Controls.Input
                value={ table.comment === null ? "" : table.comment }
                name="comment"
                fullWidth={true}
                label="Comment"
                placeholder="Edit table comment"
                rows={5}
                onChange={onChange}
            />
            <Controls.Button
                className={classes.showButton}
                disabled={disabled}
                text="Save"
                onClick={save}
            />

            <Controls.DropdownCheckbox
                value={[]}
                label="Connect to"
                options={omopTables}
                verifyMapping={verify}
                onChange={connect}
            />
        </div>
    )
}   