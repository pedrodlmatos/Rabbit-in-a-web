import axios from 'axios';
import authHeader from './auth-header';
import {environment} from "./environment";

const API_URL = environment.FIELD_MAP_URL;

class FieldMappingService {

    addFieldMapping(tableMap, source, target) {
        return axios.post(API_URL + "create", null, { headers: authHeader(), params: { tableMap: tableMap, source_id: source, target_id: target } });
    }

    removeFieldMapping(tableMapping_id, fieldMapping_id) {
        return axios.delete(API_URL + "remove", { headers: authHeader(), params: { tableMappingId: tableMapping_id, fieldMappingId: fieldMapping_id } })
    }


    /**
     * Sends a PUT request to change table mapping logic
     * 
     * @param {*} map_id table mapping id
     * @param {*} logic mapping logic
     * @returns table mapping edited
     */

    editMappingLogic(map_id, logic) {
        return axios.put(API_URL + "map/" + map_id + "/logic", null, { headers: authHeader(), params: { logic: logic }})
    }

}

export default new FieldMappingService();