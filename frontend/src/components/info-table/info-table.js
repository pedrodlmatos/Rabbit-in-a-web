import React from 'react';
import { makeStyles, Paper, TableBody, TableCell, TableContainer, Table, TableHead, TableRow, withStyles } from '@material-ui/core';
import { useTable } from 'react-table';

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
    table: {
        maxHeight: 500,
        minWidth: 700,
        marginTop: theme.spacing(3),
        marginBottom: theme.spacing(5)
    }
}))

export default function InfoTable({ columns, data }) {
    const classes = useStyles();
    const { getTableProps, headers, rows, prepareRow } = useTable({ columns, data });

    return(
        <TableContainer className={classes.table} component={Paper}>
            <Table stickyHeader aria-label="customized table" {...getTableProps()}>
                <TableHead>
                    <TableRow>
                    {headers.map(column => (
                        <StyledTableCell align="left" {...column.getHeaderProps()}>
                            {column.render('Header')}
                        </StyledTableCell>
                    ))}
                    </TableRow> 
                </TableHead>

                <TableBody>
                    {rows.map((row, i) => {
                        prepareRow(row);
                        return(
                            <StyledTableRow key={i} {...row.getRowProps()}>
                                {row.cells.map(cell => {
                                    return(
                                        <StyledTableCell component="th" scope="row" align="left" {...cell.getCellProps()}>
                                            {cell.render('Cell')}
                                        </StyledTableCell>
                                    )
                                })}
                            </StyledTableRow>
                        )
                    })}
                </TableBody>
            </Table>
        </TableContainer>
        
    )
}