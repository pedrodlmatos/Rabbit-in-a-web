import { Grid, makeStyles } from '@material-ui/core'
import React, { useState, useEffect } from 'react'
import ETLService from '../../services/etl-list-service';
import Controls from '../controls/controls';
import HelpModal from '../modals/help-modal/help-modal';

const useStyles = makeStyles(theme => ({
    tablesArea: {
        marginTop: theme.spacing(3),
        marginBottom: theme.spacing(3),
        marginRight: theme.spacing(6),
        marginLeft: theme.spacing(6)
    }

}))


export default function Session() {
    const classes = useStyles();
    const [mappings, setMappings] = useState([]);
    const [etl, setEtl] = useState({});
    const [showHelpModal, setShowHelpModal] = useState(false); 

    useEffect(() => {
        const session_id = window.location.pathname.toString().replace("/session/", "");

        ETLService.getETLById(session_id).then(res => {
            setEtl({
                id: res.data.id,
                name: res.data.name,
                sourceDatabase: res.data.sourceDatabase,
                targetDatabase: res.data.targetDatabase
            })

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
    }, []);


    return(
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
                </Grid>

                
            </Grid>

        </div>
    )
}