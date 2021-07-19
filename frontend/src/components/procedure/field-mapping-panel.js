import { Grid } from '@material-ui/core'
import Controls from '../controls/controls'
import React, { useState } from 'react'
import Xarrow from 'react-xarrows'
import MappingOperations from '../utilities/mapping-operations'

export default function FieldMappingPanel(props) {

    const {
        ehrTable,
        omopTable,
        complete,
        ehrFields,
        omopFields,
        fieldMappings,
        selectField,
        createFieldMapping,
        showFieldMapping
    } = props;

    const [selectedField, setSelectedField] = useState({});
    const [sourceSelected, setSourceSelected] = useState(false);
    const [selectedFieldMapping, setSelectedFieldMapping] = useState({});

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
        if (Object.keys(selectedField).length === 0) {
            // no field is selected
            setSelectedField(ehrField);
            setSourceSelected(true);                                               // change color of mappings that comes from the selected field
            MappingOperations.selectMappingsFromSource(fieldMappings, ehrField);
            selectField(true, ehrField, defineEHRFieldData(ehrField), true);
        } else if (selectedField === ehrField) {
            // select the same table
            MappingOperations.resetMappingColor(fieldMappings);                           // change color of mappings to grey
            setSelectedField({});                                                   // unselect
            setSourceSelected(false);
            selectField(false, ehrField, [], true);
        } else {
            // select any other source table
            MappingOperations.resetMappingColor(fieldMappings);                           // change color of arrows to grey
            setSelectedField(ehrField);                                                      // change selected field information
            setSourceSelected(true);
            MappingOperations.selectMappingsFromSource(fieldMappings, ehrField);             // change color of mappings that comes from the selected field
            selectField(true, ehrField, defineEHRFieldData(ehrField),true);
        }
    }


    /**
     * Defines the content of fields table (field name, type, description and value counts)
     *
     * @param ehrField selected field
     */

    const defineEHRFieldData = (ehrField) => {
        let data = [];
        ehrField.valueCounts.forEach(element => {
            data.push({
                value: element.value,
                frequency: element.frequency,
                percentage: element.percentage
            })
        })
        return data
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
        if (Object.keys(selectedField).length === 0) {
            // no field is selected
            MappingOperations.selectMappingsToTarget(fieldMappings, omopField)          // change color of mappings that goes to the selected field
            setSelectedField(omopField);                                                // change select field information
            setSourceSelected(false);
            selectField(true, omopField, defineOMOPFieldData(omopField), false);                                          // change content of fields table
        } else if (selectedField === omopField) {
            // select the same field -> unselect
            MappingOperations.resetMappingColor(fieldMappings);                     // change color of mappings to grey
            setSelectedField({});                                             // unselect
            setSourceSelected(false);
            selectField(false, omopField, defineOMOPFieldData(omopField), false);
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
            selectField(true, omopField, defineOMOPFieldData(omopField), false);
        }
    }

    /**
     * Defines the content of fields table (field name, type, description and list os concepts)
     *
     * @param omopField selected field
     */

    const defineOMOPFieldData = (omopField) => {
        let data = [];
        omopField.concepts.forEach(element => {
            data.push({
                conceptId: element.conceptId,
                conceptName: element.conceptName,
                conceptClassId: element.conceptClassId,
                standardConcept: element.standardConcept
            })
        })
        return data;
    }


    /**
     * Selects field mapping (changes its color to red)
     *  - If no field mapping is previously selected, only selects a field mapping
     *  - If selects the field mapping previously selected, unselect it
     *  - If selects other field mapping, unselects previous and selects the new one
     *
     * @param fieldMapping selected field mapping
     */

    const selectFieldMapping = (fieldMapping) => {
        // change color to grey
        MappingOperations.resetMappingColor(fieldMappings);

        // clean state
        setSelectedField({})
        selectField(false, {}, false);

        const index = fieldMappings.indexOf(fieldMapping);

        if (Object.keys(selectedFieldMapping).length === 0) {
            // no field mapping is selected
            fieldMappings[index].color = "red";
            setSelectedFieldMapping(fieldMapping);
            showFieldMapping(true, fieldMapping);
        } else if(selectedFieldMapping === fieldMapping) {
            // select the arrow previous selected to unselect
            setSelectedFieldMapping({});
            showFieldMapping(false, {})
            MappingOperations.resetMappingColor(fieldMappings);
        } else {
            // select any other unselected arrow
            MappingOperations.resetMappingColor(fieldMappings);                         // reset mapping colors to grey
            fieldMappings[index].color = "red";
            setSelectedFieldMapping(fieldMapping);                                      // select a new one
            showFieldMapping(true, fieldMapping);
        }
    }

    return (
        <Grid container>
            <Grid item xs={12} sm={12} md={12} lg={12}>
                <Grid container>
                    {/* EHR table */}
                    <Grid item xs={6} sm={6} md={6} lg={6}>
                        <Controls.ElementBox
                            id={ehrTable.name + 't'}
                            element={ehrTable}
                            color={ehrTable.stem ? "#A000A0" : "#FF9224"}
                            border="#000000"
                        />
                    </Grid>

                    {/* OMOP table */}
                    <Grid item xs={6} sm={6} md={6} lg={6}>
                        <Controls.ElementBox
                            id={omopTable.name + 't'}
                            element={omopTable}
                            color="#53ECEC"
                            border="#000000"
                        />
                    </Grid>
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

                <Grid container>
                    {/* EHR fields */}
                    <Grid item xs={6} sm={6} md={6} lg={6}>
                        { ehrFields.map(item => {
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

                    {/* OMOP fields */}
                    <Grid item xs={6} sm={6} md={6} lg={6}>
                        { omopFields.map(item => {
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

                    {/* field mappings */}
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
                            passProps={{
                                 onClick: () => selectFieldMapping(ar)
                            }}
                        />
                    ))}
                </Grid>
            </Grid>
        </Grid>
    )
}