import React, {Component} from 'react';
import './App.css';
import AuthService from './services/auth-service';
import { BrowserRouter as Router, Link, Route, Switch } from 'react-router-dom';
import Home from "./components/home/home";
import AdminProcedureList from './components/procedure-list/admin-procedure-list';
import Procedure from "./components/procedure/procedure";
import Documentation from "./components/documentation/Documentation";
import Login from './components/users/login/login'
import Register from './components/users/register/Register'
import UserProcedureList from './components/procedure-list/user-procedure-list'

class App extends Component {

    constructor(props) {
        super(props);
        this.logout = this.logout.bind(this);

        this.state = {
            showAdminBoard: false,
            currentUser: undefined
        };
    }


    componentDidMount() {
        const user = AuthService.getCurrentUser();

        if (user) {
            this.setState({
                currentUser: user,
                showAdminBoard: user.roles.includes("ROLE_ADMIN"),
            });
        }
    }

    logout() {
        AuthService.logout();
    }


    render() {
        const { currentUser, showAdminBoard } = this.state;

        return(
            <div>
                <Router>
                    <nav className="navbar navbar-expand navbar-dark bg-dark">
                        <Link to={"/"} className="navbar-brand">Hare in a Hat</Link>

                        <div className="navbar-nav mr-auto">
                            {showAdminBoard && (
                                <li className="nav-item">
                                    <Link to={"/all"} className="nav-link">All ETL procedures</Link>
                                </li>
                            )}

                            {currentUser && (
                                <li className="nav-item">
                                    <Link to={"/procedures"} className="nav-link">ETL Procedures</Link>
                                </li>
                            )}
                        </div>

                        {currentUser ? (
                            <div className="navbar-nav ml-auto">
                                <li className="nav-item">
                                    <Link to={"/profile"} className="nav-link">{currentUser.username}</Link>
                                </li>

                                <li className="nav-item">
                                    <a href="/login" className="nav-link" onClick={this.logout}>Log out</a>
                                </li>
                            </div>
                        ) : (
                            <div className="navbar-nav ml-auto">
                                <li className="nav-item">
                                    <Link to={"/login"} className="nav-link">Login</Link>
                                </li>

                                <li className="nav-item">
                                    <Link to={"/register"} className="nav-link">Sign up</Link>
                                </li>
                            </div>
                        )}
                    </nav>

                    <div>
                        <Switch>
                            <Route exact path="/" component={Home}/>
                            <Route exact path="/login" component={Login}/>
                            <Route exact path="/register" component={Register} />
                            <Route path='/all' component={AdminProcedureList} />
                            <Route path="/procedures" component={UserProcedureList} />
                            <Route path='/procedure/:id' component={Procedure} />
                            <Route exact path='/documentation' component={Documentation} />
                        </Switch>
                    </div>
                </Router>
            </div>
        )

    }
}

export default App;