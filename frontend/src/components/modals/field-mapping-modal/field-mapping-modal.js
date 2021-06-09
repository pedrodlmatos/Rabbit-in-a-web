import React, { useState } from 'react';
import { makeStyles, Dialog, DialogTitle, DialogContent, CircularProgress, Grid, FormGroup, FormControlLabel, Switch } from '@material-ui/core';
import CloseIcon from '@material-ui/icons/Close';
import TableMappingService from '../../../services/table-mapping-service';
import FieldMappingService from '../../../services/field-mapping-service';
import FieldService from '../../../services/field-service';
import Controls from '../../controls/controls';
import Xarrow from 'react-xarrows/lib';
import TableMappingLogic from '../../procedure/table-mapping-logic';
import FieldMappingLogic from './field-mapping-logic';
import SourceFieldDetails from './source-field-details'
import MappingOperations from '../../utilities/mapping-operations'
import TargetFieldDetails from './target-field-details'


const useStyles = makeStyles(theme => ({
    header: {
        height: 100,
        position: 'flex'
    },
    hiddenButton: {
        visibility: 'hidden'
    },
    showButton: {
        visibility: 'false'
    },
    button: {
        marginTop: theme.spacing(1),
        marginBottom: theme.spacing(2),
    },
    fieldDetails: {
        marginTop: theme.spacing(1),
        marginBottom: theme.spacing(1),
    }
}))

export default function FieldMappingModal(props) {
    const classes = useStyles();
    const { openModal, closeModal, etl_id, tableMappingId, removeTableMapping, changeMappingCompletion, updateTableMappingLogic } = props;
    
    const [loading, setLoading] = useState(true);

    const [ehrTable, setEHRTable] = useState({});
    const [omopTable, setOMOPTable] = useState({});
    const [complete, setComplete] = useState(false);
    const [fieldMappings, setFieldMappings] = useState([]);

    const [tableMappingLogic, setTableMappingLogic] = useState('');
    const [loadingSaveTableMappingLogic, setLoadingSaveTableMappingLogic] = useState(false);
    
    const [selectedField, setSelectedField] = useState({});
    const [sourceSelected, setSourceSelected] = useState(false);
    const [showFieldInfo, setShowFieldInfo] = useState(false);
    const [fieldInfo, setFieldInfo] = useState([]);
    const [showTable, setShowTable] = useState(false);

    const [selectedFieldMapping, setSelectedFieldMapping] = useState({});
    const [showDeleteFieldMappingButton, setShowDeleteFieldMappingButton] = useState(false);    // might delete

    const [showFieldMappingLogic, setShowFieldMappingLogic] = useState(false);
    const [savingFieldMappingLogic, setSavingFieldMappingLogic] = useState(false);


    /**
     * Retrieves information about table mapping from API
     *
     * TODO: pass info as props
     */

    const getInformation = () => {
        TableMappingService
            .getMapping(tableMappingId, etl_id)
            .then(res => {
                let maps = [];
                res.data.fieldMappings.forEach(item => {
                    const arrow = {
                        id: item.id,
                        start: item.ehrField,
                        end: item.omopField,
                        logic: item.logic,
                        color: 'grey'
                    }
                    maps = maps.concat(arrow);
                })

                setFieldMappings(maps);
                setEHRTable(res.data.ehrTable);
                setOMOPTable(res.data.omopTable);
                setComplete(res.data.complete);
                setTableMappingLogic(res.data.logic);
                setLoading(false);
            });
    }


    /**
     * Change the completion status of the table mapping
     */

    const handleCompletionChange = () => {
        TableMappingService
            .editCompleteMapping(tableMappingId, !complete, etl_id)
            .then(res => {
                setComplete(res.data.complete);
                changeMappingCompletion(tableMappingId, res.data.complete)
            }).catch(res => { console.log(res) })
    }

    
    /**
     * Save the logic from the table mapping
     */

    const saveTableMappingLogic = () => {
        setLoadingSaveTableMappingLogic(true);
        // make request to API
        TableMappingService
            .editMappingLogic(tableMappingId, tableMappingLogic, etl_id)
            .then(response => {
                setTableMappingLogic(response.data.logic);
                updateTableMappingLogic(tableMappingId, tableMappingLogic);
                setLoadingSaveTableMappingLogic(false);
            }).catch(error => { console.log(error) });
    }


    /**
     * Defines the selected field and changes state of current and previous selected field.
     *
     *  - If no field is selected, only changes the state of the selected field
     *  - If there is a field selected, unselect it and then select the new field changing states
     *  - If select the field that was previous selected, unselects it
     *
     * @param ehrField selected source field
     */

    const selectEHRField = (ehrField) => {
        // clean state
        setSelectedFieldMapping({});
        setShowFieldMappingLogic(false);
        setShowDeleteFieldMappingButton(false);
        if (Object.keys(selectedField).length === 0) {
            // no field is selected
            setSelectedField(ehrField);
            setSourceSelected(true);                                               // change color of mappings that comes from the selected field
            MappingOperations.selectMappingsFromSource(fieldMappings, ehrField);
            defineEHRFieldData(ehrField);                                                // define fields info
            setShowFieldInfo(true);
        } else if (selectedField === ehrField) {
            // select the same table
            MappingOperations.resetMappingColor(fieldMappings);                           // change color of mappings to grey
            setSelectedField({});                                                   // unselect
            setSourceSelected(false);
            setShowFieldInfo(false);
            setFieldInfo([]);
        } else {
            // select any other source table
            MappingOperations.resetMappingColor(fieldMappings);                           // change color of arrows to grey
            setSelectedField(ehrField);                                                      // change selected field information
            setSourceSelected(true);
            MappingOperations.selectMappingsFromSource(fieldMappings, ehrField);             // change color of mappings that comes from the selected field
            defineEHRFieldData(ehrField);
            setShowFieldInfo(true);                                                 // change content of fields table
        }
    }


    /**
     * Defines the selected field and changes state of current and previous selected field
     *
     * - If no field is selected, only changes the state of the selected field
     * - If theres is a source field selected, creates arrow
     * - If select the same field, unselect
     * - Else selects a different target field
     *
     * @param omopField target field
     */

    const selectOMOPField = (omopField) => {
        // clean state
        setSelectedFieldMapping({});
        setShowFieldMappingLogic(false);
        setShowDeleteFieldMappingButton(false);

        if (Object.keys(selectedField).length === 0) {
            // no field is selected
            MappingOperations.selectMappingsToTarget(fieldMappings, omopField)          // change color of mappings that goes to the selected field
            setSelectedField(omopField);                                                // change select field information
            setSourceSelected(false);
            defineOMOPFieldData(omopField);
            setShowFieldInfo(true);                                           // change content of fields table
        } else if (selectedField === omopField) {
            // select the same field -> unselect
            MappingOperations.resetMappingColor(fieldMappings);                     // change color of mappings to grey
            setSelectedField({});                                             // unselect
            setSourceSelected(false);
            setShowFieldInfo(false);
            setFieldInfo([]);
        } else if (sourceSelected) {
            // source field is selected -> create field mapping
            //resetArrowsColor();                                                     // change arrows color to grey
            createFieldMapping(selectedField.id, omopField.id);                           // create arrow
            //setSelectedField({});                                                   // unselects fields
            //setSourceSelected(false);                                               // clean state
            //setShowFieldInfo(false);
            //setFieldInfo([]);
        } else {
            // other target field is selected
            MappingOperations.resetMappingColor(fieldMappings);                     // change color of mappings to grey
            setSelectedField(omopField);                                                // change select table information
            setSourceSelected(false);
            MappingOperations.selectMappingsToTarget(fieldMappings, omopField)          // change color of mappings that goes to the selected field
            defineOMOPFieldData(omopField);
            setShowFieldInfo(true);                                           // change content of fields table
        }
    }

    
    /**
     * Defines the content of fields table (field name, type, description and value counts)
     *
     * @param ehrField selected field
     */

    const defineEHRFieldData = (ehrField) => {
        let data = [];
        setFieldInfo(data);

        if (ehrField.valueCounts.length === 0) setShowTable(false);
        else {
            ehrField.valueCounts.forEach(element => {
                data.push({
                    value: element.value,
                    frequency: element.frequency,
                    percentage: element.percentage
                })
            })
            setFieldInfo(data);
            setShowTable(true);
        }
    }


    /**
     * Defines the content of fields table (field name, type, description and list os concepts)
     *
     * @param omopField selected field
     */

    const defineOMOPFieldData = (omopField) => {
        let data = [];
        if (omopField.concepts.length === 0) setShowTable(false);
        else {
            omopField.concepts.forEach(element => {
                data.push({
                    conceptId: element.conceptId,
                    conceptName: element.conceptName,
                    conceptClassId: element.conceptClassId,
                    standardConcept: element.standardConcept
                })
            })
            setFieldInfo(data);
            setShowTable(true);
        }
    }


    /**
     * Sends request to API to create a mapping between two fields
     *
     * @param ehrFieldId source field id
     * @param omopFieldId target field id
     */

    const createFieldMapping = (ehrFieldId, omopFieldId) => {
        // verify if table mapping between those tables already exists
        let exists = false;
        fieldMappings.forEach(function (item) {
            if (item.start.id === ehrFieldId && item.end.id === omopFieldId) exists = true;
        })

        // if doesn't exist -> create
        if (!exists) {
            FieldMappingService
                .addFieldMapping(tableMappingId, ehrFieldId, omopFieldId, etl_id)
                .then(res => {
                    const arrow = {
                        id: res.data.id,
                        start: res.data.ehrField,
                        end: res.data.omopField,
                        logic: res.data.logic,
                        color: MappingOperations.defineMappingColor(selectedField, false, res.data.ehrField.id, res.data.omopField.id),
                    }
                    setFieldMappings(fieldMappings.concat(arrow));
                }).catch(res => { console.log(res) });
        }
    }


    /**
     * Selects field mapping (changes its color to red)
     *  - If no field mapping is previously selected, only selects a field mapping
     *  - If selects the field mapping previously selected, unselect it
     *  - If selects other field mapping, unselects previous and selects the new one
     * 
     * @param fieldMapping selected field mapping
     */

    const selectMapping = (fieldMapping) => {
        const index = fieldMappings.indexOf(fieldMapping);

        if (Object.keys(selectedFieldMapping).length === 0) {
            // no field mapping is selected
            setShowFieldInfo(false);                                                // unselect field
            setSelectedField({});

            MappingOperations.resetMappingColor(fieldMappings);                          // reset mapping colors to grey
            let arrows = fieldMappings;
            arrows[index].color = "red";
            setSelectedFieldMapping(fieldMapping);
            setFieldMappings(arrows);
            setShowDeleteFieldMappingButton(true);
            setShowFieldMappingLogic(true);
        } else if(selectedFieldMapping === fieldMapping) {
            // select the arrow previous selected to unselect
            MappingOperations.resetMappingColor(fieldMappings);                         // reset mapping colors to grey
            setSelectedFieldMapping({});
            setShowDeleteFieldMappingButton(false);
            setShowFieldMappingLogic(false);
        } else {
            // no field mapping is selected
            setShowFieldInfo(false);                                              // unselect field
            setSelectedField({});

            // select any other unselected arrow
            MappingOperations.resetMappingColor(fieldMappings);                         // reset mapping colors to grey
            let arrows = fieldMappings;
            arrows[index].color = "red";
            setSelectedFieldMapping(fieldMapping);                                      // select a new one
            setFieldMappings(arrows);
            setShowDeleteFieldMappingButton(true);
            setShowFieldMappingLogic(true);
        }
    }


    /**
     * Makes request to API to remove the selected field mapping
     */

    const removeSelectedFieldMapping = () => {
        FieldMappingService
            .removeFieldMapping(selectedFieldMapping.id, etl_id)
            .then(() => {
                const index = fieldMappings.findIndex(x => x.id === selectedFieldMapping.id);
                fieldMappings.splice(index);
                setSelectedFieldMapping({});
                setShowDeleteFieldMappingButton(false);
            })
    }


    /**
     * Makes a call to API to delete a field mapping and replace the previous with ones received
     *
     * @param fieldMappingId table mapping id
     */

    const removeMapping = (fieldMappingId) => {
        FieldMappingService.removeFieldMapping(fieldMappingId, etl_id).then(() => {
            let maps = []
            fieldMappings.forEach(function(item) {
                if (item.id !== fieldMappingId)
                    maps = maps.concat(item);
            });
            setFieldMappings(maps);
        }).catch(res => {
            console.log(res);
        })
    }


    /**
     * Verifies if a source field is connected to a target field
     *
     * @param omopFieldId target table's id
     * @returns true if are connected, false otherwise
     */

    const connectedToOMOPField = (omopFieldId) => {
        let result = false;
        fieldMappings.forEach(item => {
            if (item.end.id === omopFieldId && item.start.id === selectedField.id) result = true;
        })
        return result;
    }


    /**
     * Creates a field mapping between the selecte source field and the checked target field or removes it if already exists
     *
     * @param e check event with selected target field
     */

    const connectToOMOPField = e => {
        const omopFieldId = e.target.value[0];

        if (connectedToOMOPField(omopFieldId)) {
            fieldMappings.forEach(item => {
                if (item.end.id === omopFieldId && item.start.id === selectedField.id) removeMapping(item.id);
            })
        } else {
            createFieldMapping(selectedField.id, omopFieldId);
        }
    }


    /**
     * Verifies if a target field is connected to a source field
     *
     * @param ehrFieldId source table id
     * @returns true if they are connected, false otherwise
     */

    const connectedToEHRField = (ehrFieldId) => {
        let result = false;
        fieldMappings.forEach(item => {
            if (item.start.id === ehrFieldId && item.end.id === selectedField.id) result = true;
        })
        return result;
    }


    /**
     * Creates a field mapping between the selected target field and the checked source field or removes it if already exists
     *
     * @param {*} e check event with checked source field
     */

    const connectToEHRField = e => {
        const ehrFieldId = e.target.value[0];

        if (connectedToEHRField(ehrFieldId)) {
            fieldMappings.forEach(item => {
                if (item.start.id === ehrFieldId && item.end.id === selectedField.id) removeMapping(item.id);
            })
        } else
            createFieldMapping(ehrFieldId, selectedField.id);
    }


    /**
     * Sends request to save comment of field from EHR database
     */

    const saveEHRFieldComment = () => {
        FieldService
            .changeSourceFieldComment(selectedField.id, selectedField.comment, etl_id)
            .then(response => {
                const index = ehrTable.fields.findIndex(x => x.id === response.data.id);
                ehrTable.fields[index].comment = response.data.comment;
            }).catch(error => { console.log(error) });
    }


    /**
     * Sends request to save comment of field from OMOP CDM database
     */

    const saveOMOPFieldComment = () => {
        FieldService
            .changeTargetFieldComment(selectedField.id, selectedField.comment, etl_id)
            .then(response => {
                const index = omopTable.fields.findIndex(x => x.id === response.data.id);
                omopTable.fields[index].comment = response.data.comment;
            }).catch(error => console.log(error));
    }


    /**
     * Sends request to API to save field mapping logic
     */

    const saveFieldMappingLogic = () => {
        setSavingFieldMappingLogic(true);
        
        // make request to API
        FieldMappingService
            .editMappingLogic(selectedFieldMapping.id, selectedFieldMapping.logic, etl_id)
            .then(response => {
                let index = fieldMappings.findIndex(x => x.id === response.data.id);
                fieldMappings[index].logic = response.data.logic;
                setSavingFieldMappingLogic(false);
            }).catch(error => { console.log(error) });
    }

    
    return(
        <Dialog fullScreen open={openModal} onEnter={getInformation} classes={{ paper: classes.dialogWrapper }}>
            { loading ? (
                <CircularProgress color="primary" variant="indeterminate" size={40} />
            ) : (
                <div>
                    <DialogTitle>
                        <Grid container>
                            {/* Source table box */}
                            <Grid item xs={3} sm={3} md={3} lg={3}>
                                <Controls.ElementBox
                                    id={ehrTable.name + 't'}
                                    element={ehrTable}
                                    color={ehrTable.stem ? "#A000A0" : "#FF9224"}
                                    border="#000000"
                                />
                            </Grid>

                            {/* Target table box */}
                            <Grid item xs={3} sm={3} md={3} lg={3}>
                                <Controls.ElementBox
                                    id={omopTable.name + 't'}
                                    element={omopTable}
                                    color="#53ECEC"
                                    border="#000000"
                                />
                                <Xarrow
                                    start={ehrTable.name + 't'}
                                    end={omopTable.name + 't'}
                                    startAnchor="right"
                                    endAnchor="left"
                                    color={complete ? 'black' : 'grey'}
                                    strokeWidth={7.5}
                                    curveness={0.5}
                                />
                            </Grid>

                            {/* Completion switch */}
                            <Grid item xs={1} sm={1} md={1} lg={1}>
                                <FormGroup>
                                    <FormControlLabel 
                                        control={<Switch checked={complete} onChange={handleCompletionChange} color='primary'/>}
                                        label="Complete"
                                    />
                                </FormGroup>
                            </Grid>

                            {/* Remove table mapping */}
                            <Grid item xs={2} sm={2} md={2} lg={2}>
                                <Controls.Button 
                                    text="Remove table mapping"
                                    size="medium"
                                    color="secondary"
                                    variant="contained"
                                    onClick={removeTableMapping}
                                />
                            </Grid>

                            {/* Remove field mapping */}
                            <Grid item xs={2} sm={2} md={2} lg={2}>
                                <Controls.Button 
                                    text="Remove field mapping"
                                    size="medium"
                                    color="secondary"
                                    variant="contained"
                                    disabled={!showDeleteFieldMappingButton}
                                    onClick={removeSelectedFieldMapping}
                                />
                            </Grid>

                            {/* Close modal */}
                            <Grid item xs={1} sm={1} md={1} lg={1}>
                                <Controls.ActionButton color="secondary" onClick={closeModal}>
                                    <CloseIcon />
                                </Controls.ActionButton>
                            </Grid>
                        </Grid>
                    </DialogTitle>

                    <DialogContent>
                        <Grid container>
                            <Grid item xs={3} sm={3} md={3} lg={3}>
                                { ehrTable.fields.map(item => {
                                    return(
                                        <Controls.TooltipBox
                                            key={item.id}
                                            id={'s_' + item.name}
                                            element={item}
                                            handler="right"
                                            clicked={selectedField.id === item.id}
                                            help="Select first an EHR field and then an OMOP CDM field" 
                                            position="right-end"
                                            color='#FFE3C6'
                                            border="#000000"
                                            handleSelection={selectEHRField}
                                            createMapping={createFieldMapping} 
                                        />
                                    )
                                })}
                            </Grid>

                            <Grid item xs={3} sm={3} md={3} lg={3}>
                                { omopTable.fields.map(item => {
                                    return(
                                        <Controls.TooltipBox
                                            key={item.id}
                                            id={'t_' + item.name}
                                            element={item}
                                            handler="left"
                                            clicked={selectedField.id === item.id}
                                            help="Select first an EHR field and then an OMOP CDM field"
                                            position="right-end"
                                            color="#D5FFFF"
                                            border="#000000"
                                            handleSelection={selectOMOPField}
                                            createMapping={createFieldMapping}
                                        />
                                    )}
                                )}
                            </Grid>

                            { fieldMappings.map((ar, i) => (
                                <Xarrow
                                    key={i}
                                    start={'s_' + ar.start.name}
                                    end={'t_' + ar.end.name}
                                    startAnchor="right"
                                    endAnchor="left"
                                    color={ar.color}
                                    strokeWidth={7.5}
                                    curveness={0.5}
                                    passProps={{ onClick: () => selectMapping(ar) }}
                                />
                            ))}

                            <Grid item xs={6} sm={6} md={6} lg={6}>
                                <TableMappingLogic
                                    value={tableMappingLogic === null ? '' : tableMappingLogic}
                                    disabled={loadingSaveTableMappingLogic}
                                    onChange={(e) => setTableMappingLogic(e.target.value)}
                                    save={saveTableMappingLogic}
                                />

                                { showFieldInfo && (
                                    <div className={classes.fieldDetails}>
                                        { sourceSelected ? (
                                            <SourceFieldDetails
                                                field={selectedField}
                                                fieldInfo={fieldInfo}
                                                setFieldInfo={setFieldInfo}
                                                onCommentChange={(e) => setSelectedField({...selectedField, comment: e.target.value })}
                                                saveComment={saveEHRFieldComment}
                                                omopFields={omopTable.fields}
                                                verify={connectedToOMOPField}
                                                connect={connectToOMOPField}
                                            />
                                        ) : (
                                            <TargetFieldDetails
                                                field={selectedField}
                                                fieldInfo={fieldInfo}
                                                setFieldInfo={setFieldInfo}
                                                onCommentChange={(e) => setSelectedField({...selectedField, comment: e.target.value })}
                                                saveComment={saveOMOPFieldComment}
                                                ehrFields={ehrTable.fields}
                                                verify={connectedToEHRField}
                                                connect={connectToEHRField}
                                            />
                                        )}
                                    </div>
                                )}

                                { showFieldMappingLogic && (
                                    <FieldMappingLogic 
                                        value={selectedFieldMapping.logic}
                                        disabled={savingFieldMappingLogic}
                                        onChange={(e) => setSelectedFieldMapping({...selectedFieldMapping, logic: e.target.value})}
                                        save={saveFieldMappingLogic}
                                        omopFields={omopTable.fields}
                                    />
                                )}
                            </Grid>
                        </Grid>
                    </DialogContent>
                </div>
            )}
        </Dialog>
    )
}
