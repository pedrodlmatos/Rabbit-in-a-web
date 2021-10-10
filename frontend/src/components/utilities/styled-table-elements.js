import { createStyles, TableRow, TableSortLabel } from '@material-ui/core'

const { withStyles, TableCell } = require('@material-ui/core')

export const StyledTableCell = withStyles((theme) => ({
	head: {
		backgroundColor: theme.palette.common.black,
		color: theme.palette.common.white
	},
	body: {
		fontSize: 14,
	}
}))(TableCell)


export const StyledTableRow = withStyles((theme) => ({
	root: {
		'&:nth-of-type(odd)': {
			backgroundColor: theme.palette.action.hover
		}
	}
}))(TableRow)

export const StyledTableSortLabel = withStyles((theme) =>
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