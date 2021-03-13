import React, { useState } from 'react'
import { Grid, CircularProgress } from '@material-ui/core';
import Controls from '../../controls/controls';
import { useForm, Form } from '../../forms/use-form';
import { CDMVersions } from '../../session/CDMVersions';


const initialFValues = {
    ehrFile: '',
    omop: CDMVersions.filter(function(cdm) { return cdm.id === 'CDMV60' })[0].id,
}

export default function CreateETLForm(props) {
    
    const { addSession, close } = props;
    const [loading, setLoading] = useState(false);


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
     * Validates form and sends request to API to create a new session
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

    const closeModal = () => {
        close(resetForm);
    }


    return (
        <Form onSubmit={handleSubmit}>
            <Grid container>
                <Grid item xs={12} sm={12} md={12} lg={12}>
                    <Controls.FileInput
                        name="ehrFile"
                        type="file"
                        onChange={handleFileChange} 
                    />
                    <p>
                    { values.ehrFile === '' ? "Upload a file" : values.ehrFile.name }
                    </p>    
                    

                    <Controls.Select 
                        name="omop"
                        label="OMOP CDM"
                        value={values.omop}
                        onChange={handleInputChange}
                        options={CDMVersions}
                        errors={errors.departmentId}
                    />

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