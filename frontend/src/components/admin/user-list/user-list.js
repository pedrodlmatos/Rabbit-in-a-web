import React, { useEffect, useState } from 'react'
import { StyledTableCell, StyledTableRow, StyledTableSortLabel } from '../../utilities/styled-table-elements'
import {
	makeStyles,
	TableRow,
	TableHead,
	Checkbox,
	CircularProgress,
	Paper,
	Table,
	TableBody,
	TableContainer,
	TableFooter,
	TablePagination } from '@material-ui/core'
import SearchBar from "material-ui-search-bar";
import Controls from '../../controls/controls'
import TableOperations from '../../utilities/table-operations';
import UserService from '../../../services/user-service'

const useStyles = makeStyles((theme) => ({
	pageContainer: {
		margin: theme.spacing(1),
		padding: theme.spacing(1)
	},
	title: {
		marginBottom: theme.spacing(5),
		fontSize: "12"
	},
	table: {
		maxHeight: 600,
		minWidth: 700,
		marginTop: theme.spacing(3),
		marginBottom: theme.spacing(5)
	}
}))

export default function UserList() {

	const classes = useStyles();
	const [loading, setLoading] = useState(true);
	const [users, setUsers] = useState([]);
	const [filteredUsers, setFilteredUsers] = useState([]);
	const [sortBy, setSortBy] = useState("admin");
	const [sortOrder, setSortOrder] = useState("desc");
	const [rowsPerPage, setRowsPerPage] = useState(10);
	const [page, setPage] = useState(0);
	const [searched, setSearched] = useState("");

	/**
	 *
	 */

	useEffect(() => {
		UserService
			.getListOfAllUsers()
			.then(response => {
				setUsers(response.data);
				setFilteredUsers(response.data);
				setLoading(false);
			})
			.catch(error => console.log(error))
	}, [])


	const requestSort = (paramToSort) => {
		if (paramToSort === sortBy) setSortOrder(sortOrder === "desc" ? "asc" : "desc");
		else {
			setSortBy(paramToSort);
			setSortOrder("desc");
		}
		let sortedUsers = TableOperations.sortData(sortBy, sortOrder, users);
		setFilteredUsers(sortedUsers)
	}


	/**
	 * Send request to API to change user's privileges and reloads list of users
	 *
	 * @param user
	 */

	const makeUserAdmin = (user) => {
		UserService
			.addPrivileges(null, user.username)
			.then(response => {
				let newUsers = [];
				users.forEach(function (u) {
					if (u.username !== user.username) newUsers.push(u)
					else newUsers.push(response.data)
				})
				setUsers(newUsers);

				let newFilteredUsers = [];
				filteredUsers.forEach(function (u) {
					if (u.username !== user.username) newFilteredUsers.push(u)
					else newFilteredUsers.push(response.data)
				})
				setFilteredUsers(newFilteredUsers);
			})
			.catch(error => console.log(error))
	}


	/**
	 * Sends request to delete user's account and reloads user list
	 *
	 * @param user user to delete
	 */

	const deleteUser = (user) => {
		UserService
			.deleteAccount(null, user.username)
			.then(() => {
				let newUsers = [];
				users.forEach(function (u) {
					if (u.username !== user.username) newUsers.push(u);
				})
				setUsers(newUsers);

				let newFilteredUsers = [];
				filteredUsers.forEach(function (u) {
					if (u.username !== user.username) newFilteredUsers.push(u);
				})
				if (filteredUsers.length === 0) {
					setSearched("");
					setFilteredUsers(users);
				}
				else setFilteredUsers(newFilteredUsers);
			})
			.catch(error => console.log(error));
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


	const requestSearch = (searchedValue) => {
		const filteredRows = users.filter((row) => {
			return row.username.includes(searchedValue) || row.email.includes(searchedValue);
		});
		setFilteredUsers(filteredRows);
	};

	const cancelSearch = () => {
		setSearched("");
		setFilteredUsers(users);
	};


	return(
		<div className={classes.pageContainer}>
			{loading ? (
				<CircularProgress color="primary" variant="indeterminate" size={40} />
			) : (
				<div>
					<h1 className={classes.title}>All users</h1>

					<SearchBar
						style={{ width: "400px"}}
						value={searched}
						placeholder={"Search by username or e-mail"}
						onChange={(searchedVal) => requestSearch(searchedVal)}
						onCancelSearch={() => cancelSearch()}
					/>
					<TableContainer className={classes.table} component={Paper}>
						<Table stickyHeader>
							<colgroup>
								<col style={{ width: "25%" }} />{/* Username */}
								<col style={{ width: "25%" }} />{/* E-mail */}
								<col style={{ width: "10%" }} />{/* Admin checkbox */}
								<col style={{ width: "20%" }} />{/* Visit profile button */}
								<col style={{ width: "20%" }} />{/* Delete account button */}
							</colgroup>
							<TableHead>
								<TableRow>
									<StyledTableCell align="left">Username</StyledTableCell>
									<StyledTableCell align="left">E-mail</StyledTableCell>
									<StyledTableCell align="left">
										Admin ?
										<StyledTableSortLabel
											active={sortBy === "admin"}
											direction={sortOrder}
											onClick={() => requestSort("admin")}
										/>
									</StyledTableCell>

									<StyledTableCell align="left" />
									<StyledTableCell align="left" />
								</TableRow>
							</TableHead>
							<TableBody>
								{(rowsPerPage > 0 ? filteredUsers.slice(page * rowsPerPage, rowsPerPage * (1 + page)) : users)
									.map((user, i) => {
										return(
											<StyledTableRow key={i}>
												<StyledTableCell component="th" scope="row" align="left">
													{user.username}
												</StyledTableCell>

												<StyledTableCell component="th" scope="row" align="left">
													{user.email}
												</StyledTableCell>

												<StyledTableCell component="th" scope="row" align="left">
													{user.roles.some(role => role['name'] === "ROLE_ADMIN") ? (
														<Checkbox disabled={true} checked={true} />
													) : (
														<Checkbox checked={false} onClick={() => makeUserAdmin(user)}/>
													)}
												</StyledTableCell>

												<StyledTableCell component="th" scope="row" align="left">
													<Controls.Button
														text="Visit profile"
														onClick={() => window.location.href = '/profile/' + user.username}
													/>
												</StyledTableCell>

												<StyledTableCell component="th" scope="row" align="left">
													{user.roles.some(role => role['name'] === "ROLE_ADMIN") === false && (
														<Controls.Button
															id="del"
															text="Delete account"
															color="secondary"
															disabled={false}
															onClick={() => deleteUser(user)}
														/>
													)}
												</StyledTableCell>
											</StyledTableRow>
										)
									})
								}
							</TableBody>

							<TableFooter>
								<StyledTableRow>
									<TablePagination
										rowsPerPageOptions={[5, 10, 25]}
										colSpan={9}
										count={filteredUsers.length}
										rowsPerPage={rowsPerPage}
										page={page}
										onChangePage={handleChangePage}
										onChangeRowsPerPage={handleChangeRowsPerPage}
									/>
								</StyledTableRow>
							</TableFooter>
						</Table>
					</TableContainer>
				</div>
			)}
		</div>
	)

}