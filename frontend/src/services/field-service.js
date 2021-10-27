import axios from 'axios'
import authHeader from './headers/auth-header'
import { environment } from './environment'

const TARGET_FIELD_URL = environment.TARGET_FIELD_URL;
const SOURCE_FIELD_URL = environment.SOURCE_FIELD_URL;

class FieldService {

    /**
     * Sends PUT request to change comment of a source field
     *
     * @param ehrFieldId EHR field's id
     * @param comment comment to change to
     * @param etl_id ETL procedure's id
     * @returns
     */

    changeEHRFieldComment(ehrFieldId, comment, etl_id) {
        const username = JSON.parse(localStorage.getItem('user')).username;
        return axios.put(
            SOURCE_FIELD_URL + "comment",
            null,
            { headers: authHeader(), params:{ ehrFieldId: ehrFieldId, comment: comment, etl_id: etl_id, username: username }}
        );
    }


    /**
     * Sends PUT request to change comment of a target field
     *
     * @param omopFieldId OMOP CDM field id
     * @param comment comment to change to
     * @param etl_id ETL procedure's id
     * @returns
     */
    
    changeTargetFieldComment(omopFieldId, comment, etl_id) {
        const username = JSON.parse(localStorage.getItem('user')).username;
        return axios.put(
            TARGET_FIELD_URL + "comment",
            null,
            {
                headers: authHeader(),
                params:{
                    omopFieldId: omopFieldId,
                    comment: comment,
                    etl_id: etl_id,
                    username: username
                }
            }
        );
    }
}

export default new FieldService();