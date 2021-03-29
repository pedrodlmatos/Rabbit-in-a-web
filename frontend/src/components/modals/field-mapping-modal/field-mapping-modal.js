import React, { useState } from 'react';
import { makeStyles, Dialog, DialogTitle, DialogContent, DialogActions, Typography, CircularProgress, Grid, FormGroup, FormControlLabel, Switch } from '@material-ui/core';
import CloseIcon from '@material-ui/icons/Close';
import TableMappingService from '../../../services/table-mapping-service';
import FieldMappingService from '../../../services/field-mapping-service';
import FieldService from '../../../services/field-service';
import EHRTable from '../../session/ehr-table';
import OMOPTable from '../../session/omop-table';
import Controls from '../../controls/controls';
import Xarrow from 'react-xarrows/lib';
import InfoTable from '../../info-table/info-table';


const useStyles = makeStyles(theme => ({
    hiddenButton: {
        visibility: 'hidden'
    },
    showButton: {
        visibility: 'false'
    }

}))

export default function FieldMappingModal(props) {

    const { openModal, closeModal, mappingId, removeTableMapping, changeMappingCompletion } = props;
    const classes = useStyles();
    const [loading, setLoading] = useState(true);
    const [sourceTable, setSourceTable] = useState(null);
    const [targetTable, setTargetTable] = useState(null);
    const [fieldMappings, setFieldMappings] = useState([]);
    const [complete, setComplete] = useState(false);
    const [logic, setLogic] = useState('');
    const [selectedField, setSelectedField] = useState({});
    const [sourceSelected, setSourceSelected] = useState(false);
    const [selectedFieldMapping, setSelectedFieldMapping] = useState({});
    const [showDeleteButton, setShowDeleteButton] = useState(false);
    const [loadingSaveLogic, setLoadingSaveLogic] = useState(false);
    const [enableEditCommentButton, setEnableEditCommentButton] = useState(true);
    const [showFieldInfo, setShowFieldInfo] = useState(false);
    const [fieldInfo, setFieldInfo] = useState([]);
    const [showTable, setShowTable] = useState(false);


    const targetColumns = React.useMemo(() => [
        {
            Header: 'Concept ID',
            accessor: 'conceptId'
        },
        {
            Header: 'Concept Name',
            accessor: 'conceptName'
        },
        {
            Header: 'Class',
            accessor: 'conceptClassId'
        },
        {
            Header: 'Standard ?',
            accessor: 'standardConcept'
        }
    ], [])
    

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
                    color: 'grey'
                }
                maps = maps.concat(arrow);
            })
            setFieldMappings(maps);
            setSourceTable(res.data.source);
            setTargetTable(res.data.target);
            setComplete(res.data.complete);
            setLogic(res.data.logic);
            setLoading(false);
        })
    }


    /**
     * 
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
     * Changes color from arrows that start in selected field and makes the
     * other lighter
     * 
     * @param {*} table selected table
     */

     const selectArrowsFromSource = (field) => { 
        fieldMappings.forEach(element => {
            if (element.start === field.name)
                element.color = 'orange';
            else
                element.color = 'lightgrey'
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
            if (element.end === field.name)
                element.color = 'blue';
            else
                element.color = 'lightgrey'
        })
    }


    /**
     * Unselects all arrows (changes color to grey)
     */

     const resetArrowsColor = () => {
        fieldMappings.forEach(element => {
            element.color = element.complete ? 'black' : 'grey'
        })
    }


    /**
     * 
     * @param {*} startField 
     * @param {*} endField 
     */

    const createFieldMapping = (startField, endField) => {
        FieldMappingService.addFieldMapping(mappingId, startField.id, endField.id).then(res => {
            const arrow = {
                id: res.data.id,
                start: startField.name,
                end: endField.name,
                color: 'grey',
            }
            setFieldMappings(fieldMappings.concat(arrow));
        }).catch(res => {
            console.log(res);
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
        if (selectedField === {}) {
            // no field is selected
            setSelectedField(field);
            setSourceSelected(true);
            // change color of mappings that comes from the selected table
            selectArrowsFromSource(field);
            // define fields info
            setShowFieldInfo(true);
            defineSourceFieldData(field);
        } else if (selectedField === field) {
            // select the same table
            // change color of arrows to grey
            resetArrowsColor();
            // unselect
            setSelectedField({});
            setSourceSelected(false);
            setShowFieldInfo(false);
            setFieldInfo([]);
        } else {
            // select any other source table
            // change color of arrows to grey
            resetArrowsColor();
            // change select table information
            setSelectedField(field);
            setSourceSelected(true);
            // change color of mappings that comes from the selected table
            selectArrowsFromSource(field);
            // change content of fields table
            setShowFieldInfo(true);
            defineSourceFieldData(field);
        }
    }

    const selectTargetField = (field) => {
        console.log(field);
        if (selectedField === {}) {
            // no field is selected
            // change color of mappings that goes to the selected field
            selectArrowsFromTarget(field);
            // change select field information
            setSelectedField(field);
            setSourceSelected(false);
            // change content of fields table
            setShowFieldInfo(true);
            defineTargetFieldData(field);
        } else if (selectedField === field) {
            // select the same field -> unselect
            // change color of arrows to grey
            resetArrowsColor();
            // unselect
            setSelectedField({});
            setSourceSelected(false);
            setShowFieldInfo(false);
            setFieldInfo([]);
        } else if (sourceSelected) {
            // source field is selected -> create arrow
            // change arrows color to grey
            resetArrowsColor();
            // create arrow
            createFieldMapping(selectedField, field)
            // unselects fields
            setSelectedField({});
            // clean state
            setSourceSelected(false);
            setShowFieldInfo(false);
            setFieldInfo([]);
        } else {
            // other target field is selected
            // change color of arrows to grey
            resetArrowsColor();
            // change select table information
            setSelectedField(field);
            setSourceSelected(false);
            // change color of mappings that comes from the selected table
            selectArrowsFromTarget(field);
            // change content of fields table
            setShowFieldInfo(true);
            defineTargetFieldData(field);
        }
    }

    /**
     * Defines the content of fields table (field name, type and description)
     *
     * @param table table with data
     */

     const defineSourceFieldData = (field) => {
        let data = [];
        /*
        field.concepts.forEach(element => {
            data.push({
                field: element.name,
                type: element.type,
                description: element.description
            })
        })*/
        setFieldInfo(data);
    }


    /**
     * Defines the content of fields table (field name, type and description)
     *
     * @param table table with data
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
     * 
     * @param {*} mapping 
     */
    const selectMapping = (mapping) => {
        const index = fieldMappings.indexOf(mapping);

        if (selectedFieldMapping === {}) {
            // no arrow is selected
            let arrows = fieldMappings;
            arrows[index].color = "red";

            setSelectedFieldMapping(mapping);
            setFieldMappings(arrows);
            setShowDeleteButton(true);
        } else if(selectedFieldMapping === mapping) {
            // select the arrow previous selected to unselect
            resetArrowsColor();
            setSelectedFieldMapping({});
            setShowDeleteButton(false);
        } else {
            // select any other unselected arrow
            // unselect previous
            resetArrowsColor();
            // select a new one
            let arrows = fieldMappings;
            arrows[index].color = "red";

            setSelectedFieldMapping(mapping);
            setFieldMappings(arrows);
            setShowDeleteButton(true);
        }
    }


    /**
     * 
     */

    const deleteFieldMapping = () => {
        FieldMappingService.removeFieldMapping(mappingId, selectedFieldMapping.id).then(
            res => {
                let maps = []
                res.data.forEach(
                    function(item) {
                        const arrow = {
                            id: item.id,
                            start: item.source.name,
                            end: item.target.name,
                            color: "grey",
                        }
                        maps = maps.concat(arrow);
                    }
                )
                setFieldMappings(maps);
                setSelectedFieldMapping({});
                setShowDeleteButton(false);
            }
        )
    }


    /**
     * 
     */

    const saveLogic = () => {
        setLoadingSaveLogic(true);

        // make request to API
        TableMappingService.editMappingLogic(mappingId, logic).then(response => {
            setLogic(response.data.logic);
            setLoadingSaveLogic(false);
        }).catch(error => {
            console.log(error);
        });
    }


    /**
     * 
     */

    const saveComment = () => {
        setEnableEditCommentButton(true);

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


    

    
    return(
        <Dialog fullScreen open={openModal} onEnter={getInformation} classes={{ paper: classes.dialogWrapper }}>
            { loading ? (
                <CircularProgress color="primary" variant="indeterminate" size={40} />
            ) : (
                <div>
                    <DialogTitle>
                        <div style={{ display: 'flex' }}>
                            <Typography variant="h6" component="div" style={{ flexGrow: 1 }}>
                                { sourceTable.name } <i className="fa fa-long-arrow-alt-right" /> { targetTable.name }
                            </Typography>

                            <Controls.ActionButton color="secondary" onClick={closeModal}>
                                <CloseIcon />
                            </Controls.ActionButton>
                            <br />

                            <FormGroup>
                                <FormControlLabel 
                                    control={<Switch checked={complete} onChange={handleCompletionChange} color='primary'/>}
                                    label="Complete"
                                />
                            </FormGroup>
                        </div>
                    </DialogTitle>

                    <DialogContent>
                        <Grid container>
                            <Grid item xs={3} sm={3} md={3} lg={3}>
                                { sourceTable.fields.map(item => {
                                    return(
                                        <EHRTable 
                                            key={item.id} 
                                            id={item.name} 
                                            table={item} 
                                            clicked={selectedField.id === item.id}
                                            handleSourceTableSelection={selectSourceField} 
                                        />
                                    )
                                })}
                            </Grid>

                            <Grid item xs={3} sm={3} md={3} lg={3}>
                            { targetTable.fields.map(item => {
                                    return(
                                        <OMOPTable 
                                            key={item.id} 
                                            id={item.name} 
                                            table={item} 
                                            clicked={selectedField.id === item.id}
                                            handleTargetTableSelection={selectTargetField} 
                                        />
                                    )
                                })}
                            </Grid>
                            { fieldMappings.map((ar, i) => (
                                <Xarrow key={i}
                                    start={ar.start}
                                    end={ar.end}
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
                                <Controls.Input 
                                    value={logic === null ? '' : logic}
                                    name="comment"
                                    fullWidth={true}
                                    label="Logic"
                                    placeholder="Edit mapping logic"
                                    rows={3} 
                                    onChange={(e) => setLogic(e.target.value)}
                                />
                                <Controls.Button
                                    text="Save"
                                    size="medium"
                                    color="primary"
                                    variant="contained"
                                    disabled={loadingSaveLogic}
                                    onClick={saveLogic}
                                />

                                { showFieldInfo ? (
                                    <div>
                                        <h6><strong>Field name: </strong>{selectedField.name}</h6>
                                        <h6><strong>Field type: </strong>{selectedField.type}</h6>

                                        { sourceSelected ? (
                                            console.log(selectedField)
                                        ) : (
                                            
                                            <div>
                                                <h6><strong>Field description: </strong>{selectedField.description}</h6>
                                                { showTable ? (
                                                    <InfoTable columns={targetColumns} data={fieldInfo}/>
                                                ) : (
                                                    <>
                                                    </>
                                                ) }
                                            </div>
                                        ) }

                                        <Controls.Input 
                                            value={selectedField.comment === null ? "" : selectedField.comment}
                                            name="comment"
                                            disabled={enableEditCommentButton}
                                            fullWidth={true}
                                            label="Comment"
                                            placeholder="Edit table comment"
                                            rows={3} 
                                            onChange={(e) => setSelectedField({...selectedField, comment: e.target.value })}
                                        />
                                        <Controls.Button
                                            className={enableEditCommentButton ? classes.showButton : classes.hiddenButton}
                                            text="Edit comment"
                                            size="medium"
                                            color="primary"
                                            variant="contained"
                                            onClick={() => setEnableEditCommentButton(false)}
                                        />

                                        <Controls.Button
                                            className={enableEditCommentButton ? classes.hiddenButton : classes.showButton}
                                            text="Save"
                                            size="medium"
                                            color="primary"
                                            variant="contained"
                                            onClick={saveComment}
                                        />
                                    </div>
                                ) : (
                                    <>
                                    </>
                                ) }
                                
                            </Grid>
                        </Grid>
                    </DialogContent>

                    <DialogActions>
                        <Controls.Button 
                            text="Close"
                            size="medium"
                            color="secondary"
                            variant="contained"
                            onClick={closeModal}
                        />

                        <Controls.Button 
                            text="Remove table mapping"
                            size="medium"
                            color="secondary"
                            variant="contained"
                            onClick={removeTableMapping}
                        />

                        <Controls.Button 
                            text="Remove field mapping"
                            size="medium"
                            color="secondary"
                            variant="contained"
                            disabled={!showDeleteButton}
                            onClick={deleteFieldMapping}
                        />
                    </DialogActions>
                </div>
            )}
        </Dialog>
    )
}