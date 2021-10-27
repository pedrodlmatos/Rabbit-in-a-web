import React, { useEffect, useState } from 'react'
import {
    Checkbox,
    CircularProgress,
    makeStyles,
    Table,
    TableBody,
    TableContainer,
    TablePagination,
    TableFooter,
    Paper,
    TableHead,
    TableRow,
} from '@material-ui/core'
import ETLService from '../../../services/etl-list-service'
import { CDMVersions } from '../../utilities/CDMVersions'
import Controls from '../../controls/controls'
import TableOperations from '../../utilities/table-operations'
import { StyledTableCell, StyledTableRow, StyledTableSortLabel } from '../../utilities/styled-table-elements'
import TablePaginationActions from '@material-ui/core/TablePagination/TablePaginationActions'
import PropTypes from 'prop-types'

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
        maxHeight: 700,
        minWidth: 700,
        marginTop: theme.spacing(3),
        marginBottom: theme.spacing(5)
    }
}))


TablePaginationActions.propTypes = {
    count: PropTypes.number.isRequired,
    onPageChange: PropTypes.func.isRequired,
    page: PropTypes.number.isRequired,
    rowsPerPage: PropTypes.number.isRequired,
};

export default function AdminProcedureList() {

    const classes = useStyles();
    const [loading, setLoading] = useState(true);
    const [procedures, setProcedures] = useState([]);
    const [sortBy, setSortBy] = useState("creationDate");
    const [sortOrder, setSortOrder] = useState("desc");
    const [rowsPerPage, setRowsPerPage] = useState(5);
    const [page, setPage] = useState(0);


    /**
     * Sends GET request to API to retrieve all ETL procedures
     */
    
    useEffect(() => {
        ETLService.getAllETL().then(response => {
            const sortedProcedures = TableOperations.sortData(sortBy, sortOrder, response.data);
            setProcedures(sortedProcedures);
            setLoading(false);            
        }).catch(response => {
            console.log(response);
        })
    }, [sortBy, sortOrder]);


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
            const sortedProcedures = TableOperations.sortData(sortBy, sortOrder, new_procedures);
            setProcedures(sortedProcedures);
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
                const sortedProcedures = TableOperations.sortData(sortBy, sortOrder, procedures_new);
                setProcedures(sortedProcedures);
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
                    const sortedProcedures = TableOperations.sortData(sortBy, sortOrder, procedures_new);
                    setProcedures(sortedProcedures);
                });
        }
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
        let sortedProcedures = TableOperations.sortData(sortBy, sortOrder, procedures);
        setProcedures(sortedProcedures);
    }

    /**
     * Changes the number of the page in the table (providing new ETL procedures)
     *
     * @param event change event
     * @param newPage number of the new page
     */

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };


    /**
     * Changes the number of rows per page in the table and bring back to the first page
     *
     * @param event chang event
     */

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };


    return(
        <div className={classes.pageContainer}>
            { loading ? (
                <CircularProgress color="primary" variant="indeterminate" size={40} />
            ) : (
                <div>
                    <h1 className={classes.title}>All ETL Procedures</h1>

                    {procedures.length === 0 ? (
                        <h5>No ETL procedures created</h5>
                    ) : (
                        <TableContainer className={classes.table} component={Paper}>
                            <Table stickyHeader aria-label="customized table">
                                <colgroup>
                                    <col style={{ width: "15%"}} />{/* ETL procedure name */}
                                    <col style={{ width: "15%"}} />{/* EHR database name */}
                                    <col style={{ width: "15%"}} />{/* OMOP CDM version */}
                                    <col style={{ width: "8%"}} />{/* Deleted */}
                                    <col style={{ width: "13%"}} />{/* Creation date */}
                                    <col style={{ width: "13%"}} />{/* Modification date */}
                                    <col style={{ width: "7%"}} />{/* Users */}
                                    <col style={{ width: "7%"}} />{/* Access button */}
                                    <col style={{ width: "7%"}} />{/* Delete button */}
                                </colgroup>
                                <TableHead>
                                    <TableRow>
                                        <StyledTableCell align="left">Name</StyledTableCell>

                                        <StyledTableCell align="left">EHR Database</StyledTableCell>

                                        <StyledTableCell align="left">
                                            OMOP CDM
                                            <StyledTableSortLabel
                                                active={sortBy === "omop"}
                                                direction={sortOrder}
                                                onClick={() => requestSort("omop")}
                                            />
                                        </StyledTableCell>

                                        <StyledTableCell align="left">
                                            Deleted
                                            <StyledTableSortLabel
                                                active={sortBy === "deleted"}
                                                direction={sortOrder}
                                                onClick={() => requestSort("deleted")}
                                            />
                                        </StyledTableCell>

                                        <StyledTableCell align="left">
                                            Creation Date
                                            <StyledTableSortLabel
                                                active={sortBy === "creationDate"}
                                                direction={sortOrder}
                                                onClick={() => requestSort("creationDate")}
                                            />
                                        </StyledTableCell>

                                        <StyledTableCell align="left">
                                            Modification Date
                                            <StyledTableSortLabel
                                                active={sortBy === "modificationDate"}
                                                direction={sortOrder}
                                                onClick={() => requestSort("modificationDate")}
                                            />
                                        </StyledTableCell>

                                        <StyledTableCell align="left">Users</StyledTableCell>

                                        <StyledTableCell />
                                        <StyledTableCell />
                                    </TableRow>
                                </TableHead>

                                <TableBody>
                                    {(rowsPerPage > 0 ?
                                        procedures.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage) : procedures)
                                        .map((procedure, i) => {
                                            return(
                                                <StyledTableRow key={i}>
                                                    <StyledTableCell component="th" scope="row">
                                                        {procedure.name}
                                                    </StyledTableCell>

                                                    <StyledTableCell component="th" scope="row" align="left">
                                                        {procedure.ehrDatabase.databaseName}
                                                    </StyledTableCell>

                                                    <StyledTableCell component="th" scope="row" align="left">
                                                        {CDMVersions.filter(function(cdm) { return cdm.id === procedure.omopDatabase.databaseName })[0].name}
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
                                                        {procedure.users.map((user, i) => {
                                                            return(
                                                                <div key={i}>
                                                                    <a href={"/profile/" + user.username}>{user.username}</a>
                                                                </div>
                                                            )
                                                        })}
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
                                        })
                                    }
                                </TableBody>

                                <TableFooter>
                                    <TableRow>
                                        <TablePagination
                                            rowsPerPageOptions={[5, 10, 25]}
                                            colSpan={9}
                                            count={procedures.length}
                                            rowsPerPage={rowsPerPage}
                                            page={page}
                                            onChangePage={handleChangePage}
                                            onChangeRowsPerPage={handleChangeRowsPerPage}
                                        />
                                    </TableRow>
                                </TableFooter>
                            </Table>
                        </TableContainer>
                    )}
                </div>
            )}
        </div>
    )
}