import axios from "axios";
import { environment } from './environment'
import authHeader from './auth-header'

const API_URL = environment.AUTH_URL;

class AuthService {

    login(username, password) {
        return axios.post(API_URL + "signin", { username, password })
            .then(response => {
                if (response.data.accessToken) {
                    localStorage.setItem("user", JSON.stringify(response.data))
                }
                return response.data;
            });
    }

    logout() {
        localStorage.removeItem("user");
    }

    register(username, email, password) {
        return axios.post(API_URL + "signup", { username, email, password });
    }

    getCurrentUser() {
        return JSON.parse(localStorage.getItem("user"));
    }


    getListOfUsers() {
        const username = JSON.parse(localStorage.getItem('user')).username;
        return axios.get(
            API_URL + "all",
            { headers: authHeader(), params: { username: username } }
        );
    }
}

export default new AuthService();