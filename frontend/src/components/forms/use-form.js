import React, { useState } from 'react'
import { makeStyles } from '@material-ui/core';

export function useForm(initialFValues, validateOnChange = false, validate) {

    const [values, setValues] = useState(initialFValues);
    const [errors, setErrors] = useState({ });


    /**
     * Saves value of the field changed and validates it
     * 
     * @param {*} e input change event
     */

    const handleInputChange = e => {
        const { name, value } = e.target;

        setValues({
            ...values,
            [name]: value
        })

        if (validateOnChange) {
            validate({ [name]: value })
        }
    }


    /**
     * Saves file of the changed field and validates it
     * 
     * @param {*} e input change event
     */

    const handleFileChange = e => {
        const name = e.target.name;
        const file = e.target.files[0];

        setValues({
            ...values,
            [name]: file
        })
    }


    /**
     * Resets form to initial values
     */

    const resetForm = () => {
        setValues(initialFValues);
        setErrors({});
    }


    return {
        values, setValues,
        errors, setErrors,
        handleInputChange,
        handleFileChange,
        resetForm
    }
}

const useStyles = makeStyles(theme => ({
    root: {
        '& .MuiFormControl-root': {
            width: '80%',
            margin: theme.spacing(1)
        }
    }
}))


export function Form(props) {
    const classes = useStyles();
    const { children, ...other } = props;

    return (
        <form className={classes.root} autoComplete="off" { ...other} >
            { props.children }
        </form>
    )
}