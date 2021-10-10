import React, { useEffect, useState } from 'react'
import { withStyles, makeStyles, createStyles, Divider, CircularProgress, Paper, Grid, IconButton, Table, TableBody,
    TableCell, TableContainer, TableFooter, TableHead, TableRow, TableSortLabel, TablePagination } from '@material-ui/core';
import AddIcon from '@material-ui/icons/Add'
import AttachFileIcon from '@material-ui/icons/AttachFile'
import PropTypes from 'prop-types';
import ETLService from '../../../services/etl-list-service'
import Controls from '../../controls/controls'
import CreateETLForm from '../../forms/create-etl/create-new-etl-form'
import ETLModal from '../../modals/create-etl/etl-modal'
import CreateETLFromFileForm from '../../forms/create-etl/create-from-file-form'
import { CDMVersions } from '../../utilities/CDMVersions'
import TableOperations from '../../utilities/table-operations'
import TablePaginationActions from '@material-ui/core/TablePagination/TablePaginationActions'

const useStyles = makeStyles((theme) => ({
    pageContainer: {
        margin: theme.spacing(1),
        padding: theme.spacing(1)
    },
    title: {
        marginBottom: theme.spacing(5),
        fontSize: "12"
    },
    button: {
        marginLeft: theme.spacing(1)
    },
    divider: {
        marginLeft: "-1px"
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
            //backgroundColor: theme.palette.action.hover
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

TablePaginationActions.propTypes = {
    count: PropTypes.number.isRequired,
    onPageChange: PropTypes.func.isRequired,
    page: PropTypes.number.isRequired,
    rowsPerPage: PropTypes.number.isRequired,
};

export default function UserProcedureList() {

    const classes = useStyles();
    const [loading, setLoading] = useState(true);
    const [disabled, setDisabled] = useState(false);
    const [procedures, setProcedures] = useState({ });
    const [sortBy, setSortBy] = useState("creationDate");
    const [sortOrder, setSortOrder] = useState("desc");
    const [rowsPerPage, setRowsPerPage] = useState(5);
    const [page, setPage] = useState(0);

    const [showETLCreationModal, setShowETLCreationModal] = useState(false);
    const [showCreateNewETLModal, setShowCreateNewETLModal] = useState(false);
    const [showCreateETLFromFileModal, setShowCreateETLFromFileModal] = useState(false);


    /**
     * Sends GET request to API to retrieve all ETL procedures
     */

    useEffect(() => {
        ETLService.getUserETL().then(response => {
            const sortedList = TableOperations.sortData(sortBy, sortOrder, response.data);              /* Sort procedures */
            setProcedures(sortedList);
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
     * Closes the modal to choose type of creation and opens modal to create a new ETL procedure
     */

    const openCreateNewETLModal = () => {
        setShowETLCreationModal(false);
        setShowCreateNewETLModal(true);
    }


    /**
     * Closes the modal to choose type of creation and opens modal to create ETL procedure from JSON file
     */

    const openCreateETLFromFileModal = () => {
        setShowETLCreationModal(false);
        setShowCreateETLFromFileModal(true);
    }


    /**
     * Closes the creation modal and opens the method creation modal
     */

    const backToMethodSelection = () => {
        if (showCreateNewETLModal)
            setShowCreateNewETLModal(false);
        else if (showCreateETLFromFileModal)
            setShowCreateETLFromFileModal(false);

        setShowETLCreationModal(true);
    }


    /**
     * Sends POST request to API to create a new ETL procedure
     * Disables buttons
     *
     * @param {*} values form values (file and OMOP CDM)
     */

    const createNewETLProcedure = (values) => {
        // disables button
        setDisabled(true);
        // sends request to API and then redirects to created procedure
        ETLService.createETL(values.ehrName, values.ehrFile, values.omop).then(res => {
            window.location.href = '/procedure/' + res.data.id;
        }).catch(res => {
            console.log(res);
        })
    }


    /**
     * Creates ETL procedure from JSON file and redirects to page of created procedure
     *
     * @param values form values
     */

    const createETLProcedureFromJSONFile = (values) => {
        // disables button
        setDisabled(true);
        // sends request to API and then redirects to created procedure
        ETLService.createETLFromFile(values.file).then(res => {
            window.location.href = '/procedure/' + res.data.id;
        }).catch(res => {
            console.log(res);
        })
    }


    /**
     * Closes ETL procedure creation modal and reset its form
     *
     * @param {*} resetForm function to reset form
     */

    const closeCreateModal = (resetForm) => {
        resetForm();
        setShowCreateNewETLModal(false);
    }

    /**
     * Closes ETL procedure creation modal (with JSON file) and reset form
     *
     * @param resetForm function to reset form
     */

    const closeCreateFromFileModal = (resetForm) => {
        resetForm();
        setShowCreateETLFromFileModal(false);
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
                    <Grid container>
                        <Grid item xs={12} sm={6} md={3} lg={3}>
                            <h1 className={classes.title}>ETL Procedures</h1>
                        </Grid>

                        <Grid style={{ textAlign: "right"}} item xs={12} sm={6} md={9} lg={9}>
                            <Controls.Button
                                className={classes.button}
                                text="Create Procedure"
                                disabled={disabled}
                                onClick={() => {setShowETLCreationModal(true)}}
                            >
                                <AddIcon fontSize="large" />
                            </Controls.Button>
                        </Grid>
                    </Grid>

                    <Paper sx={{ width: '100%', overflow: 'hidden' }}>
                        <TableContainer sx={{ maxHeight: 600 }}>
                            <Table stickyHeader aria-label="sticky table">
                                <colgroup>
                                    <col style={{ width: "20%"}} />{/* ETL procedure name */}
                                    <col style={{ width: "16%"}} />{/* EHR database name */}
                                    <col style={{ width: "16%"}} />{/* OMOP CDM version */}
                                    <col style={{ width: "16%"}} />{/* Creation date */}
                                    <col style={{ width: "16%"}} />{/* Modification date */}
                                    <col style={{ width: "16%"}} />{/* Access button */}
                                </colgroup>

                                <TableHead>
                                    <StyledTableRow>
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
                                        <StyledTableCell />
                                    </StyledTableRow>
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
                                                        {procedure.creationDate}
                                                    </StyledTableCell>

                                                    <StyledTableCell component="th" scope="row" align="left">
                                                        {procedure.modificationDate}
                                                    </StyledTableCell>

                                                    <StyledTableCell component="th" scope="row" align="center">
                                                        <Controls.Button
                                                            text="Access"
                                                            onClick={() => accessETLProcedure(procedure.id)}
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
                                            colSpan={6}
                                            count={procedures.length}
                                            rowsPerPage={rowsPerPage}
                                            page={page}
                                            onChangePage={(event, page) => handleChangePage(event, page)}
                                            onChangeRowsPerPage={(event) => handleChangeRowsPerPage(event)}
                                        />
                                    </TableRow>
                                </TableFooter>
                            </Table>
                        </TableContainer>
                    </Paper>



                    {/* Modal to choose ETL procedure creation method*/}
                    <ETLModal
                        title="Create ETL procedure"
                        show={showETLCreationModal}
                        setShow={setShowETLCreationModal}
                    >
                        <Grid container>
                            <Grid item xs={6} sm={6} md={6} lg={6} align="center">
                                <IconButton color="inherit" onClick={openCreateNewETLModal}>
                                    <AddIcon className={classes.iconButton} />
                                </IconButton>
                                <p>Create a new ETL procedure</p>
                            </Grid>
                            <Divider orientation="vertical" flexItem className={classes.divider} />
                            <Grid item xs={6} sm={6} md={6} lg={6} align="center">
                                <IconButton color="inherit" onClick={openCreateETLFromFileModal}>
                                    <AttachFileIcon className={classes.iconButton} />
                                </IconButton>
                                <p>Create from file</p>
                            </Grid>
                        </Grid>
                    </ETLModal>

                    {/* Modal to create a new ETL procedure */}
                    <ETLModal
                        title={"Create new ETL procedure"}
                        show={showCreateNewETLModal}
                        setShow={setShowCreateNewETLModal}
                    >
                        <CreateETLForm addSession={createNewETLProcedure} back={backToMethodSelection} close={closeCreateModal} />
                    </ETLModal>

                    {/* Modal to create ETL procedure from file */}
                    <ETLModal
                        title={"Create ETL procedure from summary file"}
                        show={showCreateETLFromFileModal}
                        setShow={setShowCreateETLFromFileModal}
                    >
                        <CreateETLFromFileForm addSession={createETLProcedureFromJSONFile} back={backToMethodSelection} close={closeCreateFromFileModal} />
                    </ETLModal>
                </div>
            )}
        </div>
    )
}