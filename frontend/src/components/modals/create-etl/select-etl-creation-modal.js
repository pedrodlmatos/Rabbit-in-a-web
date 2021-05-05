import {
    Dialog,
    DialogContent,
    DialogTitle,
    Divider,
    Grid,
    IconButton,
    makeStyles,
    Typography
} from '@material-ui/core'
import Controls from '../../controls/controls'
import CloseIcon from '@material-ui/icons/Close'
import React from 'react'
import AddIcon from '@material-ui/icons/Add'
import AttachFileIcon from '@material-ui/icons/AttachFile'

const useStyles = makeStyles(theme => ({
    dialogWrapper: {
        //padding: theme.spacing(2),
        position: 'absolute',
        top: theme.spacing(5)
    },
    DialogTitle: {
        paddingRight: '0px',
        margin: theme.spacing(1),
        flexGrow: 1
    },
    root: {
        border: `1px solid ${theme.palette.divider}`,
        borderRadius: theme.shape.borderRadius,
        backgroundColor: theme.palette.background.paper,
        color: theme.palette.text.secondary,
    },
    divider: {
        marginLeft: "-1px"
    },
    iconButton: {
        width: "50px",
        height: "50px"
    }
}))

export default function CreateEtlModal(props) {

    const { title, openModal, setOpenModal, openCreateNewETLModal, openCreateETLFromFileModal} = props;
    const classes = useStyles();

    return (
        <Dialog open={openModal} fullWidth classes={{ paper: classes.dialogWrapper }}>
            <DialogTitle >
                <div style={{ display: 'flex' }}>
                    <Typography variant="h6" component="div" className={classes.DialogTitle}>
                        { title }
                    </Typography>

                    <Controls.ActionButton color="secondary" onClick={() => {setOpenModal(false)}}>
                        <CloseIcon />
                    </Controls.ActionButton>
                </div>
            </DialogTitle>

            <DialogContent dividers>
                <Grid container className={classes.root}>
                    <Grid item xs={6} sm={6} md={6} lg={6} align="center">
                        <IconButton color="inherit" onClick={openCreateNewETLModal}>
                            <AddIcon className={classes.iconButton} />
                        </IconButton>
                        <p>Create a new ETL procedure</p>
                    </Grid>
                    <Divider orientation="vertical" flexItem className={classes.divider} />
                    <Grid item xs={6} sm={6} md={6} lg={6} align="center">
                        <IconButton color="inherit" onClick={openCreateETLFromFileModal}>
                            <AttachFileIcon className={classes.iconButton} />
                        </IconButton>
                        <p>Create from file</p>
                    </Grid>
                </Grid>
            </DialogContent>
        </Dialog>
    )
}
