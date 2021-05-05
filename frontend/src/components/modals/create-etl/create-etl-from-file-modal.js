import React from 'react'
import { Dialog, DialogTitle, DialogContent, makeStyles, Typography } from '@material-ui/core';
import CloseIcon from '@material-ui/icons/Close';
import Controls from '../../controls/controls';


const useStyles = makeStyles(theme => ({
    dialogWrapper: {
        padding: theme.spacing(2),
        position: 'absolute',
        top: theme.spacing(5)
    },
    DialogTitle: {
        paddingRight: '0px'
    }
}))


export default function CreateETLFromFileModal(props) {

    const { title, children, openModal, setOpenModal } = props;
    const classes = useStyles();

    return (
        <Dialog open={openModal} fullWidth classes={{ paper: classes.dialogWrapper }}>
            <DialogTitle className={classes.DialogTitle}>
                <div style={{ display: 'flex' }}>
                    <Typography variant="h6" component="div" style={{ flexGrow: 1}}>
                        { title }
                    </Typography>

                    <Controls.ActionButton color="secondary" onClick={() => {setOpenModal(false)}}>
                        <CloseIcon />
                    </Controls.ActionButton>
                </div>
            </DialogTitle>

            <DialogContent dividers>
                { children }
            </DialogContent>
        </Dialog>
    )
}