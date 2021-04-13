import axios from 'axios';
import authHeader from './auth-header';
import authHeaderMultiPart from "./auth-header-multi-part";
import {environment} from "./environment";

const API_URL = environment.ETL_URL;

class ETLService {

    /**
     * Sends GET request to API to retrieve all ETL sessions
     *  
     * @returns all ETL sessions
     */

    getAllETL = async () => {
        try {
            return await axios.get(API_URL + 'sessions', { headers: authHeader() })
        } catch (e) {
            console.log(e);
        }
    }


    /**
     * Sends GET request to API to retrieve session given its Id
     * 
     * @param {*} id ETL session's id
     * @returns ETL session
     */

    getETLById = async (id) => {
        try {
            return await axios.get(API_URL + "sessions/" + id, { headers: authHeader() });
        } catch (e) {
            console.log(e);
        }

    }


    /**
     * Sends POST request to API to create an ETL session
     * 
     * @param {*} file file containing Scan Report of the EHR database
     * @param {*} cdm OMOP CDM version
     * @returns created ETL session created
     */
    
    createETL = async (name, file, cdm) => {
        let formData = new FormData();
        formData.append("file", file);
        
        try {
            return await axios.post(API_URL + "sessions", formData, { headers: authHeaderMultiPart(), params: { name: name, cdm: cdm } });
        } catch (e) {
            console.log(e);
        }
    }

    
    createETLFromFile = async (file) => {
        let formData = new FormData();
        formData.append("file", file);

        try {
            return await axios.post(API_URL + "sessions/save", formData, { headers: authHeaderMultiPart() });
        } catch(e) {
            console.log(e);
        }
    }


    /**
     * Sends PUT request to change the OMOP CDM version
     *
     * @param etl ETL session
     * @param cdm CDM version to change to
     * @returns {Promise<AxiosResponse<any>>} ETL session with new data
     */

    changeTargetDatabase(etl, cdm) {
        return axios.put(API_URL + "sessions/targetDB", null, { headers: authHeader(), params:{etl: etl, cdm: cdm }});
    }


    /**
     * Sends GET request to retrieve data from source fields summary file
     * 
     * @param {*} etl etl's id
     * @returns 
     */

    downloadSourceFieldsFile = async (etl) => {
        return await axios.get(API_URL + "sessions/sourceCSV", { headers: authHeader(), params: { etl: etl }})
    }


    downloadTargetFieldsFile = async (etl) => {
        return await axios.get(API_URL + "sessions/targetCSV", { headers: authHeader(), params: { etl: etl }})
    }


    downloadSaveFile(etl) {
        return axios.get(API_URL + "sessions/save", { headers: authHeader(), params: {etl: etl }});
    }
}

export default new ETLService();