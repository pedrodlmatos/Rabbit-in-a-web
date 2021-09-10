import { Button } from '@material-ui/core'
import React from 'react'

export default function IconAction(props) {

    const {text, icon, handleClick} = props;

    return (
        <Button onClick={handleClick}>
            {icon}
            {text}
        </Button>
    )
}