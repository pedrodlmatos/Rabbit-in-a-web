import { CheckBox as MCheckBox } from '@material-ui/icons'

export default function Checkbox(props) {

    const {checked, onClick} = props;

    return(
        <MCheckBox checked={checked || false} onClick={onClick} />
    )
}