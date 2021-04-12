import React, { useState, useRef } from 'react';
import { Dialog, DialogContent, DialogTitle, makeStyles } from '@material-ui/core'
import Controls from '../../controls/controls';
import ETLService from '../../../services/etl-list-service';
import { CSVLink } from 'react-csv';

const useStyles = makeStyles(theme => ({ }))


export default function FilesModal(props) {

    const classes = useStyles();
    const { etl_id, openModal, closeModal } = props;
    const [sourceFieldFile, setSourceFieldFile] = useState([]);
    const [targetFieldFile, setTargetFieldFile] = useState([]);
    
    const source_csvLink = useRef();
    const target_csvLink = useRef();

    const fetchSourceFieldsFile = () => {
        ETLService.downloadSourceFieldsFile(etl_id).then(response => {
            setSourceFieldFile(response.data);
        }).catch(e => console.log(e));
        source_csvLink.current.link.click();
    }


    const fetchTargetFieldsFile = () => {
        ETLService.downloadTargetFieldsFile(etl_id).then(response => {
            setTargetFieldFile(response.data);
        }).catch(e => console.log(e));
        target_csvLink.current.link.click();
    }


    const fetchSaveFile = () => {
        ETLService.downloadSaveFile(etl_id).then(response => {
            console.log(response);
            const filename = response.headers.get('Content-Disposition').split('filename=')[1];
            console.log(filename);
            response.blob().then(blob => {
                let url = window.URL.createObjectURL(blob);
                let a = document.createElement('a');
                a.href = url;
                a.download = filename;
                a.click();
            })
        })
    }


    return(
        <Dialog open={openModal} classes={{ paper: classes.dialogWrapper }}>
            <DialogTitle>
                Summary files
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
                    <CSVLink
                        data={targetFieldFile} 
                        filename="target_fields.csv"
                        className="hidden"
                        ref={target_csvLink}
                        target="_blank"
                    />
                </div>
                <br />
                <div>
                    <Controls.Button text="Save" onClick={fetchSaveFile}/>
                </div>


            </DialogContent>
        </Dialog>
    )

}