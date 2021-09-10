import React, { useEffect, useState } from 'react'
import { Redirect } from 'react-router-dom'
import AuthService from '../../../services/auth-service'
import ETLService from '../../../services/etl-list-service'
import Controls from '../../controls/controls'
import EditIcon from '@material-ui/icons/Edit'
import SaveIcon from '@material-ui/icons/Save'
import {
    Grid,
    makeStyles,
    Paper,
    Table,
    TableBody, TableCell,
    TableContainer,
    TableHead,
    TableRow,
    withStyles
} from '@material-ui/core'
import { CDMVersions } from '../../../services/CDMVersions'

const useStyles = makeStyles((theme) => ({
    container: {
        margin: theme.spacing(2, 3)
    },
    textbox: {
        marginTop: theme.spacing(2),
        marginBottom: theme.spacing(3),
    },
    iconButton: {
        border: "solid 0px #ffffff",
        color: "#000000",
        backgroundColor: "#ffffff",
        '&:hover': {
            color: "#000000",
            backgroundColor: "#ffffff",
        }
    },
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

export default function Profile() {
    const classes = useStyles();
    const initialUserValues = { username: "" }

    const [ready, setReady] = useState(false);
    const [redirect, setRedirect] = useState(null);
    const [currentUser, setCurrentUser] = useState(initialUserValues);

    // when search own profile
    const [ownProfile, setOwnProfile] = useState(false);
    const [procedures, setProcedures] = useState([]);

    // when visiting other user's profile
    const [userProfile, setUserProfile] = useState({});

    const [disableChangeUsername, setDisableChangeUsername] = useState(true);           // flag to disable/enable username change
    const [disableChangeEmail, setDisableChangeEmail] = useState(true);                 // flag to disable/enable email change

    useEffect(() => {
        // get logged user
        const currentUser = AuthService.getCurrentUser();

        if (!currentUser) {
            setRedirect("/home");
        }
        setCurrentUser(currentUser);

        // get other user's profile
        const username = window.location.pathname.toString().replace("/profile/", "");
        if (username === currentUser.username) {
            // logged user is the same as the searched
            setOwnProfile(true);
            // get most recent ETL procedures
            ETLService.getRecentETLs()
                .then(response => setProcedures(response.data))
                .catch(error => console.log(error))
        } else {
            // logged user visits other user's profile
            AuthService
                .getVisitedProfile(username)
                .then(response => setUserProfile(response.data))
                .catch(error => console.log(error));

            // get shared ETL procedures
            ETLService
                .getSharedETLs(username)
                .then(response => setProcedures(response.data))
                .catch(error => console.log(error));
        }

        setReady(true);
    }, [])


    /**
     * Redirects for the ETL procedure page
     *
     * @param id
     */

    const accessETLProcedure = (id) => {
        window.location.href = '/procedure/' + id;
    }

    const changeEmail = () => {
        AuthService
            .changeUserEmail(currentUser.email)
            .then(response => {
                setCurrentUser(response.data);
                setDisableChangeEmail(true);
            })
            .catch(error => console.log(error))
    }


    return(
        <div className={classes.container}>
            {redirect !== null && (
                <Redirect to={redirect} />
            )}

            {ready && (
                <div>
                    {ownProfile ? (
                        <Grid container>
                            <Grid item xs={6} sm={6} md={6} lg={6}>
                                <header>
                                    <h3>
                                        <strong>{currentUser.username}</strong> Profile
                                    </h3>
                                </header>

                                {/* User name area */}
                                <Grid container className={classes.textbox}>
                                    <Grid item xs={2} sm={2} md={2} lg={2}>
                                        <h5>Username:</h5>
                                    </Grid>

                                    <Grid item>
                                        <Controls.Input
                                            label="Username"
                                            placeholder="Username"
                                            value={currentUser.username}
                                            size="small"
                                            disabled={disableChangeUsername}
                                            //onChange={e => setEtl({...etl, name: e.target.value})}
                                        />
                                        {disableChangeUsername ? (
                                            <Controls.Button className={classes.iconButton} variant="outlined" color="inherit">
                                                <EditIcon onClick={() => setDisableChangeUsername(false)} />
                                            </Controls.Button>
                                        ) : (
                                            <Controls.Button className={classes.iconButton} variant="outlined" color="inherit">
                                                <SaveIcon onClick={() => changeEmail()} />
                                            </Controls.Button>
                                        )}
                                    </Grid>
                                </Grid>

                                {/* Email area */}
                                <Grid container className={classes.textbox}>
                                    <Grid item xs={2} sm={2} md={2} lg={2}>
                                        <h5>Email:</h5>
                                    </Grid>

                                    <Grid item>
                                        <Controls.Input
                                            label="Username"
                                            placeholder="Username"
                                            value={currentUser.email}
                                            size="small"
                                            disabled={disableChangeEmail}
                                            // onChange={e => setCurrentUser({...currentUser, email: e.target.value})}
                                        />
                                        {disableChangeEmail ? (
                                            <Controls.Button className={classes.iconButton} variant="outlined" color="inherit">
                                                <EditIcon onClick={() => setDisableChangeEmail(false)} />
                                            </Controls.Button>
                                        ) : (
                                            <Controls.Button className={classes.iconButton} variant="outlined" color="inherit">
                                                <SaveIcon
                                                    //    onClick={() => changeEmail()}
                                                />
                                            </Controls.Button>
                                        )}
                                    </Grid>
                                </Grid>
                            </Grid>

                            <Grid item xs={6} sm={6} md={6} lg={6}>
                                <TableContainer className={classes.table} component={Paper}>
                                    <Table stickyHeader aria-label="customized table">
                                        <colgroup>
                                            <col style={{ width: "20%"}} />{/* ETL procedure name */}
                                            <col style={{ width: "16%"}} />{/* EHR database name */}
                                            <col style={{ width: "16%"}} />{/* OMOP CDM version */}
                                            <col style={{ width: "16%"}} />{/* Access button */}
                                        </colgroup>
                                        <TableHead>
                                            <TableRow>
                                                <StyledTableCell align="left">Name</StyledTableCell>

                                                <StyledTableCell align="left">EHR Database</StyledTableCell>

                                                <StyledTableCell align="left">OMOP CDM</StyledTableCell>

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
                            </Grid>
                        </Grid>
                    ) : (
                        <Grid container>
                            <Grid item xs={6} sm={6} md={6} lg={6}>
                                <header>
                                    <h3>
                                        <strong>{currentUser.username}</strong> Profile
                                    </h3>
                                </header>

                                {/* User name area */}
                                <Grid container className={classes.textbox}>
                                    <Grid item xs={2} sm={2} md={2} lg={2}>
                                        <h5><strong>Username:</strong></h5>
                                    </Grid>
                                    <Grid item>
                                        {userProfile.username}
                                    </Grid>
                                </Grid>

                                {/* Email area */}
                                <Grid container className={classes.textbox}>
                                    <Grid item xs={2} sm={2} md={2} lg={2}>
                                        <h5><strong>Email:</strong></h5>
                                    </Grid>

                                    <Grid item>
                                        {userProfile.email}
                                    </Grid>
                                </Grid>
                            </Grid>

                            <Grid item xs={6} sm={6} md={6} lg={6}>
                                <h3>ETL procedures with {userProfile.username}</h3>
                                <br />
                                <TableContainer className={classes.table} component={Paper}>
                                    <Table stickyHeader aria-label="customized table">
                                        <colgroup>
                                            <col style={{ width: "20%"}} />{/* ETL procedure name */}
                                            <col style={{ width: "16%"}} />{/* EHR database name */}
                                            <col style={{ width: "16%"}} />{/* OMOP CDM version */}
                                            <col style={{ width: "16%"}} />{/* Access button */}
                                        </colgroup>
                                        <TableHead>
                                            <TableRow>
                                                <StyledTableCell align="left">Name</StyledTableCell>

                                                <StyledTableCell align="left">EHR Database</StyledTableCell>

                                                <StyledTableCell align="left">OMOP CDM</StyledTableCell>

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
                            </Grid>
                        </Grid>
                    )}
                </div>
            )}
        </div>
    )
}
