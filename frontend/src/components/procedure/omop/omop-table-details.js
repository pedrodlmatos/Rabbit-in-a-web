/**
 * Table with information about a table from the OMOP CDM (name)
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

export default function OMOPTableDetails(props) {
    const classes = useStyles();
    const { table, columns, data, onChange, disabled, save, ehrTables, verify, connect } = props;

    return(
        <div>
            <Grid container>
                <Grid style={{position: 'relative'}} item xs={9} sm={9} md={9} lg={9}>
                    <h6 style={{position:'absolute', bottom: 0}}><strong>Table: </strong>{table.name}</h6>
                </Grid>

                <Grid item xs={3} sm={3} md={3} lg={3}>
                    <Controls.DropdownCheckbox
                        value={[]}
                        label="Connect to"
                        options={ehrTables}
                        verifyMapping={verify}
                        onChange={connect}
                    />
                </Grid>
            </Grid>

            <Grid container>
                <Grid item xs={12} sm={12} md={12} lg={12}>
                    <br/>
                    <InfoTable columns={columns} data={data} />
                    <br/>
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
                </Grid>
            </Grid>




        </div>
    )
}   