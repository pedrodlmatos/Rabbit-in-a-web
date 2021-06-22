import { CssBaseline, makeStyles } from '@material-ui/core'

const drawerWidth = 240;

const useStyles = makeStyles((theme) => ({
    root: {
        display: 'flex'
    }
}))

export default function Instructions() {

    const classes = useStyles();

    return (
        <div className={classes.root}>
            <CssBaseline />

        </div>
    )
}