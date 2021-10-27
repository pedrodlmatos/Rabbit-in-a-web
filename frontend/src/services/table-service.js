import axios from 'axios'
import authHeader from './headers/auth-header'
import { environment } from './environment'

const TARGET_TABLE_URL = environment.TARGET_TABLE_URL;
const SOURCE_TABLE_URL = environment.SOURCE_TABLE_URL;

class TableService {

    /**
     * Sends PUT request to change comment of a source table
     *
     * @param ehrTableId table's id
     * @param comment comment to change to
     * @param etl_id ETL procedure's id
     * @returns
     */

    changeEHRTableComment(ehrTableId, comment, etl_id) {
        const username = JSON.parse(localStorage.getItem('user')).username;

        return axios.put(
            SOURCE_TABLE_URL + "comment",
            null,
            {
                headers: authHeader(),
                params:{
                    ehrTableId: ehrTableId,
                    comment: comment,
                    username: username,
                    etl_id: etl_id
                }
            }
        );
    }


    /**
     * Sends PUT request to change comment of a target table
     *
     * @param omopTableId Target table's id
     * @param comment comment to change to
     * @param etl_id ETL procedure's id
     * @returns
     */
    
    changeOMOPTableComment(omopTableId, comment, etl_id) {
        const username = JSON.parse(localStorage.getItem('user')).username;
        return axios.put(
            TARGET_TABLE_URL + "comment", 
            null, 
            {
                headers: authHeader(),
                params:{
                    omopTableId: omopTableId,
                    comment: comment,
                    username: username,
                    etl_id: etl_id
                }
            }
        );
    }
}

export default new TableService();