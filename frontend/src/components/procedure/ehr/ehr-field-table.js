import React, { useState } from 'react'
import {
  makeStyles,
  Paper,
  Table,
  TableBody,
  TableContainer,
  TableHead,
  TableRow
} from '@material-ui/core'
import { StyledTableCell, StyledTableRow, StyledTableSortLabel } from '../../utilities/styled-table-elements'

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
                            Frequency
                            <StyledTableSortLabel
                                active={sortBy === "frequency"}
                                direction={sortOrder}
                                onClick={() => requestSort("frequency")}
                            />
                        </StyledTableCell>

                        <StyledTableCell>
                            Percentage (%)
                            <StyledTableSortLabel
                                active={sortBy === "percentage"}
                                direction={sortOrder}
                                onClick={() => requestSort("percentage")}
                            />
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