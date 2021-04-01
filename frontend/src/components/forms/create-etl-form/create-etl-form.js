import React, { useState } from 'react'
import { Grid, CircularProgress } from '@material-ui/core';
import Controls from '../../controls/controls';
import { useForm, Form } from '../../forms/use-form';
import { CDMVersions } from '../../../services/CDMVersions';


/**
 * Initial values of the form
 */

const initialFValues = {
    ehrName: '',
    ehrFile: '',
    omop: CDMVersions.filter(function(cdm) { return cdm.id === 'CDMV60' })[0].id,
}


export default function CreateETLForm(props) {
    
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

        // TODO: Validate from

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
                <Grid item xs={12} sm={6} md={6} lg={6}>
                    <Controls.Input
                        label="EHR Name"
                        placeholder="EHR database name"
                        name="ehrName"
                        onChange={handleInputChange}
                    />
                </Grid>
                
                <Grid item xs={12} sm={6} md={6} lg={6}>
                    <Controls.FileInput
                        name="ehrFile"
                        type="file"
                        placeholder="Upload EHR scan"
                        onChange={handleFileChange} 
                    />
                    <p>{ values.ehrFile === '' ? "Upload a file" : values.ehrFile.name }</p>
                </Grid>

                <Grid item xs={12} sm={12} md={12} lg={12}>
                    <Controls.Select  name="omop" label="OMOP CDM" value={values.omop} onChange={handleInputChange} options={CDMVersions} errors={errors.departmentId} />

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