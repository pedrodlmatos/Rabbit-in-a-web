import React, { useEffect, useState } from 'react'
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom'
import { CircularProgress, makeStyles } from '@material-ui/core'
import HomeIcon from '@material-ui/icons/Home'
import ExitToAppIcon from '@material-ui/icons/ExitToApp'
import PersonAddIcon from '@material-ui/icons/PersonAdd'
import PersonIcon from '@material-ui/icons/Person'
import './App.css'
import AuthService from './services/auth-service'
import Home from './components/home/home'
import AdminProcedureList from './components/procedure-list/admin-procedure-list/admin-procedure-list'
import Procedure from './components/procedure/procedure'
import Documentation from './components/documentation/Documentation'
import Login from './components/users/login/login'
import Register from './components/users/register/Register'
import UserProcedureList from './components/procedure-list/user-procedure-list/user-procedure-list'
import Instructions from './components/instructions/instructions'
import { Nav, Navbar, NavDropdown, Row } from 'react-bootstrap'
import Profile from './components/users/profile/profile'

const useStyles = makeStyles((theme) => ({
    navbar: {
        zIndex: 1,
        position: 'relative'
    },
    icon: {
        marginRight: theme.spacing(1),
        bottom: '2px',
        position: 'relative'
    },
    link:{
        marginLeft: theme.spacing(1),
        marginRight: theme.spacing(2),
        color: 'white',
        textDecoration: 'none',
        //alignItems: 'center',
        '&:focus, &:hover, &:visited, &:link, &:active': {
            color: 'white',
            textDecoration: 'none'
        }
    }
}))

export default function App () {
    const classes = useStyles();
    const [showAdminBoard, setShowAdminBoard] = useState(false);
    const [currentUser, setCurrentUser] = useState(undefined);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        setLoading(true);
        const user = AuthService.getCurrentUser();
        if (user) {
            setCurrentUser(user);
            setShowAdminBoard(user.roles.includes("ROLE_ADMIN"));
        }
        setLoading(false);
    }, [])


    /**
     * Logs out from application removing information about the logged user.
     * Then, redirects to home page
     */

    const logout = () => {
        setLoading(true);
        AuthService.logout();
        setCurrentUser(undefined);
        setShowAdminBoard(false);
        window.location.href = '/';
        setLoading(false);
    }

    return(
        <div>
            {loading ? (
                <CircularProgress color="primary" variant="indeterminate" size={40} />
            ) : (
                <div>
                    <Navbar bg="dark" variant="dark">
                        <Nav className="container-fluid">
                            <Nav.Item>
                                <Navbar.Brand href="/">
                                    <HomeIcon className={classes.icon} />
                                    Rabbit in a web
                                </Navbar.Brand>
                            </Nav.Item>

                            {currentUser && (
                                <Nav.Item className="mr-auto">
                                    <Nav.Link href="/procedures">ETL Procedures</Nav.Link>
                                </Nav.Item>
                            )}

                            {showAdminBoard && (
                                <Nav.Item className="mr-auto">
                                    <NavDropdown title="Admin" id="basic-nav-dropdown">
                                        <NavDropdown.Item href="/all">Manage ETL Procedures</NavDropdown.Item>
                                        <NavDropdown.Item href="#">Manage users</NavDropdown.Item>
                                    </NavDropdown>
                                </Nav.Item>
                            )}

                            {currentUser ? (
                                <Row>
                                    <Nav.Item className="ml-auto">
                                        <Nav.Link href={"/profile/" + currentUser.username}>
                                            <PersonIcon className={classes.icon}/>
                                            {currentUser.username}
                                        </Nav.Link>
                                    </Nav.Item>

                                    <Nav.Item className="ml-auto">
                                        <Nav.Link onClick={() => logout()}>
                                            Logout
                                            <ExitToAppIcon className={classes.icon} />
                                        </Nav.Link>
                                    </Nav.Item>
                                </Row>
                            ) : (
                                <Row>
                                    <Nav.Item className="ml-auto">
                                        <Nav.Link href="/login">
                                            <ExitToAppIcon className={classes.icon}/>
                                            Login
                                        </Nav.Link>
                                    </Nav.Item>

                                    <Nav.Item className="ml-auto">
                                        <Nav.Link href="/register">
                                            <PersonAddIcon className={classes.icon} />
                                            Register
                                        </Nav.Link>
                                    </Nav.Item>
                                </Row>
                            )}
                        </Nav>
                    </Navbar>
                    <Router>
                        <Switch>
                            <Route exact path="/" component={Home}/>
                            <Route exact path="/login" component={Login}/>
                            <Route exact path="/register" component={Register} />
                            <Route path='/all' component={AdminProcedureList} />
                            <Route path="/procedures" component={UserProcedureList} />
                            <Route path='/procedure/:id' component={Procedure} />
                            <Route path='/profile/:username' component={Profile} />
                            <Route exact path='/documentation' component={Documentation} />
                            <Route exact path='/instructions' component={Instructions} />
                        </Switch>
                    </Router>
                </div>
            )}
        </div>
    )
}
