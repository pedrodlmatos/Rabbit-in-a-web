import axios from 'axios'
import authHeader from './auth-header'
import { environment } from './environment'

const API_URL = environment.USER_URL;

class UserService {

	/**
	 *
	 * @returns {Promise<AxiosResponse<any>>}
	 */

	getListOfAllUsers() {
		const username = JSON.parse(localStorage.getItem('user')).username;
		return axios.get(
			API_URL + "all_users",
			{ headers: authHeader(), params: { username: username } }
		);
	}


	/**
	 *
	 * @returns {Promise<AxiosResponse<any>>}
	 */

	getListOfUsers() {
		const username = JSON.parse(localStorage.getItem('user')).username;
		return axios.get(
			API_URL + "other_users",
			{ headers: authHeader(), params: { username: username } }
		);
	}


	/**
	 *
	 * @param newEmail
	 * @returns {Promise<AxiosResponse<any>>}
	 */

	changeUserEmail (newEmail) {
		const username = JSON.parse(localStorage.getItem('user')).username;
		return axios.put(
			API_URL + "changeEmail",
			null,
			{ headers: authHeader(), params: { username: username, newEmail: newEmail}}
		)
	}


	/**
	 *
	 * @param newUsername
	 * @returns {Promise<AxiosResponse<any>>}
	 */
	changeUsername (newUsername) {
		const username = JSON.parse(localStorage.getItem('user')).username;
		return axios.put(
			API_URL + "changeUsername",
			null,
			{ headers: authHeader(), params: { username: username, newUsername: newUsername}}
		)
	}

	/**
	 *
	 * @param username
	 * @returns {Promise<AxiosResponse<any>>}
	 */

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
		if (loggedUsername === null) {
			loggedUsername = JSON.parse(localStorage.getItem('user')).username;
		}
		return axios.put(
			API_URL + "add_privilege",
			null,
			{ headers: authHeader(), params: { loggedUsername: loggedUsername, visitedUsername: visitedUsername}}
		)
	}


	/**
	 * Sends request to API to remove an user account
	 *
	 * @param loggedUsername user who is logged
	 * @param visitedUsername user to remove
	 * @returns {Promise<AxiosResponse<any>>}
	 */

	deleteAccount(loggedUsername, visitedUsername) {
		if (loggedUsername === null) loggedUsername = JSON.parse(localStorage.getItem('user')).username;
		return axios.delete(
			API_URL + "delete",
			{ headers: authHeader(), params: { loggedUsername: loggedUsername, visitedUsername: visitedUsername}}
		)
	}


}

export default new UserService();