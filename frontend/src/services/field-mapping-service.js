import axios from 'axios';
import authHeader from './auth-header';
import {environment} from "./environment";

const API_URL = environment.FIELD_MAP_URL;

class FieldMappingService {

    /**
     *
     * @param tableMappingId
     * @param sourceFieldId
     * @param targetFieldId
     * @param etl_id
     * @returns {Promise<AxiosResponse<any>>}
     */

    addFieldMapping(tableMappingId, ehrFieldId, omopFieldId, etl_id) {
        const username = JSON.parse(localStorage.getItem('user')).username;
        return axios.post(
            API_URL + "create",
            null,
            {
                headers: authHeader(),
                params: {
                    tableMappingId: tableMappingId,
                    ehrFieldId: ehrFieldId,
                    omopFieldId: omopFieldId,
                    etl_id: etl_id,
                    username: username
                }
            }
        );
    }


    /**
     *
     *
     * @param fieldMappingId
     * @param etl_id
     * @returns {Promise<AxiosResponse<any>>}
     */

    removeFieldMapping(fieldMappingId, etl_id) {
        const username = JSON.parse(localStorage.getItem('user')).username;
        return axios.delete(
            API_URL + "remove",
            { headers: authHeader(), params: { fieldMappingId: fieldMappingId, etl_id: etl_id, username: username } }
        );
    }


    /**
     * Sends a PUT request to change table mapping logic
     *
     * @param fieldMappingId table mapping id
     * @param logic mapping logic
     * @param etl_id
     * @returns
     */

    editMappingLogic(fieldMappingId, logic, etl_id) {
        const username = JSON.parse(localStorage.getItem('user')).username;
        return axios.put(
            API_URL + "map/" + fieldMappingId + "/logic",
            null,
            { headers: authHeader(), params: { logic: logic, etl_id: etl_id, username: username }}
        );
    }

}

export default new FieldMappingService();