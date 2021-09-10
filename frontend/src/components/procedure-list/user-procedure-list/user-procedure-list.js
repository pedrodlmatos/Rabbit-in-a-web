import React, { useEffect, useState } from 'react'
import {
    CircularProgress, createStyles,
    Divider,
    Grid,
    IconButton,
    makeStyles,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer, TableHead,
    TableRow, TableSortLabel, withStyles
} from '@material-ui/core'
import AddIcon from '@material-ui/icons/Add'
import AttachFileIcon from '@material-ui/icons/AttachFile'
import ETLService from '../../../services/etl-list-service'
import Controls from '../../controls/controls'
import CreateETLForm from '../../forms/create-etl/create-new-etl-form'
import ETLModal from '../../modals/create-etl/etl-modal'
import CreateETLFromFileForm from '../../forms/create-etl/create-from-file-form'
import { CDMVersions } from '../../../services/CDMVersions'
import moment from 'moment'

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

export default function UserProcedureList() {

    const classes = useStyles();
    const [loading, setLoading] = useState(true);
    const [disabled, setDisabled] = useState(false);
    const [procedures, setProcedures] = useState({ });
    const [sortBy, setSortBy] = useState("omop");
    const [sortOrder, setSortOrder] = useState("desc");

    const [showETLCreationModal, setShowETLCreationModal] = useState(false);
    const [showCreateNewETLModal, setShowCreateNewETLModal] = useState(false);
    const [showCreateETLFromFileModal, setShowCreateETLFromFileModal] = useState(false);



    /**
     * Sends GET request to API to retrieve all ETL procedures
     */

    useEffect(() => {
        ETLService.getUserETL().then(response => {
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
                    let cdmIndexI = CDMVersions.findIndex(function(item) { return item.id === i.omopDatabase.databaseName});
                    let cdmIndexJ = CDMVersions.findIndex(function(item) { return item.id === j.omopDatabase.databaseName});
                    if (cdmIndexI < cdmIndexJ)
                        return sortOrder === "desc" ? -1 : 1;
                    else if (cdmIndexI > cdmIndexJ)
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


                    <TableContainer className={classes.table} component={Paper}>
                        <Table stickyHeader aria-label="customized table">
                            <colgroup>
                                <col style={{ width: "20%"}} />{/* ETL procedure name */}
                                <col style={{ width: "16%"}} />{/* EHR database name */}
                                <col style={{ width: "16%"}} />{/* OMOP CDM version */}
                                <col style={{ width: "16%"}} />{/* Creation date */}
                                <col style={{ width: "16%"}} />{/* Modification date */}
                                <col style={{ width: "16%"}} />{/* Access button */}
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
                                </TableRow>
                            </TableHead>

                            <TableBody>
                                {procedures.map((procedure, i) => {
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
                                })}
                            </TableBody>
                        </Table>
                    </TableContainer>

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
                        title={"Create ETL procedure from file"}
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