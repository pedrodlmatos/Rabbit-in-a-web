import React, {Component} from "react";
import axios from "axios";

class Demo extends Component {

    componentDidMount() {
        const url = 'http://localhost:8080/sessions/demo';

        axios.get(url)
            .then(res => {
                this.setState({ sessions: res.data })
            })
    }

    render() {
        return(
            <h1>Demo</h1>
        )
    }

}

export default Demo;