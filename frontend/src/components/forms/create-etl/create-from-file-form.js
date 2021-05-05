import React, { useState } from 'react'
import { Grid, CircularProgress } from '@material-ui/core';
import Controls from '../../controls/controls';
import { useForm, Form } from '../use-form';

/**
 * Initial values of the form
 */

const initialFValues = {
    file: '',
}


export default function FileETLForm(props) {
    
    const { addSession, close } = props;
    const [loading, setLoading] = useState(false);


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
        values, setValues,
        errors, setErrors,
        handleInputChange,
        handleFileChange,
        resetForm
    } = useForm(initialFValues, true, validate);


    /**
     * Validates form and calls function from parent to create ETL session
     *  
     * @param {*} e submit event
     */

    const handleSubmit = e => {
        e.preventDefault();
        if (validate()) {
            setLoading(true);
            addSession(values, resetForm);
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
                <Grid item xs={12} sm={12} md={12} lg={12}>
                    <Controls.FileInput
                        name="file"
                        type="file"
                        placeholder="Upload Scan file"
                        error={errors.file}
                        onChange={handleFileChange} 
                    />
                    <p>{ values.file === '' ? "Upload a file" : values.file.name }</p>

                    { loading ? 
                        (
                            <div>
                                <Controls.Button type="submit" text="Creating" disabled>
                                    <CircularProgress color="primary" variant="indeterminate" size={20}/>
                                </Controls.Button>
    
                                <Controls.Button text="Close" color="secondary" onClick={closeModal} disabled />
                            </div>
                        ) : (
                            <div>
                                <Controls.Button type="submit" text="Create" />
                                <Controls.Button text="Close" color="secondary" onClick={closeModal} />
                            </div>
                        ) 
                    }
                </Grid>
            </Grid>
        </Form>
    )
}