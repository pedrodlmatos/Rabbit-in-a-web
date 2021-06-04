import axios from 'axios';
import authHeader from './auth-header';
import {environment} from "./environment";
import { unstable_batchedUpdates } from 'react-dom'

const API_URL = environment.TABLE_MAP_URL;

class TableMappingService {

    /**
     * Sends a GET request to API to retrieve data from a table mapping (field of each table and field mappings)
     *
     * @param map_id table mapping id
     * @param etl_id ETL procedure's id
     * @returns mapping table mapping information
     */

    getMapping(map_id, etl_id) {
        const username = JSON.parse(localStorage.getItem('user')).username;
        return axios.get(
            API_URL + "map/" + map_id,
            { headers: authHeader(), params: { etl_id: etl_id, username: username }}
        );
    }


    /**
     * Sends a POST request to API to create a new table mapping
     * 
     * @param {*} etl ETL procedure id
     * @param {*} source source table id
     * @param {*} target target table id
     * @returns table mapping created
     */

    addTableMapping = async (etl, source, target) => {
        const username = JSON.parse(localStorage.getItem('user')).username;
        return await axios.post(
            API_URL + "map", 
            {}, 
            { headers: authHeader(), params: { username: username, etl_id: etl, source_id: source, target_id: target } }
        );
    }


    /**
     * Sends a DELETE request to delete table mapping
     * 
     * @param etl_id ETL procedure id
     * @param map_id table mapping id
     * @returns ETL procedure
     */

    removeTableMapping(etl_id, map_id) {
        const username = JSON.parse(localStorage.getItem('user')).username;
        return axios.delete(
            API_URL + "map",
            { headers: authHeader(), params: { map_id: map_id, etl_id: etl_id, username:username } }
        )
    }


    /**
     * Sends a PUT request to change table mapping completion status
     * 
     * @param map_id table mapping id
     * @param complete completion status
     * @returns table mapping edited
     */

    editCompleteMapping(map_id, complete, etl_id) {
        const username = JSON.parse(localStorage.getItem('user')).username;
        return axios.put(
            API_URL + "map/" + map_id + "/complete",
            null,
            { headers: authHeader(), params: { completion: complete, etl_id: etl_id, username: username }}
        );
    }


    /**
     * Sends a PUT request to change table mapping logic
     * 
     * @param map_id table mapping id
     * @param logic mapping logic
     * @param etl_id ETL procedure's id
     * @returns table mapping edited
     */

    editMappingLogic(map_id, logic, etl_id) {
        const username = JSON.parse(localStorage.getItem('user')).username;
        return axios.put(
            API_URL + "map/" + map_id + "/logic",
            null,
            { headers: authHeader(), params: { logic: logic, etl_id: etl_id, username: username }}
        )
    }

}

export default new TableMappingService();