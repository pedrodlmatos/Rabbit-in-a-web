/**
 * Creates request to retrieve files data from API and handles file creation with received data
 */

import ETLService from '../../services/etl-list-service'
import { saveAs } from 'file-saver'

class FilesMethods {

    /**
     * Retrieves the source fields list from API and saves content as CSV file
     *
     * @param etl_id ETL procedure's id
     */

    fetchSourceFieldsFile = (etl_id) => {
        ETLService.downloadSourceFieldsFile(etl_id).then(response => {
            let blob = new Blob([response.data], { type: 'application/csv', name: 'source_fields.csv' });
            saveAs(blob, 'source_fields.csv');
        }).catch(e => console.log(e));
    }


    /**
     * Retrieves the target fields list from API and saves content as CSV file
     *
     * @param etl_id ETL procedure's id
     */

    fetchTargetFieldsFile = (etl_id) => {
        ETLService.downloadTargetFieldsFile(etl_id).then(response => {
            let blob = new Blob([response.data], { type: 'application/csv', name: 'target_fields.csv' });
            saveAs(blob, 'target_fields.csv');
        })
    }


    /**
     * Retrieves the session info file from API and saves content as JSON file.
     * Used to create an ETL procedure.
     *
     * @param etl_id ETL procedure's id
     */

    fetchSaveFile = (etl_id) => {
        ETLService.downloadSaveFile(etl_id).then(response => {
            let blob = new Blob([JSON.stringify(response.data)], { type: 'application/json', name: 'Scan.json' });
            saveAs(blob, 'Scan.json');
        })
    }


    /**
     * Retrieves the summary file from API and saves content as docx file
     *
     * @param etl_id ETL procedure's id
     */

    fetchSummaryFile = (etl_id) => {
        ETLService.downloadSummaryFile(etl_id).then(response => {
            let blob = new Blob([response.data], { type: 'application/octet-stream' });
            saveAs(blob, 'table_mappings.docx');
        })
    }
}

export default new FilesMethods();