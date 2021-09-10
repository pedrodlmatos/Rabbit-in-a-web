import React, { useState } from 'react'
import {
  createStyles,
  makeStyles,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TableSortLabel,
  withStyles
} from '@material-ui/core'

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

const useStyles = makeStyles(theme => ({
    table: {
        maxHeight: 500,
        minWidth: 700,
        marginTop: theme.spacing(3),
        marginBottom: theme.spacing(5)
    }
}))

export default function OMOPFieldTable(props) {

    const classes = useStyles();
    const { data, setData } = props;
    const [sortOrder, setSortOrder] = useState("desc");


    const sortData = (order) => {
        let itemsToSort = JSON.parse(JSON.stringify(data));
        let sortedItems = [];
        let compareFn = (i, j) => {
            let conceptIdI = parseInt(i.conceptId);
            let conceptIdJ = parseInt(j.conceptId);

            if (conceptIdI > conceptIdJ) return order === "desc" ? 1 : -1;
            else if (conceptIdI < conceptIdJ) return order === "desc" ? -1 : 1;
            else return 0;
        }

        sortedItems = itemsToSort.sort(compareFn);
        return sortedItems;
    }


    const requestSort = () => {
        setSortOrder(sortOrder === "desc" ? "asc" : "desc");
        setData(sortData(sortOrder));
    }


    return(
        <TableContainer className={classes.table} component={Paper}>
            <Table stickyHeader aria-label="customized table">
                <TableHead>
                    <TableRow>
                        <StyledTableCell>
                            <StyledTableSortLabel
                                active={true}
                                direction={sortOrder}
                                onClick={() => requestSort()}
                            />

                            Concept ID
                        </StyledTableCell>

                        <StyledTableCell>Concept Name</StyledTableCell>

                        <StyledTableCell>Class</StyledTableCell>

                        <StyledTableCell>Standard ?</StyledTableCell>
                    </TableRow>
                </TableHead>

                <TableBody>
                    {data.map((row, i) => {
                        return(
                            <StyledTableRow key={i}>
                                <StyledTableCell component="th" scope="row" align="left">{row.conceptId}</StyledTableCell>

                                <StyledTableCell component="th" scope="row" align="left">{row.conceptName}</StyledTableCell>

                                <StyledTableCell component="th" scope="row" align="left">{row.conceptClassId}</StyledTableCell>

                                <StyledTableCell component="th" scope="row" align="left">{row.standardConcept}</StyledTableCell>
                            </StyledTableRow>
                        )
                    })}
                </TableBody>
            </Table>
        </TableContainer>
    )
}