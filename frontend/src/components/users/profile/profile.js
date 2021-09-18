import React, { useEffect, useState } from 'react'
import { Redirect } from 'react-router-dom'
import AuthService from '../../../services/auth-service'
import ETLService from '../../../services/etl-list-service'
import UserService from '../../../services/user-service'
import Controls from '../../controls/controls'
import EditIcon from '@material-ui/icons/Edit'
import SaveIcon from '@material-ui/icons/Save'
import {
    Grid,
    Checkbox,
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
    const [procedures, setProcedures] = useState([]);                                   // list of procedures
    const [loggedUser, setLoggedUser] = useState(initialUserValues);                              // logged user
    const [loggedAdmin, setLoggedAdmin] = useState(false);                              // if logged user is ADMIN
    const [ownProfile, setOwnProfile] = useState(false);                                  // when search own profile
    const [visitedUser, setVisitedUser] = useState({});                                 // when visiting other user's profile
    const [visitedAdmin, setVisitedAdmin] = useState(false);                            // if visited user is admin
    const [disableChangeUsername, setDisableChangeUsername] = useState(true);           // flag to disable/enable username change
    const [disableChangeEmail, setDisableChangeEmail] = useState(true);                 // flag to disable/enable email change

    const columns = React.useMemo(() => [
        { Header: 'Name', accessor: 'name', sorted: 'false', size: '25%'},
        { Header: 'EHR Database', accessor: 'ehrDatabase.databaseName', sorted: 'false', size: '25%'},
        { Header: 'OMOP CDM', accessor: 'omopDatabase.databaseName', sorted: 'false', size: '25%'},
        { Header: '', accessor: 'access', sorted: 'false', size: '25%'}
    ], [])

    useEffect(() => {
        // get logged user
        const currentUser = AuthService.getCurrentUser();
        if (!currentUser)
            setRedirect("/home");
        setLoggedUser(currentUser);
        setLoggedAdmin(currentUser.roles.includes("ROLE_ADMIN"));

        // get username by url
        const username = window.location.pathname.toString().replace("/profile/", "");
        if (username === currentUser.username) {
            // logged user is the same as the visited
            setOwnProfile(true);
            // get most recent ETL procedures
            ETLService.getRecentETLs()
                .then(response => setProcedures(response.data))
                .catch(error => console.log(error))
        } else {
            // logged user visits other user's profile
            UserService
                .getVisitedProfile(username)
                .then(response => {
                    console.log(response.data);
                    setVisitedUser(response.data);
                    setVisitedAdmin(response.data.roles.some(role => role['name'] === "ROLE_ADMIN"))
                })
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
        UserService
            .changeUserEmail(loggedUser.email)
            .then(response => {
                setLoggedUser(response.data);
                setDisableChangeEmail(true);
            })
            .catch(error => console.log(error))
    }


    /**
     *
     */

    const makeVisitedUserAdmin = () => {
        UserService
            .addPrivileges(loggedUser.username, visitedUser.username)
            .then(response => {
                setVisitedUser(response.data);
                setVisitedAdmin(true)
            })
            .catch(error => console.log(error));
    }


    return(
        <div className={classes.container}>
            {redirect !== null && (
                <Redirect to={redirect} />
            )}

            {ready && (
                <div>
                    {ownProfile ? (
                        /* when visiting own profile */
                        <Grid container>
                            <Grid item xs={6} sm={6} md={6} lg={6}>
                                <header>
                                    <h3>
                                        <strong>{loggedUser.username}</strong> Profile
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
                                            value={loggedUser.username}
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
                                            value={loggedUser.email}
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

                                {loggedAdmin && (
                                    <p>
                                        <strong>Administrator: </strong>
                                        <Checkbox checked={loggedAdmin} disabled={true}/>
                                    </p>
                                )}
                            </Grid>

                            <Grid item xs={6} sm={6} md={6} lg={6}>
                                <h5>Most recent updates</h5>
                                <br />
                                <Controls.Table
                                    columns={columns}
                                    data={procedures}
                                    onAccess={accessETLProcedure}
                                />
                            </Grid>
                        </Grid>
                    ) : (
                        /* When visiting other user's profile */
                        <Grid container>
                            <Grid item xs={6} sm={6} md={6} lg={6}>
                                <header>
                                    <h3>
                                        <strong>{visitedUser.username}</strong> Profile
                                    </h3>
                                </header>

                                {/* User name area */}
                                <Grid container className={classes.textbox}>
                                    <Grid item xs={2} sm={2} md={2} lg={2}>
                                        <h5><strong>Username:</strong></h5>
                                    </Grid>
                                    <Grid item>
                                        {visitedUser.username}
                                    </Grid>
                                </Grid>

                                {/* Email area */}
                                <Grid container className={classes.textbox}>
                                    <Grid item xs={2} sm={2} md={2} lg={2}>
                                        <h5><strong>Email:</strong></h5>
                                    </Grid>

                                    <Grid item>
                                        {visitedUser.email}
                                    </Grid>
                                </Grid>

                                {loggedAdmin && (
                                    <p>
                                        <strong>Administrator: </strong>
                                        <Checkbox
                                            checked={visitedAdmin}
                                            disabled={visitedAdmin}
                                            onClick={() => makeVisitedUserAdmin()}
                                        />
                                    </p>
                                )}
                            </Grid>

                            <Grid item xs={6} sm={6} md={6} lg={6}>
                                <h3>ETL procedures with {visitedUser.username}</h3>
                                <br />
                                <Controls.Table
                                    columns={columns}
                                    data={procedures}
                                    onAccess={accessETLProcedure}
                                />
                            </Grid>
                        </Grid>
                    )}
                </div>
            )}
        </div>
    )
}
