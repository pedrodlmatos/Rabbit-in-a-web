import React from 'react'
import {
	Paper,
	TableCell,
	TableContainer,
	Table as MTable,
	TableRow,
	withStyles,
	TableHead, TableBody, createStyles, TableSortLabel
} from '@material-ui/core'
import { CDMVersions } from '../utilities/CDMVersions'
import Controls from './controls'

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

export default function Table(props) {

	const {columns, data, onAccess} = props;

	return(
		<TableContainer component={Paper}>
			<MTable stickyHeader aria-label="customized table">
				<colgroup>
					{columns.map((col, i) => {
						return(
							<col key={i} style={{ width: col.size }} />
						)
					})}
				</colgroup>
				<TableHead>
					<TableRow>
						{columns.map((column, i) => {
							return(
								<StyledTableCell key={i}>
									{column.Header}
									{column.sorted && (
										<StyledTableSortLabel
											active={false}
											direction={"desc"}
										/>
									)}
								</StyledTableCell>
							)
						})}
					</TableRow>
				</TableHead>

				<TableBody>
					{data.map((procedure, i) => {
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

								<StyledTableCell component="th" scope="row" align="center">
									<Controls.Button
										text="Access"
										onClick={() => onAccess(procedure.id)}
									/>
								</StyledTableCell>
							</StyledTableRow>
						)
					})}
				</TableBody>
			</MTable>
		</TableContainer>
	)

}