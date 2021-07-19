import {
    CssBaseline,
    Divider,
    Drawer,
    Hidden,
    List,
    ListItem,
    ListItemText,
    makeStyles, Typography,
    useTheme
} from '@material-ui/core'
import { useState } from 'react'

const drawerWidth = 240;

const useStyles = makeStyles((theme) => ({
    drawer: {
        width: drawerWidth,
        flexShrink: 0
    },
    drawerPaper: {
        width: drawerWidth,
    },
    drawerHeader: {
        //display: 'flex',
        alignItems: 'center',
        padding: theme.spacing(0, 1),
        // necessary for content to be below app bar
        ...theme.mixins.toolbar,
        justifyContent: 'flex-end',
    },
    content: {
        //flexGrow: 1,
        marginLeft: drawerWidth,
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
        <div>
            <Drawer
                className={classes.drawer}
                variant="persistent"
                anchor="left"
                open={true}
                classes={{ paper: classes.drawerPaper }}
            >
                <div className={classes.drawerHeader} />
                <Divider />
                <List>
                    <ListItem button onClick={() => showCreateETL()}>
                        <ListItemText primary={"Create ETL procedure"}/>
                    </ListItem>
                </List>
            </Drawer>

            <main className={classes.content}>
                {/* ETL procedure creation */}
                {createETL && (
                    <Typography paragraph>
                        Click the button to create an ETL procedure and it will be presented two ways of creating one


                    </Typography>
                )}
            </main>
        </div>

    )
}