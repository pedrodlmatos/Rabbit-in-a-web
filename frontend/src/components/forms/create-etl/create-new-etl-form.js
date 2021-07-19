import React, { useState } from 'react'
import { Grid, CircularProgress, makeStyles } from '@material-ui/core';
import Controls from '../../controls/controls';
import { useForm, Form } from '../use-form';
import { CDMVersions } from '../../../services/CDMVersions';


const useStyles = makeStyles(theme => ({
    element: {
        margin: theme.spacing(1),
        height: '50px'
    },
    fileInput: {
        width: "50px",
        marginTop: theme.spacing(1)
    },
    errorText: {
        marginLeft: theme.spacing(1),
    },
    item: {
        marginBottom: theme.spacing(1)
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
    ehrName: '',
    omop: CDMVersions.filter(function(cdm) { return cdm.id === 'CDMV60' })[0].id,
}


export default function CreateETLForm(props) {
    
    const { addSession, back, close } = props;
    const [loading, setLoading] = useState(false);
    const classes = useStyles();


    /**
     * Validates a field from the form
     *  
     * @param {*} fieldValues values
     * @returns validated fields 
     */

    const validate = (fieldValues=values) => {
        let temp = { ...errors }

        if ('ehrName' in fieldValues)
            temp.ehrName = fieldValues.ehrName ? "" : "This field is required"

        if ('ehrFile' in fieldValues) {
            if (fieldValues.ehrFile.name === undefined)
                temp.ehrFile = "This field is required"
            else
                temp.ehrFile = fieldValues.ehrFile.name.endsWith('.xlsx') ? "" : "Invalid extension"
        }

        setErrors({ ...temp })

        if (fieldValues === values) {
            return Object.values(temp).every(x => x === "")
        }
    }


    const {
        values,
        errors, setErrors,
        handleInputChange,
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
                {/* Text input to define EHR database name */}
                <Grid className={classes.item} item xs={12} sm={6} md={6} lg={6}>
                    <Controls.Input
                        className={classes.element}
                        label="EHR Name"
                        placeholder="EHR database name"
                        name="ehrName"
                        error={errors.ehrName}
                        onChange={handleInputChange}
                    />
                </Grid>

                {/* File input to choose EHR scan */}
                <Grid className={classes.item} item xs={12} sm={6} md={6} lg={6}>
                    <Controls.FileInput
                        name="ehrFile"
                        text="Choose EHR scan"
                        type="file"
                        placeholder="Upload EHR scan"
                        onChange={handleFileChange}
                        error={errors.ehrFile}
                    />
                    <p className={classes.errorText} style={{ color: errors.ehrFile === "" ? "black" : "red" }}>
                        {errors.ehrFile === "" ? values.ehrFile.name : errors.ehrFile}
                    </p>
                </Grid>

                {/* Select OMOP CDM version */}
                <Grid className={classes.item} item xs={6} sm={6} md={6} lg={6}>
                    <Controls.Select
                        className={classes.select}
                        name="omop"
                        label="OMOP CDM"
                        value={values.omop}
                        onChange={handleInputChange}
                        options={CDMVersions}
                        errors={errors.cdmId}
                    />
                </Grid>

                <Grid item xs={12} sm={12} md={12} lg={12}>
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