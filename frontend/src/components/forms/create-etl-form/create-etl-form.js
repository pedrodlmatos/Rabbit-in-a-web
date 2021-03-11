import React, { useEffect } from 'react'
import { Grid } from '@material-ui/core';
import Controls from '../../controls/controls';
import { useForm, Form } from '../../forms/use-form';
import { CDMVersions } from '../../session/CDMVersions';

const genderItems = [
    { id: 'male', title: 'Male' },
    { id: 'female', title: 'Female' },
    { id: 'other', title: 'Other' }
]

const initialFValues = {
    id: 0,
    fullName: '',
    gender: 'male',
    departmentId: '',
    isPermanent: false
}

export default function CreateETLForm(props) {
    const { addOrEdit, recordForEdit } = props;

    const validate = (fieldValues = values) => {
        let temp = { ...errors }
        if ('fullName' in fieldValues) {
            temp.fullName = fieldValues.fullName ? "" : "This field is required"
        }
        if ('departmentId' in fieldValues) {
            temp.departmentId = fieldValues.departmentId.length !== 0 ? "" : "This field is required"
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
        resetForm
    } = useForm(initialFValues, true, validate);

    const handleSubmit = e => {
        e.preventDefault();
        if (validate()) {
            addOrEdit(values, resetForm);
        }
    }

    useEffect(() => {
        if (recordForEdit != null) {
            setValues({
                ...recordForEdit
            })
        }
    })

    return (
        <Form onSubmit={handleSubmit}>
            <Grid container>
                <Grid item xs={6} sm={6} md={6} lg={6}>
                    <Controls.Input 
                        name="fullName"
                        label="Full Name"
                        value={values.fullName}
                        onChange={handleInputChange}
                        error={errors.fullName} 
                    />
                </Grid>

                <Grid item xs={6} sm={6} md={6} lg={6}>
                    <Controls.RadioGroup 
                        name="gender"
                        label="Gender"
                        value={values.gender}
                        onChange={handleInputChange}
                        items={genderItems}
                    />

                    <Controls.Select 
                        name="departmentId"
                        label="Department"
                        value={values.departmentId}
                        onChange={handleInputChange}
                        options={CDMVersions}
                        errors={errors.departmentId}
                    />


                </Grid>
            </Grid>
        </Form>
    )
}