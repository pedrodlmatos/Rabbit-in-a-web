import React, { Component } from 'react'
import './home.css'

class Home extends Component {

    render() {
        return (
            <div className="homeContent">
                <img className="logo" src="/rabbitinahatlogo.png" alt="Logo" />

                <p>Rabbit in a Hat is a project developed by Observational Health Data Sciences and Informatics (OHDSI) and allows to:</p>

                <ul className="text">
                    <li>Read a scan created with White Rabbit</li>
                    <li>Read data from OMOP Common Data Models (CDM)</li>
                    <li>Map a source table of EHR database to a target table of CDM</li>
                    <li>Map field between two tables</li>
                </ul>

                <p>To check documentation, click <a href="instructions">here</a></p>
            </div>
        )
    }
}

export default Home