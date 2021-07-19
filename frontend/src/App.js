import React, { useEffect, useState } from 'react'
import { BrowserRouter as Router, Link, Route, Switch } from 'react-router-dom';
import { AppBar, CssBaseline, makeStyles, Toolbar, Typography } from '@material-ui/core'
import './App.css';
import AuthService from './services/auth-service';
import Home from "./components/home/home";
import AdminProcedureList from './components/procedure-list/admin-procedure-list/admin-procedure-list';
import Procedure from "./components/procedure/procedure";
import Documentation from "./components/documentation/Documentation";
import Login from './components/users/login/login'
import Register from './components/users/register/Register'
import UserProcedureList from './components/procedure-list/user-procedure-list/user-procedure-list'
import Instructions from './components/instructions/instructions'

const drawerWidth = 240;

const useStyles = makeStyles((theme) => ({
    link:{
        marginLeft: theme.spacing(1),
        marginRight: theme.spacing(2),
        color: "white",
        textDecoration: 'none',
        '&:focus, &:hover, &:visited, &:link, &:active': {
            color: "white",
            textDecoration: 'none'
        }
    },
    title: {
        flexGrow: 1
    },
    appBar: {
        position: 'relative',
        zIndex: theme.zIndex.drawer + 1,
        width: `calc(100%-${drawerWidth}px)`,
        backgroundColor: "black"
    },
    entry: {
        marginLeft: theme.spacing(1),
        marginRight: theme.spacing(1)
    }
}))

export default function App() {
    const classes = useStyles();
    const [showAdminBoard, setShowAdminBoard] = useState(false);
    const [currentUser, setCurrentUser] = useState(undefined);

    useEffect(() => {
        const user = AuthService.getCurrentUser();
        if (user) {
            setCurrentUser(user);
            setShowAdminBoard(user.roles.includes("ROLE_ADMIN"));
        }
    }, [])

    const logout = () => {
        AuthService.logout();
        setCurrentUser(undefined);
        setShowAdminBoard(false);
    }

    return(
        <div>
            <Router>
                <CssBaseline />
                <AppBar position="fixed" className={classes.appBar}>
                    <Toolbar>
                        <Typography
                            style={ currentUser ? { flexGrow: 0 } : { flexGrow: 1}}
                            className={classes.link}
                            variant="h4"
                            component={Link}
                            to={'/'}>
                            Rabbit in a web
                        </Typography>

                        {showAdminBoard && (
                            <Typography className={classes.link} variant="subtitle1" component={Link} to={'/all'}>
                                All ETL procedures
                            </Typography>
                        )}

                        {currentUser && (
                            <Typography style={{ flexGrow: 1 }} className={classes.link} variant="subtitle1" component={Link} to={'/procedures'}>
                                ETL procedures
                            </Typography>
                        )}

                        {currentUser ? (
                            <div>
                                <Typography className={classes.link} variant="subtitle1" component={Link} to={'/profile'}>
                                    {currentUser.username}
                                </Typography>
                                <Typography
                                    className={classes.link}
                                    variant="subtitle1"
                                    onClick={() => logout()}
                                    component={Link}
                                    to={'/'}
                                >
                                    Log out
                                </Typography>
                            </div>
                        ) : (
                            <div>
                                <Typography className={classes.link} variant="subtitle1" component={Link} to={"/login"}>
                                    Login
                                </Typography>
                                <Typography className={classes.link} variant="subtitle1" component={Link} to={"/register"}>
                                    Register
                                </Typography>
                            </div>
                        )}
                    </Toolbar>
                </AppBar>

                <Switch>
                    <Route exact path="/" component={Home}/>
                    <Route exact path="/login" component={Login}/>
                    <Route exact path="/register" component={Register} />
                    <Route path='/all' component={AdminProcedureList} />
                    <Route path="/procedures" component={UserProcedureList} />
                    <Route path='/procedure/:id' component={Procedure} />
                    <Route exact path='/documentation' component={Documentation} />
                    <Route exact path='/instructions' component={Instructions} />
                </Switch>
            </Router>

        </div>
    )
}
