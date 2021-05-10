import React, { useState } from 'react'
import { Grid, CircularProgress, makeStyles } from '@material-ui/core'
import Controls from '../../controls/controls';
import { useForm, Form } from '../use-form';


const useStyles = makeStyles(theme => ({
    item: {
        marginBottom: theme.spacing(1),
    },
    errorText: {
        marginLeft: theme.spacing(1),
    },
    button: {
        margin: theme.spacing(1)
    },
    exitButton: {
        margin: theme.spacing(1),
        float: "right"
    }
}))


/**
 * Initial values of the form
 */

const initialFValues = {
    file: '',
}


export default function CreateETLFromFileForm(props) {
    
    const { addSession, back, close } = props;
    const [loading, setLoading] = useState(false);
    const classes = useStyles();


    /**
     * Validates a field from the form
     *  
     * @param {*} fieldValues values
     * @returns validated fields 
     */

    const validate = (fieldValues = values) => {
        let temp = { ...errors }

        if ('file' in fieldValues) {
            temp.file = fieldValues.file.name.endsWith('.json') ? "" : "Invalid extension"
        }

        setErrors({ ...temp })

        if (fieldValues === values) {
            return Object.values(temp).every(x => x === "")
        }
    }


    const {
        values,
        errors, setErrors,
        handleFileChange,
        resetForm
    } = useForm(initialFValues, true, validate);


    /**
     * Validates form and calls function from parent to create ETL procedure
     *  
     * @param {*} e submit event
     */

    const handleSubmit = e => {
        e.preventDefault();
        if (validate()) {
            setLoading(true);
            addSession(values);
        }
    }

    /**
     * Calls function from parent to close modal and resets form
     */

    const closeModal = () => {
        close(resetForm);
    }


    return (
        <Form onSubmit={handleSubmit}>
            <Grid container>                
                <Grid className={classes.item} item xs={12} sm={12} md={12} lg={12}>
                    <Controls.FileInput
                        name="file"
                        type="file"
                        placeholder="Upload Scan file"
                        error={errors.file}
                        onChange={handleFileChange} 
                    />
                    <p className={classes.errorText} style={{ color: errors.file === "" ? "black" : "red" }}>
                        {errors.file === "" ? values.file.name : errors.file}
                    </p>

                    { loading ? 
                        (
                            <div>
                                <Controls.Button className={classes.button} text="Back" color="default" onClick={back} disabled/>
                                <Controls.Button className={classes.button} type="submit" text="Creating" disabled>
                                    <CircularProgress color="primary" variant="indeterminate" size={20}/>
                                </Controls.Button>
    
                                <Controls.Button className={classes.exitButton} text="Close" color="secondary" onClick={closeModal} disabled />
                            </div>
                        ) : (
                            <div>
                                <Controls.Button className={classes.button} text="Back" color="default" onClick={back}/>
                                <Controls.Button className={classes.button} type="submit" text="Create" />
                                <Controls.Button className={classes.exitButton} text="Close" color="secondary" onClick={closeModal} />
                            </div>
                        ) 
                    }
                </Grid>
            </Grid>
        </Form>
    )
}