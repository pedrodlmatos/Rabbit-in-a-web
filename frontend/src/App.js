import React, {Component} from 'react';
import './App.css';
import { BrowserRouter as Router, Link, Route, Switch } from 'react-router-dom';
import Home from "./components/home/home";
import ProcedureList from './components/procedure-list/procedure-list';
import Procedure from "./components/session/procedure";
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
                                <Link to={"/procedures"} className="nav-link">ETL Procedures</Link>
                            </li>
                        </div>
                    </nav>

                    <div>
                        <Switch>
                            <Route exact path="/" component={Home}/>
                            <Route path='/procedures' component={ProcedureList} />
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