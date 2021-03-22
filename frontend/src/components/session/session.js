import { Grid, CircularProgress, makeStyles } from '@material-ui/core'
import React, { useState, useEffect } from 'react'
import Xarrow from 'react-xarrows/lib';
import ETLService from '../../services/etl-list-service';
import Controls from '../controls/controls';
import HelpModal from '../modals/help-modal/help-modal';
import { CDMVersions } from './CDMVersions';
import EHRTable from './ehr-table';
import OMOPTable from './omop-table';

const useStyles = makeStyles(theme => ({
    tablesArea: {
        marginTop: theme.spacing(3),
        marginBottom: theme.spacing(3),
        marginRight: theme.spacing(6),
        marginLeft: theme.spacing(6)
    },
    showFieldsInfo: {
        visibility: 'hidden'
    },
    hideFieldsInfo: {
        visibility: 'false'
    }

}))


export default function Session() {

    const initialETLValues = {
        id: null, name: null,
        targetDatabase: { id: null, tables: [], databaseName: '' },
        sourceDatabase: { id: null, tables: [], databaseName: null }
    }

    const classes = useStyles();
    const [loading, setLoading] = useState(true);
    const [etl, setEtl] = useState(initialETLValues);
    const [mappings, setMappings] = useState([]);
    const [omopName, setOmopName] = useState('');
    const [showHelpModal, setShowHelpModal] = useState(false); 
    const [showFieldsInfo, setShowFieldsInfo] = useState(false);
    const [fieldsInfo, setFieldsInfo] = useState([]);
    const [selectedTable, setSelectedTable] = useState({})
    const [sourceSelected, setSourceSelected] = useState(false);

    
    useEffect(() => {

        const session_id = window.location.pathname.toString().replace("/session/", "");
        
        ETLService.getETLById(session_id).then(res => {
            setEtl({
                id: res.data.id,
                name: res.data.name,
                sourceDatabase: res.data.sourceDatabase,
                targetDatabase: res.data.targetDatabase
            });
            setOmopName(CDMVersions.filter(function(cdm) { return cdm.id === res.data.targetDatabase.databaseName })[0].name);
            

            // table mappings
            let maps = [];
            res.data.tableMappings.forEach(function(item) {
                const arrow = {
                    id: item.id,
                    start: item.source,
                    end: item.target,
                    complete: item.complete,
                    color: item.complete ? 'black' : 'grey'
                }
                maps = maps.concat(arrow);
            });
            setMappings(maps);    
        }).catch(res => {
            console.log(res);
        })

        setLoading(false);
    }, []);


    const handleCDMChange = () => {
        // TODO
    }


    /**
     * 
     * @param {*} table 
     */
    const selectArrowsFromSource = (table) => {
        mappings.forEach(element => {
            if (element.start.name === table.name)
                element.color = 'orange';
            else
                element.color = 'lightgrey'
        })
    }


    /**
     * 
     */
    const resetArrowsColor = () => {
        mappings.forEach(element => {
            element.color = element.complete ? 'black' : 'grey'
        })
    }


    const defineData = (table) => {
        let data = [];
        table.fields.forEach(element => {
            data.push({
                field: element.name,
                type: element.type,
                description: element.description
            })
        })

        setFieldsInfo(data);
        setShowFieldsInfo(true);
    }


    /**
     * 
     * @param {*} table 
     */
    const selectSourceTable = (table) => {
        // TODO
        
        if (selectedTable === null) {
            // all tables are unselected
            setSelectedTable(table);
            setSourceSelected(true);

            // change color of mappings that comes from the selected table
            selectArrowsFromSource(table);
            // define fields info
            defineData(table);
        } else if (selectedTable === table) {
            // select the same table
            // change color of arrows to grey
            resetArrowsColor();

            // unselect
            setSelectedTable({});
            setSourceSelected(false);
            setFieldsInfo(null);
            setShowFieldsInfo(false);
        } else {
            // select any other source table

            // change color of arrows to grey
            resetArrowsColor();
            // change select table information
            setSelectedTable(table);
            setSourceSelected(true);
            // change color of mappings that comes from the selected table
            selectArrowsFromSource(table);
            // change content of fields table
            defineData(table);
        }
    }

    const selectTargetTable = () => {
        // TODO
    }

    const selectArrow = () => {
        // TODO
    }

    const openFieldMappingModal = () => {
        // TODO
    }

    return(
        <div>
            { loading ? (
                <CircularProgress color="primary" variant="indeterminate" size={40} />
            ) : (
                <div className={classes.tablesArea}>
                    <Grid container>
                        <Grid item xs={4} sm={4} md={4} lg={4}>
                            <h1>{ etl.name }</h1>
                        </Grid>

                        <Grid item xs={1} sm={1} md={1} lg={1}>
                            <Controls.Button variant="contained" size="medium" color="primary" text="Help " onClick={() => setShowHelpModal(true)}>
                                <i className="fa fa-info"/>
                            </Controls.Button>
                        </Grid>

                        <HelpModal modalIsOpen={showHelpModal} closeModal={() => setShowHelpModal(false)}/>
                    </Grid>

                    <Grid container>
                        <Grid item xs={3} sm={3} md={3} lg={3}>
                            <div>
                                <h4>{ etl.sourceDatabase.databaseName }</h4>
                            </div>

                            <div>
                                { etl.sourceDatabase.tables.map(item => {
                                    return(
                                        <EHRTable 
                                            key={item.id} 
                                            id={item.name} 
                                            table={item} 
                                            clicked={selectedTable.id === item.id}
                                            handleSourceTableSelection={selectSourceTable} 
                                        />
                                    )
                                })}
                            </div>
                        </Grid>
                            
                        <Grid item xs={3} sm={3} md={3} lg={3}>
                            <Controls.Select 
                                name={omopName} 
                                label="OMOP CDM" 
                                value={etl.targetDatabase.databaseName}
                                onChange={handleCDMChange}
                                options={CDMVersions} 
                            />

                            <div>
                                { etl.targetDatabase.tables.map(item => {
                                    return(
                                        <OMOPTable key={item.id} id={item.name} table={item} handleSourceTableSelection={selectSourceTable} />
                                    )
                                })}
                            </div>
                        </Grid>
                        { mappings.map((ar, i) => (
                            <Xarrow key={i}
                                start={ar.start.name}
                                end={ar.end.name}
                                startAnchor="right"
                                endAnchor="left"
                                color={ar.color}
                                strokeWidth={7.5}
                                curveness={0.5}
                                passProps={{
                                    onClick: selectArrow,
                                    onDoubleClick: openFieldMappingModal
                                }}
                            />
                        ))}

                        <Grid item xs={6} sm={6} md={6} lg={6}>
                            <div className={showFieldsInfo ? classes.hideFieldsInfo : classes.showFieldsInfo}>
                                <h6><strong>Table: </strong>{selectedTable === null ? '' : selectedTable.name}</h6>
                            </div>
                        </Grid>
                    </Grid>
                </div>
            )}
        </div>
    )
}