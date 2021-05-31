import axios from 'axios';
import authHeader from './auth-header';
import {environment} from "./environment"

const TARGET_TABLE_URL = environment.TARGET_TABLE_URL;
const SOURCE_TABLE_URL = environment.SOURCE_TABLE_URL;

class TableService {

    /**
     * Sends PUT request to change comment of a source table
     *
     * @param table table's id
     * @param comment comment to change to
     * @param etl_id
     * @returns
     */

    changeSourceTableComment(table_id, comment, etl_id) {
        const username = JSON.parse(localStorage.getItem('user')).username;

        return axios.put(
            SOURCE_TABLE_URL + "comment",
            null,
            { headers: authHeader(), params:{ table_id: table_id, comment: comment, username: username, etl_id: etl_id }});
    }


    /**
     * Sends PUT request to change comment of a target table
     *
     * @param table_id Target table's id
     * @param comment comment to change to
     * @param etl_id ETL procedure's id
     * @returns
     */
    
    changeTargetTableComment(table_id, comment, etl_id) {
        const username = JSON.parse(localStorage.getItem('user')).username;
        return axios.put(
            TARGET_TABLE_URL + "comment", 
            null, 
            { headers: authHeader(), params:{ table_id: table_id, comment: comment, username: username, etl_id: etl_id }});
    }
}

export default new TableService();