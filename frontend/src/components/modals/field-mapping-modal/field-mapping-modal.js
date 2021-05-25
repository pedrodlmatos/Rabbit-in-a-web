import React, { useState } from 'react';
import { makeStyles, Dialog, DialogTitle, DialogContent, CircularProgress, Grid, FormGroup, FormControlLabel, Switch } from '@material-ui/core';
import CloseIcon from '@material-ui/icons/Close';
import TableMappingService from '../../../services/table-mapping-service';
import FieldMappingService from '../../../services/field-mapping-service';
import FieldService from '../../../services/field-service';
import Controls from '../../controls/controls';
import Xarrow from 'react-xarrows/lib';
import InfoTable from '../../info-table/info-table';
import TableMappingLogic from '../../procedure/table-mapping-logic';
import FieldMappingLogic from './field-mapping-logic';
import SourceFieldDetails from './source-field-details'
import MappingOperations from '../../procedure/mapping-operations'
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
    const { openModal, closeModal, mappingId, removeTableMapping, changeMappingCompletion } = props;
    
    const [loading, setLoading] = useState(true);
    const [sourceTable, setSourceTable] = useState({});
    const [targetTable, setTargetTable] = useState({});
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
    const [showDeleteFieldMappingButton, setShowDeleteFieldMappingButton] = useState(false);

    const [showFieldMappingLogic, setShowFieldMappingLogic] = useState(false);
    const [savingFieldMappingLogic, setSavingFieldMappingLogic] = useState(false);


    /**
     * 
     */

    const getInformation = () => {
        TableMappingService.getMapping(mappingId).then(res => {
            let maps = [];
            res.data.fieldMappings.forEach(item => {
                const arrow = {
                    id: item.id,
                    start: item.source.name,
                    end: item.target.name,
                    logic: item.logic,
                    color: 'grey'
                }
                maps = maps.concat(arrow);
            })
            setFieldMappings(maps);
            setSourceTable(res.data.source);
            setTargetTable(res.data.target);
            setComplete(res.data.complete);
            setTableMappingLogic(res.data.logic);
            setLoading(false);
        });
    }


    /**
     * Change the completion status of the table mapping
     */

    const handleCompletionChange = () => {
        TableMappingService.editCompleteMapping(mappingId, !complete).then(res => { 
            setComplete(res.data.complete);
            changeMappingCompletion(mappingId, res.data.complete)
        }).catch(res => {
            console.log(res);
        })
    }

    
    /**
     * Save the logic from the table mapping
     */

    const saveTableMappingLogic = () => {
        setLoadingSaveTableMappingLogic(true);
        // make request to API
        TableMappingService.editMappingLogic(mappingId, tableMappingLogic).then(response => {
            setTableMappingLogic(response.data.logic);
            setLoadingSaveTableMappingLogic(false);
        }).catch(error => {
            console.log(error);
        });
    }


    /**
     * Defines the selected field and changes state of current and previous selected field.
     *
     *  - If no field is selected, only changes the state of the selected field
     *  - If there is a field selected, unselect it and then select the new field changing states
     *  - If select the field that was previous selected, unselects it
     *
     * @param field selected source field
     */

    const selectSourceField = (field) => {
        // clean state
        setSelectedFieldMapping({});
        setShowFieldMappingLogic(false);
        setShowDeleteFieldMappingButton(false);
        if (Object.keys(selectedField).length === 0) {                             // no field is selected
            setSelectedField(field);
            setSourceSelected(true);                                               // change color of mappings that comes from the selected table
            selectArrowsFromSource(field);
            setShowFieldInfo(true);                                                // define fields info
            defineSourceFieldData(field);
        } else if (selectedField === field) {                                      // select the same table
            resetArrowsColor();                                                    // change color of arrows to grey
            setSelectedField({});                                                  // unselect
            setSourceSelected(false);
            setShowFieldInfo(false);
            setFieldInfo([]);
        } else {                                                                   // select any other source table
            resetArrowsColor();                                                    // change color of arrows to grey
            setSelectedField(field);                                               // change select table information
            setSourceSelected(true);
            selectArrowsFromSource(field);                                         // change color of mappings that comes from the selected table
            setShowFieldInfo(true);                                                // change content of fields table
            defineSourceFieldData(field);
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
     * @param field target field
     */

    const selectTargetField = (field) => {
        // clean state
        setSelectedFieldMapping({});
        setShowFieldMappingLogic(false);
        setShowDeleteFieldMappingButton(false);
        if (Object.keys(selectedField).length === 0) {                              // no field is selected
            selectArrowsFromTarget(field);                                          // change color of mappings that goes to the selected field
            setSelectedField(field);                                                // change select field information
            setSourceSelected(false);
            setShowFieldInfo(true);                                                 // change content of fields table
            defineTargetFieldData(field);
        } else if (selectedField === field) {                                       // select the same field -> unselect
            resetArrowsColor();                                                     // change color of arrows to grey
            setSelectedField({});                                                   // unselect
            setSourceSelected(false);
            setShowFieldInfo(false);
            setFieldInfo([]);
        } else if (sourceSelected) {                                                // source field is selected -> create arrow
            resetArrowsColor();                                                     // change arrows color to grey
            createFieldMapping(selectedField.id, field.id);                         // create arrow
            setSelectedField({});                                                   // unselects fields
            setSourceSelected(false);                                               // clean state
            setShowFieldInfo(false);
            setFieldInfo([]);
        } else {                                                                    // other target field is selected
            resetArrowsColor();                                                     // change color of arrows to grey
            setSelectedField(field);                                                // change select table information
            setSourceSelected(false);
            selectArrowsFromTarget(field);                                          // change color of mappings that comes from the selected table
            setShowFieldInfo(true);                                                 // change content of fields table
            defineTargetFieldData(field);
        }
    }

    
    /**
     * Defines the content of fields table (field name, type, description and value counts)
     *
     * @param field selected field
     */

    const defineSourceFieldData = (field) => {
        let data = [];
        setFieldInfo(data);
        if (field.valueCounts.length === 0) {
            setShowTable(false);
        } else {
            field.valueCounts.forEach(element => {
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
     * @param field selected field
     */

    const defineTargetFieldData = (field) => {
        let data = [];
        if (field.concepts.length === 0) {
            setShowTable(false);
        } else {
            field.concepts.forEach(element => {
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
     * Changes color from arrows that start in selected field and makes the
     * other lighter
     * 
     * @param {*} table selected table
     */

    const selectArrowsFromSource = (field) => { 
        fieldMappings.forEach(element => {
            element.color = element.start === field.name ? 'orange' : 'lightgrey';
        })
    }


    /**
     * Changes color from arrows that end in selected field and makes the
     * other lighter
     * 
     * @param {*} field selected field
     */

    const selectArrowsFromTarget = (field) => {
        fieldMappings.forEach(element => {
            element.color = element.end === field.name ? 'blue' : 'lightgrey';
        })
    }


    /**
     * Unselects all arrows (changes color to grey)
     */

    const resetArrowsColor = () => {
        fieldMappings.forEach(element => {
            element.color = 'grey';
        })
    }


    /**
     * 
     * @param {*} startField 
     * @param {*} endField 
     */

    const createFieldMapping = (startField_id, endField_id) => {
        FieldMappingService.addFieldMapping(mappingId, startField_id, endField_id).then(res => {
            const arrow = {
                id: res.data.id,
                start: res.data.source.name,
                end: res.data.target.name,
                logic: res.data.logic,
                color: 'grey',
            }
            setFieldMappings(fieldMappings.concat(arrow));
        }).catch(res => {
            console.log(res);
        });
    }


    /**
     * Selects an arrow (changes its color to red)
     *  - If no arrow is previously selected, only selects an arrow
     *  - If selects the arrow previously selected, unselect it
     *  - If selects other arrow, unselects previous and selects the new one
     * 
     * @param {*} mapping selected field mapping
     */

    const selectMapping = (mapping) => {
        const index = fieldMappings.indexOf(mapping);
        if (Object.keys(selectedFieldMapping).length === 0) {                       // no arrow is selected
            let arrows = fieldMappings;
            arrows[index].color = "red";
            setSelectedFieldMapping(mapping);
            setFieldMappings(arrows);
            setShowDeleteFieldMappingButton(true);
            setShowFieldMappingLogic(true);
        } else if(selectedFieldMapping === mapping) {                               // select the arrow previous selected to unselect
            resetArrowsColor();
            setSelectedFieldMapping({});
            setShowDeleteFieldMappingButton(false);
            setShowFieldMappingLogic(false);
        } else {                                                                    // select any other unselected arrow         
            resetArrowsColor();                                                     // unselect previous
            let arrows = fieldMappings;
            arrows[index].color = "red";
            setSelectedFieldMapping(mapping);                                       // select a new one
            setFieldMappings(arrows);
            setShowDeleteFieldMappingButton(true);
            setShowFieldMappingLogic(true);
        }
    }


    /**
     * Verifies if a source field is connected to a target field
     *
     * @param {*} targetField_id target table's id
     * @returns true if are connect, false otherwise
     */

    const connectedToTarget = (targetField_id) => {
        let result = false;
        fieldMappings.forEach(item => {
            if (item.end.id === targetField_id && item.start.id === selectedField.id) {
                result = true;
            }
        })
        return result;
    }


    /**
     * Creates a field mapping between two fields or removes it if already exists
     *
     * @param {*} e check event
     */

    const connectToTargetField = e => {
        const targetField_id = e.target.value[0];

        if (connectedToTarget(targetField_id)) {
            fieldMappings.forEach(item => {
                if (item.end.id === targetField_id && item.start.id === selectedField.id) {
                    removeMapping(mappingId, item.id);
                }
            })
        } else {
            createFieldMapping(selectedField.id, targetField_id);
        }
    }

    /**
     * Verifies if a target table is connected to a source table
     *
     * @param {*} sourceField_id source table id
     * @returns true if they are connected, false otherwise
     */

    const connectedToSource = (sourceField_id) => {
        let result = false;
        fieldMappings.forEach(item => {
            if (item.start.id === sourceField_id && item.end.id === selectedField.id) {
                result = true;
            }
        })
        return result;
    }


    /**
     * Creates a table mapping between two tables or removes it if already exists
     *
     * @param {*} e check event
     */

    const connectToSourceField = e => {
        const sourceField_id = e.target.value[0];

        if (connectedToSource(sourceField_id)) {
            fieldMappings.forEach(item => {
                if (item.start.id === sourceField_id && item.end.id === selectedField.id) {
                    removeMapping(mappingId, item.id);
                }
            })
        } else {
            createFieldMapping(sourceField_id, selectedField.id);
        }
    }


    /**
     * Makes a call to API to delete a field mapping and replace the previous with ones received
     *
     * @param {*} tableMapping_id ETL id
     * @param {*} fieldMapping_id table mapping id
     */

    const removeMapping = (tableMapping_id, fieldMapping_id) => {
        FieldMappingService.removeFieldMapping(tableMapping_id, fieldMapping_id).then(res => {
            let maps = []
            res.data.forEach(function(item) {
                const arrow = {
                    id: item.id,
                    start: item.source,
                    end: item.target,
                    complete: item.complete,
                    color: "grey"
                }
                maps = maps.concat(arrow);
            });
            setFieldMappings(maps);
        }).catch(res => {
            console.log(res);
        })
    }


    /**
     * Removes the selected field mapping
     */

    const deleteFieldMapping = () => {
        FieldMappingService.removeFieldMapping(mappingId, selectedFieldMapping.id).then(() => {
            const index = fieldMappings.findIndex(x => x.id === selectedFieldMapping.id);
            fieldMappings.splice(index);
            setSelectedFieldMapping({});
            setShowDeleteFieldMappingButton(false);
        })
    }


    
    /**
     * 
     */

    const saveFieldComment = () => {
        if (sourceSelected) {
            FieldService.changeSourceTableComment(selectedField.id, selectedField.comment).then(response => {
                let fields = []
                sourceTable.fields.forEach(item => {
                    if (item.id === response.data.id) {
                        fields = fields.concat(response.data)
                    } else {
                        fields = fields.concat(item)
                    }
                })
                sourceTable.fields = fields;

                setSourceTable({
                    ...sourceTable,
                    fields: fields
                })
            }).catch(error => {
                console.log(error);
            });
        } else {
            FieldService.changeTargetTableComment(selectedField.id, selectedField.comment).then(response => {
                let fields = []
                targetTable.fields.forEach(item => {
                    if (item.id === response.data.id) {
                        fields = fields.concat(response.data)
                    } else {
                        fields = fields.concat(item)
                    }
                })
                targetTable.fields = fields;

                setTargetTable({
                    ...targetTable,
                    fields: fields
                })

            }).catch(error => {
                console.log(error);
            });
        }
    }


    /**
     * 
     */

    const saveFieldMappingLogic = () => {
        setSavingFieldMappingLogic(true);
        
        // make request to API
        FieldMappingService.editMappingLogic(selectedFieldMapping.id, selectedFieldMapping.logic).then(response => {
            let index = fieldMappings.findIndex(x => x.id === response.data.id);
            fieldMappings[index].logic = response.data.logic;
            setSavingFieldMappingLogic(false);
        }).catch(error => {
            console.log(error);
        });
    }


    

    
    return(
        <Dialog fullScreen open={openModal} onEnter={getInformation} classes={{ paper: classes.dialogWrapper }}>
            { loading ? (
                <CircularProgress color="primary" variant="indeterminate" size={40} />
            ) : (
                <div>
                    <DialogTitle>
                        <Grid container>
                            <Grid item xs={3} sm={3} md={3} lg={3}>
                                <Controls.ElementBox
                                    id={sourceTable.name + 't'}
                                    element={sourceTable}
                                    color={sourceTable.stem ? "#A000A0" : "#FF9224"}
                                    border="#000000"
                                />
                            </Grid>
                            <Grid item xs={3} sm={3} md={3} lg={3}>
                                <Controls.ElementBox
                                    id={targetTable.name + 't'}
                                    element={targetTable}
                                    color="#53ECEC"
                                    border="#000000"
                                />
                                <Xarrow
                                    start={sourceTable.name + 't'}
                                    end={targetTable.name + 't'}
                                    startAnchor="right"
                                    endAnchor="left"
                                    color={complete ? 'black' : 'grey'}
                                    strokeWidth={7.5}
                                    curveness={0.5}
                                />
                            </Grid>
                            <Grid item xs={1} sm={1} md={1} lg={1}>
                                <FormGroup>
                                    <FormControlLabel 
                                        control={<Switch checked={complete} onChange={handleCompletionChange} color='primary'/>}
                                        label="Complete"
                                    />
                                </FormGroup>
                            </Grid>
                            <Grid item xs={2} sm={2} md={2} lg={2}>
                                <Controls.Button 
                                    text="Remove table mapping"
                                    size="medium"
                                    color="secondary"
                                    variant="contained"
                                    onClick={removeTableMapping}
                                />
                            </Grid>
                            <Grid item xs={2} sm={2} md={2} lg={2}>
                                <Controls.Button 
                                    text="Remove field mapping"
                                    size="medium"
                                    color="secondary"
                                    variant="contained"
                                    disabled={!showDeleteFieldMappingButton}
                                    onClick={deleteFieldMapping}
                                />
                            </Grid>
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
                                { sourceTable.fields.map(item => {
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
                                            handleSelection={selectSourceField}
                                            createMapping={createFieldMapping} 
                                        />
                                    )
                                })}
                            </Grid>

                            <Grid item xs={3} sm={3} md={3} lg={3}>
                            { targetTable.fields.map(item => {
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
                                            handleSelection={selectTargetField}
                                            createMapping={createFieldMapping} 
                                        />
                                    )
                                })}
                            </Grid>
                            { fieldMappings.map((ar, i) => (
                                <Xarrow
                                    key={i}
                                    start={'s_' + ar.start}
                                    end={'t_' + ar.end}
                                    startAnchor="right"
                                    endAnchor="left"
                                    color={ar.color}
                                    strokeWidth={7.5}
                                    curveness={0.5}
                                    passProps={{
                                        onClick: () => selectMapping(ar)
                                    }}
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
                                                saveComment={saveFieldComment}
                                                omopFields={targetTable.fields}
                                                verify={connectedToTarget}
                                                connect={connectToTargetField}
                                            />
                                        ) : (
                                            <TargetFieldDetails
                                                field={selectedField}
                                                fieldInfo={fieldInfo}
                                                setFieldInfo={setFieldInfo}
                                                onCommentChange={(e) => setSelectedField({...selectedField, comment: e.target.value })}
                                                saveComment={saveFieldComment}
                                                ehrFields={sourceTable.fields}
                                                verify={connectedToSource}
                                                connect={connectToSourceField}
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
                                        omopFields={targetTable.fields}
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
