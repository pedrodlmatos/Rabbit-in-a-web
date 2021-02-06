import axios from 'axios';
import authHeader from './auth-header';

const API_URL = 'http://localhost:8081/api/fieldMap/'

class FieldMappingService {

    addFieldMapping(tableMap, source, target) {
        return axios.post(API_URL + "create", {}, { headers: authHeader(), params: { tableMap: tableMap, source_id: source, target_id: target } });
    }

    removeFieldMapping(tableMapping_id, fieldMapping_id) {
        return axios.delete(API_URL + "remove", { headers: authHeader(), params: { tableMappingId: tableMapping_id, fieldMappingId: fieldMapping_id } })
    }

}

export default new FieldMappingService();