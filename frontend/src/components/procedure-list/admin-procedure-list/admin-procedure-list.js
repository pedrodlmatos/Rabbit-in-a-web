import React, { useState, useEffect } from 'react';
import {
    makeStyles,
    CircularProgress,
    Paper,
    Table,
    TableHead,
    TableRow,
    TableBody,
    Checkbox,
    TableContainer, withStyles, TableCell, createStyles, TableSortLabel
} from '@material-ui/core'
import ETLService from "../../../services/etl-list-service";
import { CDMVersions } from '../../../services/CDMVersions';
import moment from 'moment';
import Controls from '../../controls/controls'


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

const StyledTableCell = withStyles((theme) => ({
    head: {
        backgroundColor: theme.palette.common.black,
        color: theme.palette.common.white
    },
    body: {
        fontSize: 14,
    }
}))(TableCell);

const StyledTableRow = withStyles((theme) => ({
    root: {
        '&:nth-of-type(odd)': {
            backgroundColor: theme.palette.action.hover
        }
    }
}))(TableRow)


const StyledTableSortLabel = withStyles((theme) =>
    createStyles({
        root: {
            color: 'white',
            "&:hover": {
                color: 'white',
            },
            '&$active': {
                color: 'white',
            },
        },
        active: {},
        icon: {
            color: 'inherit !important'
        },
    })
)(TableSortLabel);


export default function AdminProcedureList() {

    const classes = useStyles();
    const [loading, setLoading] = useState(true);
    const [procedures, setProcedures] = useState([]);
    const [sortBy, setSortBy] = useState("omop");
    const [sortOrder, setSortOrder] = useState("desc");

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


    /**
     * Redirects for the ETL procedure page
     *
     * @param id
     */

    const accessETLProcedure = (id) => {
        window.location.href = '/procedure/' + id;
    }


    /**
     * Deletes an ETL procedure, removing it from table
     *
     * @param etl_id ETL procedure's id
     */

    const deleteETLProcedure = (etl_id) => {
        ETLService.deleteETLProcedure(etl_id).then(() => {
            let new_procedures = []
            procedures.forEach(function(p) {
                if (p.id !== etl_id)
                    new_procedures.push(p);
            })
            setProcedures(new_procedures);
            sortData(sortBy, sortOrder);
        });
    }


    /**
     * Verifies if an ETL procedure is marked as deleted
     *
     * @param id
     * @returns {boolean}
     */

    const getDeletionStatus = (id) => {
        let result = false;
        procedures.forEach(item => {
            if (item.id === id) result = item.deleted;
        });
        return result;
    }


    /**
     * Changes the deletion status of a given ETL procedure
     *
     * @param procedure ETL procedure
     */
    // TODO: refactor (fix)
    const handleETLProcedureDeletion = (procedure) => {
        if (procedure.deleted) {
            ETLService.unmarkETLProcedureAsDeleted(procedure.id).then(() => {
                let procedures_new = [];
                procedures.forEach(item => {
                    if (item.id === procedure.id)
                        item.deleted = false;

                    procedures_new.push(item);
                })
                setProcedures(procedures_new);
                sortData(sortBy, sortOrder);
            });
        } else {
            ETLService.markETLProcedureAsDeleted(procedure.id)
                .then(() => {
                    let procedures_new = [];
                    procedures.forEach(item => {
                        if (item.id === procedure.id)
                            item.deleted = true;
                        procedures_new.push(item);
                    })
                    setProcedures(procedures_new);
                    sortData(sortBy, sortOrder);
                });
        }
    }


    /**
     * Sorts the list of ETL procedures according to the parameter and order
     *
     * @param paramSort parameter to sort to (OMOP CDM, creation date, modification date)
     * @param sortOrder sort order (descendent or ascendant)
     * @returns {*|*[]} list of sorted items
     */

    const sortData = (paramSort, sortOrder) => {
        let itemsToSort = JSON.parse(JSON.stringify(procedures));
        let sortedItems = [];
        let compareFn = null;
        
        switch (paramSort) {
            case "omop":
                compareFn = (i, j) => {
                    let cdmIndexI = CDMVersions.findIndex(function(item) { return item.id === i.targetDatabase.databaseName});
                    let cdmIndexJ = CDMVersions.findIndex(function(item) { return item.id === j.targetDatabase.databaseName});
                    if (cdmIndexI < cdmIndexJ)
                        return sortOrder === "desc" ? -1 : 1;
                    else if (cdmIndexI > cdmIndexJ)
                        return sortOrder === "desc" ? 1 : -1;
                    else
                        return 0;
                }
                break;
            case "deleted":
                compareFn = (i, j) => {
                    if (i.deleted === false && j.deleted)
                        return sortOrder === "desc" ? -1 : 1;
                    else if (i.deleted && j.deleted === false)
                        return sortOrder === "desc" ? 1 : -1;
                    else
                        return 0; 
                }
                break;
            case "creationDate":
                compareFn = (i, j) => {
                    let dateI = moment(i.creationDate, "DD-MM-YYYY HH:mm").format('DD-MMM-YYYY HH:mm')
                    let dateJ = moment(j.creationDate, "DD-MM-YYYY HH:mm").format('DD-MMM-YYYY HH:mm')
    
                    if (dateI > dateJ)
                        return sortOrder === "desc" ? 1 : -1;
                    else if (dateI < dateJ)
                        return sortOrder === "desc" ? -1 : 1;
                    else
                        return 0;
                }
                break;
            case "modificationDate":
                compareFn = (i, j) => {
                    let dateI = moment(i.modificationDate, "DD-MM-YYYY HH:mm").format('DD-MMM-YYYY HH:mm')
                    let dateJ = moment(j.modificationDate, "DD-MM-YYYY HH:mm").format('DD-MMM-YYYY HH:mm')
    
                    if (dateI > dateJ)
                        return sortOrder === "desc" ? 1 : -1;
                    else if (dateI < dateJ)
                        return sortOrder === "desc" ? -1 : 1;
                    else
                        return 0;
                }
                break;
            default:
                break;
        }

        sortedItems = itemsToSort.sort(compareFn);
        return sortedItems;
    }


    /**
     * Defines the parameter to sort by and the order and sort list of procedures
     *
     * @param paramToSort Parameter to sort by
     */

    const requestSort = (paramToSort) => {        
        if (paramToSort === sortBy) {
            // change sort order
            setSortOrder(sortOrder === "desc" ? "asc" : "desc");
        } else {
            // change param sorted by
            setSortBy(paramToSort);
            setSortOrder("desc");
        }
        let sortedProcedures = sortData(sortBy, sortOrder);
        setProcedures(sortedProcedures);
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
                                    <StyledTableCell>Name</StyledTableCell>

                                    <StyledTableCell>EHR Database</StyledTableCell>

                                    <StyledTableCell>
                                        <StyledTableSortLabel
                                            active={sortBy === "omop"}
                                            direction={sortOrder}
                                            onClick={() => requestSort("omop")}
                                        />
                                        OMOP CDM
                                    </StyledTableCell>

                                    <StyledTableCell>
                                        <StyledTableSortLabel
                                            active={sortBy === "deleted"}
                                            direction={sortOrder}
                                            onClick={() => requestSort("deleted")}
                                        />
                                        Deleted
                                    </StyledTableCell>

                                    <StyledTableCell>
                                        <StyledTableSortLabel
                                            active={sortBy === "creationDate"}
                                            direction={sortOrder}
                                            onClick={() => requestSort("creationDate")}
                                        />
                                        Creation Date
                                    </StyledTableCell>

                                    <StyledTableCell>
                                        <StyledTableSortLabel
                                            active={sortBy === "modificationDate"}
                                            direction={sortOrder}
                                            onClick={() => requestSort("modificationDate")}
                                        />
                                        Modification Date
                                    </StyledTableCell>

                                    <StyledTableCell>Users</StyledTableCell>

                                    <StyledTableCell />
                                    <StyledTableCell />
                                </TableRow>
                            </TableHead>

                            <TableBody>
                                {procedures.map((procedure, i) => {
                                    return(
                                        <StyledTableRow key={i}>
                                            <StyledTableCell component="th" scope="row" align="left">
                                                {procedure.name}
                                            </StyledTableCell>

                                            <StyledTableCell component="th" scope="row" align="left">
                                                {procedure.sourceDatabase.databaseName}
                                            </StyledTableCell>

                                            <StyledTableCell component="th" scope="row" align="left">
                                                {CDMVersions.filter(function(cdm) { return cdm.id === procedure.targetDatabase.databaseName })[0].name}
                                            </StyledTableCell>

                                            <StyledTableCell component="th" scope="row" align="left">
                                                <Checkbox checked={getDeletionStatus(procedure.id)} onClick={() => handleETLProcedureDeletion(procedure)} />
                                            </StyledTableCell>

                                            <StyledTableCell component="th" scope="row" align="left">
                                                {procedure.creationDate}
                                            </StyledTableCell>

                                            <StyledTableCell component="th" scope="row" align="left">
                                                {procedure.modificationDate}
                                            </StyledTableCell>

                                            <StyledTableCell component="th" scope="row" align="left">
                                                {procedure.users.map((user, i) => { return(<div key={i}>{user.username}</div>)})}
                                            </StyledTableCell>

                                            <StyledTableCell component="th" scope="row" align="left">
                                                <Controls.Button
                                                    text="Access"
                                                    onClick={() => accessETLProcedure(procedure.id)}
                                                />
                                            </StyledTableCell>

                                            <StyledTableCell component="th" scope="row" align="left">
                                                <Controls.Button
                                                    id="del"
                                                    text="Delete"
                                                    color="secondary"
                                                    disabled={false}
                                                    onClick={() => deleteETLProcedure(procedure.id)}
                                                />
                                            </StyledTableCell>
                                        </StyledTableRow>
                                    )
                                })}

                            </TableBody>
                        </Table>
                    </TableContainer>
                </div>
            )}
        </div>
    )
}