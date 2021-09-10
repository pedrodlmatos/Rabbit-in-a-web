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

export default function EHRFieldTable(props) {

    const { data, setData } = props;
    const classes = useStyles();
    const [sortBy, setSortBy] = useState("");
    const [sortOrder, setSortOrder] = useState("desc");


    const sortData = (paramSort, order) => {
        let itemsToSort = JSON.parse(JSON.stringify(data));
        let sortedItems = [];
        let compareFn = null;

        switch (paramSort) {
            case "frequency":
                compareFn = (i, j) => {
                    if (i.frequency > j.frequency) return order === "desc" ? 1 : -1
                    else if (i.frequency < j.frequency) return  order === "desc" ? -1 : 1
                    else return 0
                }
                break;
            case "percentage":
                compareFn = (i, j) => {
                    if (i.percentage > j.percentage) return order === "desc" ? 1 : -1
                    else if (i.percentage < j.percentage) return  order === "desc" ? -1 : 1
                    else return 0
                }
                break;
            default:
                break;
        }
        sortedItems = itemsToSort.sort(compareFn);
        return sortedItems;
    }


    const requestSort = (paramToSort) => {
        if (paramToSort === sortBy) {
            // change order
            setSortOrder(sortOrder === "desc" ? "asc" : "desc");
        } else {
            // change param to sort by
            setSortBy(paramToSort);
            setSortOrder("desc");
        }
        setData(sortData(paramToSort, sortOrder));
    }


    return(
        <TableContainer className={classes.table} component={Paper}>
            <Table stickyHeader aria-label="customized table">
                <TableHead>
                    <TableRow>
                        <StyledTableCell>Value</StyledTableCell>

                        <StyledTableCell>
                            <StyledTableSortLabel
                                active={sortBy === "frequency"}
                                direction={sortOrder}
                                onClick={() => requestSort("frequency")}
                            />
                            Frequency
                        </StyledTableCell>

                        <StyledTableCell>
                            <StyledTableSortLabel
                                active={sortBy === "percentage"}
                                direction={sortOrder}
                                onClick={() => requestSort("percentage")}
                            />
                            Percentage (%)
                        </StyledTableCell>
                    </TableRow>
                </TableHead>

                <TableBody>
                    {data.map((row, i) => {
                        return(
                            <StyledTableRow key={i}>
                                <StyledTableCell component="th" scope="row" align="left">{row.value}</StyledTableCell>

                                <StyledTableCell component="th" scope="row" align="left">{row.frequency}</StyledTableCell>

                                <StyledTableCell component="th" scope="row" align="left">{row.percentage}</StyledTableCell>
                            </StyledTableRow>
                        )
                    })}
                </TableBody>
            </Table>
        </TableContainer>
    )
}