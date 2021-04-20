import React, { useState, useRef } from 'react';
import { Dialog, DialogContent, DialogTitle, makeStyles } from '@material-ui/core'
import CloseIcon from '@material-ui/icons/Close';
import Controls from '../../controls/controls';
import ETLService from '../../../services/etl-list-service';
import { CSVLink } from 'react-csv';
import { saveAs } from 'file-saver';

const useStyles = makeStyles(theme => ({ }))


export default function FilesModal(props) {

    const classes = useStyles();
    const { etl_id, openModal, closeModal } = props;
    const [sourceFieldFile, setSourceFieldFile] = useState([]);
    const [summaryFile, setSummaryFile] = useState([]);
    
    const source_csvLink = useRef();
    const summary_link = useRef();

    const fetchSourceFieldsFile = () => {
        ETLService.downloadSourceFieldsFile(etl_id).then(response => {
            setSourceFieldFile(response.data);
        }).catch(e => console.log(e));
        source_csvLink.current.link.click();
    }


    const fetchTargetFieldsFile = () => {
        ETLService.downloadTargetFieldsFile(etl_id).then(response => {
            let blob = new Blob([response.data], { type: 'application/csv', name: 'target_fields.csv' });
            saveAs(blob, 'target_fields.csv');
        })
    }


    const fetchSaveFile = () => {
        ETLService.downloadSaveFile(etl_id).then(response => {
            console.log(response);
            //const filename = response.headers.get('Content-Disposition').split('filename=')[1];
            //console.log(filename);
            let blob = new Blob([JSON.stringify(response.data)], { type: 'application/json', name: 'Scan.json' });
            saveAs(blob, 'Scan.json');
        })
    }

    const fetchSummaryFile = () => {
        ETLService.downloadSummaryFile(etl_id).then(response => {
            //const url = window.URL.createObjectURL(new Blob([response.data]));
            //const link = document.createElement("a");
            //link.href = url;
            //link.setAttribute(
            //    "download",
            //    "table_mapping.docx"
            //);
            //document.body.appendChild(link);
            //link.click();
            //response.blob().then(blob => console.log(blob))
            
            //let blob = new Blob([response], { type: 'application/octet-stream' });
            //saveAs(blob, 'table_mappings.docx');

            var blob = new Blob([response.data], { type: 'application/octet-stream' });
            saveAs(blob, 'table_mappings.docx');
            //var link = document.createElement('a');
            //link.href = URL.createObjectURL(blob);
            // set the name of the file
            //link.download = ".docx";
            // clicking the anchor element will download the file
            //link.click();

        })
    }


    return(
        <Dialog open={openModal} classes={{ paper: classes.dialogWrapper }}>
            <DialogTitle>
                Summary files
                <Controls.ActionButton color="secondary" onClick={closeModal}>
                    <CloseIcon />
                </Controls.ActionButton>
            </DialogTitle>

            <DialogContent>
                <div>
                    <Controls.Button text="Source Field" onClick={fetchSourceFieldsFile} />
                    <CSVLink
                        data={sourceFieldFile} 
                        filename="source_fields.csv"
                        className="hidden"
                        ref={source_csvLink}
                        target="_blank"
                    />
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