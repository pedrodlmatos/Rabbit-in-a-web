import React, {Component} from 'react';
import './App.css';
import { BrowserRouter as Router, Link, Route, Switch } from 'react-router-dom';
import Home from "./components/home/home";
import SessionList from './components/session-list/session-list';
import Session from "./components/session/session";
import Documentation from "./components/documentation/Documentation";

class App extends Component {

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
                            <Route path='/sessions' component={SessionList} />
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