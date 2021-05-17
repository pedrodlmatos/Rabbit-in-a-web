import React, { useState, useEffect } from 'react';
import { makeStyles, CircularProgress, withStyles, Table, TableBody, TableContainer, TableCell, TableRow, Paper, TableHead } from '@material-ui/core'
import ETLService from "../../../services/etl-list-service";
import { CDMVersions } from '../../../services/CDMVersions';
import Controls from '../../controls/controls';

const StyledTableCell = withStyles((theme) => ({
    head: {
        backgroundColor: theme.palette.common.black,
        color: theme.palette.common.white
    },
    body: {
        fontSize: 14
    }
}))(TableCell);

const StyledTableRow = withStyles((theme) => ({
    root: {
        '&:nth-of-type(odd)': {
            backgroundColor: theme.palette.action.hover
        }
    }
}))(TableRow)

const useStyles = makeStyles(theme => ({
    pageContainer: {
        margin: theme.spacing(1),
        padding: theme.spacing(1)
    },
    title: {
        marginBottom: theme.spacing(5),
        fontSize: "12"
    },
    button: {
        marginRight: theme.spacing(1)
    },
    iconButton: {
        width: "50px",
        height: "50px"
    },
    table: {
        maxHeight: 500,
        minWidth: 700,
        marginTop: theme.spacing(3),
        marginBottom: theme.spacing(5)
    }
}))


export default function AdminProcedureList() {

    const classes = useStyles();
    const [loading, setLoading] = useState(true);
    const [procedures, setProcedures] = useState([]);
    const [loadingDelete, setLoadingDelete] = useState(false);

    const columns = React.useMemo(() => [
        { Header: 'Name', accessor: 'field' },
        { Header: 'EHR', accessor: 'type' },
        { Header: 'OMOP CDM', accessor: 'description' },
        { Header: 'Users', accessor: 'users'},
        { Header: '', accessor: ''},
        { Header: '', accessor: ''},
    ], [])


    /**
     * Sends GET request to API to retrieve all ETL procedures
     */
    
    useEffect(() => {
        ETLService.getAllETL().then(response => {
            setProcedures(response.data);
            setLoading(false);
        }).catch(response => {
            console.log(response);
        })
    }, []);


    const deleteETLProcedure = (id) => {
        setLoadingDelete(true);
        ETLService.deleteETLProcedure(id).then(response => {
            const index = procedures.findIndex(x => x.id === id);
            const etl = procedures.splice(index, 1);
            setLoadingDelete(false);
            setProcedures(etl);
        }).catch(e => { console.log(e)})
    }


    return(
        <div className={classes.pageContainer}>
            { loading ? (
                <CircularProgress color="primary" variant="indeterminate" size={40} />
            ) : (
                <div>
                    <h1 className={classes.title}>All ETL Procedures</h1>

                    <TableContainer className={classes.table} component={Paper}>
                        <Table stickyHeader aria-label="customized table">
                            <TableHead>
                                <TableRow>
                                    {columns.map(column => (
                                        <StyledTableCell align="left">
                                            {column.Header}
                                        </StyledTableCell>
                                    ))}
                                </TableRow>
                            </TableHead>

                            <TableBody>
                                {procedures.map(procedure => {
                                    return(
                                        <StyledTableRow>
                                            <StyledTableCell component="th" scope="row" align="left">
                                                {procedure.name}
                                            </StyledTableCell>
                                            
                                            <StyledTableCell component="th" scope="row" align="left">
                                                {procedure.sourceDatabase.databaseName}
                                            </StyledTableCell>
                                            
                                            <StyledTableCell component="th" scope="row" align="left">
                                                {CDMVersions.filter(function(cdm) { return cdm.id === procedure.targetDatabase.databaseName })[0].name}
                                            </StyledTableCell>
                                            
                                            <StyledTableCell component="th" scope="row" align="left">{procedure.name}</StyledTableCell>

                                            <StyledTableCell component="th" scope="row" align="left">
                                                <Controls.Button text="Access" />
                                            </StyledTableCell>

                                            <StyledTableCell component="th" scope="row" align="left">
                                                <Controls.Button  
                                                    color="secondary" 
                                                    disabled={loadingDelete}
                                                    onClick={() => deleteETLProcedure(procedure.id)}
                                                >   
                                                    Delete
                                                    {loadingDelete && (
                                                        <CircularProgress color="primary" variant="indeterminate" size={10}/>
                                                    )}
                                                </Controls.Button>
                                            </StyledTableCell>
                                        </StyledTableRow>
                                    )
                                })}

                            </TableBody>
                        </Table>

                    </TableContainer>
                    
                    {/*
                    <Grid container spacing={4}>
                        { procedures.map(session =>
                            <Grid key={session.id} item xs={12} sm={12} md={2} lg={2}>
                                <ProcedureCard
                                    id={session.id}
                                    name={session.name}
                                    ehr={session.sourceDatabase.databaseName}
                                    omop={session.targetDatabase.databaseName}
                                />
                            </Grid>
                        )}
                        </Grid>
                        */}
                </div>
            )}
        </div>
    )
}