import axios from 'axios';
import authHeader from './auth-header';
import {environment} from "./environment"

const TARGET_TABLE_URL = environment.TARGET_TABLE_URL;
const SOURCE_TABLE_URL = environment.SOURCE_TABLE_URL;

class TableService {

    /**
     * Sends PUT request to change comment of a target table
     * 
     * @param {*} table table's id
     * @param {*} comment comment to change to
     * @returns 
     */
    
    changeTargetTableComment(table, comment) {
        return axios.put(TARGET_TABLE_URL + "comment", null, { headers: authHeader(), params:{ table: table, comment: comment }});
    }


    /**
     * Sends PUT request to change comment of a source table
     * 
     * @param {*} table table's id
     * @param {*} comment comment to change to
     * @returns 
     */
    
    changeSourceTableComment(table, comment) {
        return axios.put(SOURCE_TABLE_URL + "comment", null, { headers: authHeader(), params:{ table: table, comment: comment }});
    }

}

export default new TableService();