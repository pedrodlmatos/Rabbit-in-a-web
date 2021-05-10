import React  from 'react';
import { Dialog, DialogContent, DialogTitle } from '@material-ui/core'
import CloseIcon from '@material-ui/icons/Close';
import Controls from '../../controls/controls';
import ETLService from '../../../services/etl-list-service';
import { saveAs } from 'file-saver';


export default function FilesModal(props) {

    const { etl_id, openModal, closeModal } = props;

    const fetchSourceFieldsFile = () => {
        ETLService.downloadSourceFieldsFile(etl_id).then(response => {
            let blob = new Blob([response.data], { type: 'application/csv', name: 'source_fields.csv' });
            saveAs(blob, 'source_fields.csv');
        }).catch(e => console.log(e));
    }


    const fetchTargetFieldsFile = () => {
        ETLService.downloadTargetFieldsFile(etl_id).then(response => {
            let blob = new Blob([response.data], { type: 'application/csv', name: 'target_fields.csv' });
            saveAs(blob, 'target_fields.csv');
        })
    }


    const fetchSaveFile = () => {
        ETLService.downloadSaveFile(etl_id).then(response => {
            let blob = new Blob([JSON.stringify(response.data)], { type: 'application/json', name: 'Scan.json' });
            saveAs(blob, 'Scan.json');
        })
    }

    const fetchSummaryFile = () => {
        ETLService.downloadSummaryFile(etl_id).then(response => {
            let blob = new Blob([response.data], { type: 'application/octet-stream' });
            saveAs(blob, 'table_mappings.docx');
        })
    }


    return(
        <Dialog open={openModal}>
            <DialogTitle>
                Summary files
                <Controls.ActionButton color="secondary" onClick={closeModal}>
                    <CloseIcon />
                </Controls.ActionButton>
            </DialogTitle>

            <DialogContent>
                <div>
                    <Controls.Button text="Source Field" onClick={fetchSourceFieldsFile} />
                </div>
                <br />
                <div>
                    <Controls.Button text="Target Field" onClick={fetchTargetFieldsFile} />
                </div>
                <br />
                <div>
                    <Controls.Button text="Save" onClick={fetchSaveFile} />
                </div>
                <br />
                <div>
                    <Controls.Button text="Summary" onClick={fetchSummaryFile} />
                </div>


            </DialogContent>
        </Dialog>
    )

}