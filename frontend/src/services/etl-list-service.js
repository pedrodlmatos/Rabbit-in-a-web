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

    getAllETL = () => {
        try {
            return axios.get(API_URL + 'procedures', { headers: authHeader() })
        } catch (e) {
            console.log(e);
        }
    }


    /**
     *
     */

    getUserETL = () => {
        const user = JSON.parse(localStorage.getItem('user'));
        
        try {
            return axios.get(API_URL + "user_procedures", { headers: authHeader(), params: { username: user.username } });
        } catch (e) {
            console.log(e);
        }
    }


    
    deleteETLProcedure(id) {
        try {
            return axios.delete(API_URL + "procedures", { headers: authHeader(), params: {etl_id: id }});
            //return axios.get(API_URL + 'procedures', { headers: authHeader() })
        } catch(e) {
            console.log(e);
        }
    }


    markETLProcedureAsDeleted(id) {
        const username = JSON.parse(localStorage.getItem('user')).username;
        try {
            return axios.put(API_URL + "procedures_del", null, { headers: authHeader(), params: {etl_id: id, username: username }});
        } catch(e) {
            console.log(e);
        }
    }


    unmarkETLProcedureAsDeleted(id) {
        const username = JSON.parse(localStorage.getItem('user')).username;
        try {
            return axios.put(API_URL + "procedures_undel", null, { headers: authHeader(), params: {etl_id: id, username: username }});
        } catch(e) {
            console.log(e);
        }
    }


    /**
     * Sends GET request to API to retrieve procedure given its Id
     * 
     * @param {*} id ETL procedure's id
     * @returns ETL procedure
     */

    getETLById = async (id) => {
        const username = JSON.parse(localStorage.getItem('user')).username;
        try {
            return await axios.get(API_URL + "procedures/" + id, { headers: authHeader(), params: {username: username } });
        } catch (e) {
            console.log(e);
        }

    }


    /**
     * Sends POST request to API to create an ETL procedure
     *
     * @param name EHR database name
     * @param {*} file file containing Scan Report of the EHR database
     * @param {*} cdm OMOP CDM version
     * @returns created ETL procedure created
     */
    
    createETL = async (name, file, cdm) => {
        const username = JSON.parse(localStorage.getItem('user')).username;

        let formData = new FormData();
        formData.append("file", file);
        
        try {
            return await axios.post(API_URL + "procedures", formData, { headers: authHeaderMultiPart(), params: { name: name, cdm: cdm, username: username } });
        } catch (e) {
            console.log(e);
        }
    }

    
    createETLFromFile = async (file) => {
        let formData = new FormData();
        formData.append("file", file);
        const username = JSON.parse(localStorage.getItem('user')).username;

        try {
            return await axios.post(API_URL + "procedures/save", formData, { headers: authHeaderMultiPart(), params: {username: username} });
        } catch(e) {
            console.log(e);
        }
    }


    /**
     * Sends PUT request to change the OMOP CDM version
     *
     * @param etl ETL procedure
     * @param cdm CDM version to change to
     * @returns {Promise<AxiosResponse<any>>} ETL procedure with new data
     */

    changeTargetDatabase(etl_id, cdm) {
        const username = JSON.parse(localStorage.getItem('user')).username;
        return axios.put(
            API_URL + "procedures/targetDB", 
            null, 
            { headers: authHeader(), params:{ username: username, etl_id: etl_id, cdm: cdm }}
        );
    }


    addStemTables(etl) {
        return axios.put(API_URL + "procedures/stem", null, { headers: authHeader(), params:{etl: etl }});
    }


    /**
     * Sends GET request to retrieve data from source fields summary file
     * 
     * @param {*} etl etl's id
     * @returns 
     */

    downloadSourceFieldsFile = async (etl) => {
        return await axios.get(API_URL + "procedures/sourceCSV", { headers: authHeader(), params: { etl: etl }})
    }


    downloadTargetFieldsFile = async (etl) => {
        return await axios.get(API_URL + "procedures/targetCSV", { headers: authHeader(), params: { etl: etl }, responseType: "blob"})
    }


    downloadSaveFile(etl) {
        return axios.get(API_URL + "procedures/save", { headers: authHeader(), params: {etl: etl }});
    }

    downloadSummaryFile(etl) {
        return axios.get(API_URL + "procedures/summary", { headers: authHeader(), params: {etl: etl }, responseType: "blob"});
    }

    removeStemTables (etl) {
        return axios.put(API_URL + "procedures/remove_stem", null, { headers: authHeader(), params:{etl: etl }});
    }


}

export default new ETLService();