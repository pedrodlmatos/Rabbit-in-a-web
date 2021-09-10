/**
 * Table with information about a table from the EHR database (name, number of rows)
 * and its fields (field names, data type and description)
 */

import { Grid, makeStyles } from '@material-ui/core'
import React from 'react'
import Controls from '../../controls/controls'
import InfoTable from '../info-table'

const useStyles = makeStyles(theme => ({
    showButton: {
        marginTop: theme.spacing(1),
        visibility: 'false'
    }
}))

export default function EHRTableDetails(props) {
    const classes = useStyles();
    const { table, columns, data, onChange, disabled, save, omopTables, verify, connect } = props;

    return(
        <div>
            <Grid container>
                {/* Table name */}
                <Grid item xs={9} sm={9} md={9} lg={9}>
                    <h6><strong>Table: </strong>{ table.name}</h6>
                    <h6><strong>Number of rows &gt;= </strong>{ table.rowCount === null ? 0 : table.rowCount }</h6>
                </Grid>

                {/* Dropbox to connect to other tables */}
                <Grid item xs={3} sm={3} md={3} lg={3}>
                    <Controls.DropdownCheckbox
                        value={[]}
                        label="Connect to"
                        options={omopTables}
                        verifyMapping={verify}
                        onChange={connect}
                    />
                </Grid>
            </Grid>

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
        </div>
    )
}   