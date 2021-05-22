import {
    Dialog,
    DialogContent,
    DialogTitle,
    makeStyles,
    Typography
} from '@material-ui/core'
import Controls from '../../controls/controls'
import CloseIcon from '@material-ui/icons/Close'
import React from 'react'

const useStyles = makeStyles(theme => ({
    dialogWrapper: {
        position: 'absolute',
        top: theme.spacing(5)
    },
    DialogTitle: {
        paddingRight: '0px',
        margin: theme.spacing(1),
        flexGrow: 1
    },
    button: {
        margin: theme.spacing(1)
    },
    deleteButton: {
        margin: theme.spacing(1),
        float: "right"
    }
}))

export default function DeleteModal(props) {

    const { show, setShow, deleteProcedure } = props;
    const classes = useStyles();

    return (
        <Dialog open={show} fullWidth classes={{ paper: classes.dialogWrapper }}>
            <DialogTitle >
                <div style={{ display: 'flex' }}>
                    <Typography variant="h6" component="div" className={classes.DialogTitle}>
                        Delete ETL Procedure
                    </Typography>

                    <Controls.ActionButton color="secondary" onClick={() => {setShow(false)}}>
                        <CloseIcon />
                    </Controls.ActionButton>
                </div>
            </DialogTitle>

            <DialogContent dividers>
                <Typography variant="h6" component="div" className={classes.DialogTitle}>
                    Do you want to remove this ETL procedure?
                </Typography>

                <div>
                    <Controls.Button className={classes.button} text="Close" color="default" onClick={() => setShow(false)} />
                    <Controls.Button className={classes.deleteButton} text="Delete" color="secondary" onClick={deleteProcedure} />
                </div>
            </DialogContent>
        </Dialog>
    )
}
