import axios from 'axios'
import authHeader from './auth-header'
import { environment } from './environment'

const API_URL = environment.USER_URL;

class UserService {

	getListOfUsers() {
		const username = JSON.parse(localStorage.getItem('user')).username;
		return axios.get(
			API_URL + "other_users",
			{ headers: authHeader(), params: { username: username } }
		);
	}

	changeUserEmail (newEmail) {
		const username = JSON.parse(localStorage.getItem('user')).username;
		return axios.put(
			API_URL + "changeEmail",
			null,
			{ headers: authHeader(), params: { username: username, newEmail: newEmail}}
		)
	}

	getVisitedProfile(username) {
		return axios.get(
			API_URL + "user",
			{ headers: authHeader(), params: { username: username } }
		)
	}

	
	/**
	 *
	 * @param loggedUsername
	 * @param visitedUsername
	 * @returns {Promise<AxiosResponse<any>>}
	 */

	addPrivileges (loggedUsername, visitedUsername) {
		return axios.put(
			API_URL + "add_privilege",
			null,
			{ headers: authHeader(), params: { loggedUsername: loggedUsername, visitedUsername: visitedUsername}}
		)
	}
}

export default new UserService();