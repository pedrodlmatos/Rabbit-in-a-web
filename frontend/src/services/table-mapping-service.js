import axios from 'axios';
import authHeader from './auth-header';

const API_URL = 'http://localhost:8081/api/tableMap/'

class TableMappingService {

    getMapping(map_id) {
        return axios.get(API_URL + "map/" + map_id, { headers: authHeader()});
    }

    addTableMapping(etl, source, target) {
        return axios.post(API_URL + "create", {}, { headers: authHeader(), params: { etl_id: etl, source_id: source, target_id: target } });
    }

    removeTableMapping(etl_id, map_id) {
        return axios.delete(API_URL + "remove", { headers: authHeader(), params: { etl_id: etl_id, map_id: map_id } })
    }

}

export default new TableMappingService();