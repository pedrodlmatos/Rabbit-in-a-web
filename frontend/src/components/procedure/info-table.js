import React from 'react'
import {
  makeStyles,
  Paper,
  Table,
  TableBody,
  TableContainer,
  TableHead
} from '@material-ui/core'
import { useTable } from 'react-table'
import {StyledTableRow, StyledTableCell} from '../utilities/styled-table-elements'

const useStyles = makeStyles({
    table: {
        //maxWidth: 700,
    }
})


export default function InfoTable({ columns, data }) {

    const classes = useStyles();
    const { getTableProps, headerGroups, rows, prepareRow } = useTable({ columns, data })

    return(
        <TableContainer component={Paper}>
            <Table width={1} stickyHeader className={classes.table} aria-label="customized table" {...getTableProps()}>
                <TableHead>
                    {headerGroups.map(headerGroup => (
                        <StyledTableRow {...headerGroup.getHeaderGroupProps()}>
                            {headerGroup.headers.map(column => (
                                <StyledTableCell {...column.getHeaderProps()}>
                                    {column.render('Header')}
                                </StyledTableCell>
                            ))}
                        </StyledTableRow>
                    ))}
                </TableHead>
                <TableBody>
                    {rows.map(row => {
                        prepareRow(row)
                        return (
                            <StyledTableRow {...row.getRowProps()}>
                                {row.cells.map(cell => {
                                    return (
                                        <StyledTableCell {...cell.getCellProps()}>
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

/*
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
    tableWrap: {
        //display: 'block',
        //minWidth: '100%',
        overflowX: 'scroll',
        overflowY: 'hidden'
    },
    table: {
        borderSpacing: 0,
        width: '100%',
        marginTop: theme.spacing(3),
        marginBottom: theme.spacing(5)
    }
}))

export default function InfoTable({ columns, data }) {
    const classes = useStyles();
    const { getTableProps, headers, rows, prepareRow } = useTable({ columns, data });

    return(
        <TableContainer className={classes.tableWrap} component={Paper}>
            <Table className={classes.table} aria-label="customized table" {...getTableProps()}>
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
}*/