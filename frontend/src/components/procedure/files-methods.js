import ETLService from '../../services/etl-list-service'
import { saveAs } from 'file-saver';

class FilesMethods {

    /**
     *
     */

    fetchSourceFieldsFile = (etl_id) => {
        ETLService.downloadSourceFieldsFile(etl_id).then(response => {
            let blob = new Blob([response.data], { type: 'application/csv', name: 'source_fields.csv' });
            saveAs(blob, 'source_fields.csv');
        }).catch(e => console.log(e));
    }


    fetchTargetFieldsFile = (etl_id) => {
        ETLService.downloadTargetFieldsFile(etl_id).then(response => {
            let blob = new Blob([response.data], { type: 'application/csv', name: 'target_fields.csv' });
            saveAs(blob, 'target_fields.csv');
        })
    }


    fetchSaveFile = (etl_id) => {
        ETLService.downloadSaveFile(etl_id).then(response => {
            let blob = new Blob([JSON.stringify(response.data)], { type: 'application/json', name: 'Scan.json' });
            saveAs(blob, 'Scan.json');
        })
    }

    fetchSummaryFile = (etl_id) => {
        ETLService.downloadSummaryFile(etl_id).then(response => {
            let blob = new Blob([response.data], { type: 'application/octet-stream' });
            saveAs(blob, 'table_mappings.docx');
        })
    }
}

export default new FilesMethods();