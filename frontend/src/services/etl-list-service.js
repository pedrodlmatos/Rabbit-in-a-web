import axios from 'axios';
import authHeader from './auth-header';
import authHeaderMultiPart from "./auth-header-multi-part";
import {environment} from "./environment";

const API_URL = environment.ETL_URL;

class ETLService {

    getAllETL = async () => {
        try {
            return await axios.get(API_URL + 'sessions', { headers: authHeader() })
        } catch (e) {
            console.log(e);
        }

        //return axios.get(API_URL + 'sessions', { headers: authHeader() })
    }

    getETLById = async (id) => {
        try {
            return await axios.get(API_URL + "sessions/" + id, { headers: authHeader() });
        } catch (e) {
            console.log(e);
        }

    }

    createETL = async (file, cdm) => {
        let formData = new FormData();
        formData.append("file", file)
        try {
            return await axios.post(API_URL + "sessions", formData, { headers: authHeaderMultiPart(), params: { cdm: cdm } });
        } catch (e) {
            console.log(e);
        }
    }


    /**
     * Sends a request to change the OMOP CDM version
     *
     * @param etl ETL session
     * @param cdm CDM version to change to
     * @returns {Promise<AxiosResponse<any>>} ETL session with new data
     */

    changeTargetDatabase(etl, cdm) {
        return axios.put(API_URL + "sessions/targetDB", null, { headers: authHeader(), params:{etl: etl.id, cdm: cdm }});
    }
}

export default new ETLService();