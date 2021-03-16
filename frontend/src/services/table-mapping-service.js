import axios from 'axios';
import authHeader from './auth-header';
import {environment} from "./environment";

const API_URL = environment.TABLE_MAP_URL;

class TableMappingService {

    /**
     * Sends a GET request to API to retrieve data from a table mapping (field of each table and field mappings)
     * 
     * @param {*} map_id table mapping id
     * @returns table mapping
     */

    getMapping(map_id) {
        return axios.get(API_URL + "map/" + map_id, { headers: authHeader()});
    }


    /**
     * Sends a POST request to API to create a new table mapping
     * 
     * @param {*} etl ETL session id
     * @param {*} source source table id
     * @param {*} target target table id
     * @returns table mapping created
     */

    addTableMapping(etl, source, target) {
        return axios.post(API_URL + "map", {}, { headers: authHeader(), params: { etl_id: etl, source_id: source, target_id: target } });
    }


    /**
     * Sends a DELETE request to delete table mapping
     * 
     * @param {*} etl_id ETL session id
     * @param {*} map_id table mapping id
     * @returns ETL session
     */

    removeTableMapping(etl_id, map_id) {
        return axios.delete(API_URL + "map", { headers: authHeader(), params: { etl_id: etl_id, map_id: map_id } })
    }


    /**
     * Sends a PUT request to change table mapping completion status
     * 
     * @param {*} map_id table mapping id
     * @param {*} complete completion status
     * @returns table mapping edited
     */

    editCompleteMapping(map_id, complete) {
        return axios.put(API_URL + "map/" + map_id, null, { headers: authHeader(), params: { completion: complete }})
    }

}

export default new TableMappingService();