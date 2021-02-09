import axios from 'axios';
import authHeader from './auth-header';
import authHeaderMultiPart from "./auth-header-multi-part";
import {environment} from "./environment";

const API_URL = environment.ETL_URL;

class ETLService {

    getAllETL() {
        return axios.get(API_URL + 'all', { headers: authHeader() })
    }

    getETLById(id) {
        return axios.get(API_URL + id, { headers: authHeader() });
    }

    createETL(file, cdm) {
        let formData = new FormData();
        formData.append("file", file)
        return axios.post(API_URL + "add", formData, { headers: authHeaderMultiPart(), params: { cdm: cdm } });
    }

    changeTargetDatabase(etl, cdm) {
        return axios.put(API_URL + "changeTrgDB", etl, { headers: authHeader(), params: {cdm: cdm} });
    }
}

export default new ETLService();