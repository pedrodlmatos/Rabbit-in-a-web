import React, { Component } from 'react';
import { BrowserRouter as Router, Link, Route, Switch } from 'react-router-dom';
import Documentation from "../documentation/Documentation";
import { MenuItems } from "./MenuItems";
import './Navbar.css'
import Home from "../home/home";
import Sessions from "../sessionList/ProcedureList";
import Session from "../procedure/Procedure";


export default class Navbar extends Component {

    constructor() {
        super();
        this.state = { clicked: false }
    }
    

    /**
     * 
     */

    handleClick = () => {
        this.setState({ clicked: !this.state.clicked })
    }

    
    render() {
        return(
            <Router>
                <nav className="NavbarItems">
                    <h1 className="navbar-logo">Rabbit in a Hat<i className="fab fa-react"></i></h1>
                    <div className="menu-icon" onClick={this.handleClick}>
                        <i className={this.state.clicked ? 'fas fa-times' : 'fas fa-bars'}></i>
                    </div>
                    <ul className={this.state.clicked ? 'nav-menu active' : 'nav-menu'}>
                        { MenuItems.map((item, index) => {
                            return (
                                <li key={index}>
                                    <Link className={item.cName} to={item.url}>{item.title}</Link>
                                </li>
                            )
                        })}
                    </ul>
                </nav>

                <Switch>
                    <Route exact path='/home' component={Home} />
                    <Route path='/sessions' component={Sessions} />
                    <Route path='/session/:id' component={Session} />
                    <Route exact path='/documentation' component={Documentation} />
                </Switch>
            </Router>
        )
    }
}