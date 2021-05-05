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
    }
}))

export default function ETLModal(props) {

    const { title, show, setShow, children } = props;
    const classes = useStyles();

    return (
        <Dialog open={show} fullWidth classes={{ paper: classes.dialogWrapper }}>
            <DialogTitle >
                <div style={{ display: 'flex' }}>
                    <Typography variant="h6" component="div" className={classes.DialogTitle}>
                        { title }
                    </Typography>

                    <Controls.ActionButton color="secondary" onClick={() => {setShow(false)}}>
                        <CloseIcon />
                    </Controls.ActionButton>
                </div>
            </DialogTitle>

            <DialogContent dividers>
                {children}
            </DialogContent>
        </Dialog>
    )
}
