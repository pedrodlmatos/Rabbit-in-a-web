import { Dialog, DialogContent, DialogTitle, makeStyles, Typography } from '@material-ui/core'
import ActionButton from './action-button'
import CloseIcon from '@material-ui/icons/Close'
import Button from './button'

const useStyles = makeStyles(theme => ({
	dialogWrapper: {
		position: 'absolute',
		top: theme.spacing(5)
	},
	title: {
		paddingRight: '0px',
		margin: theme.spacing(1),
		flexGrow: 1
	},
	buttonsDiv: {
		marginTop: theme.spacing(3),
		marginBottom: theme.spacing(1)
	},
	deleteButton: {
		float: "right"
	}
}))

export default function Modal(props) {
	const {show, setShow, title, children, onDelete} = props;
	const classes = useStyles();

	return(
		<Dialog open={show} fullWidth classes={{ paper: classes.dialogWrapper }}>
			<DialogTitle>
				<div style={{ display: 'flex' }}>
					<Typography variant="h6" component="div" className={classes.title}>
						{title}
					</Typography>

					<ActionButton color="inherit" onClick={() => {setShow(false)}}>
						<CloseIcon />
					</ActionButton>
				</div>
			</DialogTitle>

			<DialogContent dividers>
				{children}
				<div className={classes.buttonsDiv}>
					<Button text="Close" color="default" onClick={() => setShow(false)} />
					<Button className={classes.deleteButton} text="Delete" color="secondary" onClick={onDelete} />
				</div>
			</DialogContent>
		</Dialog>
	)
}