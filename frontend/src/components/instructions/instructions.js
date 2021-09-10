import { Divider, Grid, List, ListItem, ListItemText, makeStyles, Typography, } from '@material-ui/core'
import { useState } from 'react'

const useStyles = makeStyles((theme) => ({

    divider: {
        marginLeft: "-1px",
        minHeight: '100%'
    },
    content: {
        marginLeft: theme.spacing(2),
        marginTop: theme.spacing(2),
        padding: theme.spacing(3),
    },
}))

export default function Instructions() {
    const classes = useStyles();
    const [createETL, setCreateETL] = useState(true);

    const resetVars = () => {
        setCreateETL(false);
    }

    const showCreateETL = () => {
        resetVars();
        setCreateETL(true);
    }

    return (
        <Grid container>
            <Grid item xs={2} sm={2} md={2} lg={2}>
                <List>
                    <ListItem button onClick={() => showCreateETL()}>
                        <ListItemText primary={"Create ETL procedure"}/>
                    </ListItem>

                    <ListItem button onClick={() => showCreateETL()}>
                        <ListItemText primary={"Create ETL procedure"}/>
                    </ListItem>

                    <ListItem button onClick={() => showCreateETL()}>
                        <ListItemText primary={"Create ETL procedure"}/>
                    </ListItem>
                </List>
            </Grid>
            <Divider orientation="vertical" flexItem className={classes.divider} />
            <Grid item xs={10} sm={10} md={10} lg={10}>
                {/* ETL procedure creation */}
                <div className={classes.content}>
                    {createETL && (
                        <Typography paragraph>
                            Click the button to create an ETL procedure and it will be presented two ways of creating one
                        </Typography>
                    )}
                </div>
            </Grid>
        </Grid>
    )
}
