import React, {Component} from 'react';
import AuthService from './services/auth-service';
import './App.css';
import { BrowserRouter as Router, Link, Route, Switch } from 'react-router-dom';
import Home from "./components/home/home";
import Sessions from "./components/sessionList/SessionList";
import Session from "./components/session/Session";
import Documentation from "./components/documentation/Documentation";

class App extends Component {
    constructor(props) {
        super(props);
        this.logout = this.logout.bind(this);

        this.state = {
            showModeratorBoard: false,
            showAdminBoard: false,
            currentUser: undefined
        };
    }

    componentDidMount() {
        const user = AuthService.getCurrentUser();

        if (user) {
            this.setState({
                currentUser: user,
                showModeratorBoard: user.roles.includes("ROLE_MODERATOR"),
                showAdminBoard: user.roles.includes("ROLE_ADMIN"),
            });
        }
    }

    logout() {
        AuthService.logout();
    }

    render() {
        return(
            <div>
                <Router>
                    <nav className="navbar navbar-expand navbar-dark bg-dark">
                        <Link to={"/"} className="navbar-brand">Rabbit in a Hat</Link>

                        <div className="navbar-nav mr-auto">
                            <li className="nav-item">
                                <Link to={"/sessions"} className="nav-link">Sessions</Link>
                            </li>
                        </div>
                    </nav>

                    <div>
                        <Switch>
                            <Route exact path="/" component={Home}/>
                            <Route path='/sessions' component={Sessions} />
                            <Route path='/session/:id' component={Session} />
                            <Route exact path='/documentation' component={Documentation} />
                        </Switch>
                    </div>
                </Router>
            </div>
        )

    }
}

export default App;