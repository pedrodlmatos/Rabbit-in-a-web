import {
    Dialog,
    DialogContent,
    DialogTitle, Divider,
    makeStyles, NoSsr,
    Typography
} from '@material-ui/core'
import useAutocomplete from '@material-ui/lab/useAutocomplete';
import CloseIcon from '@material-ui/icons/Close'
import CheckIcon from '@material-ui/icons/Check'
import React, { useRef, useState } from 'react'
import Controls from '../../controls/controls';
import styled from 'styled-components'
import AuthService from '../../../services/auth-service'

const useStyles = makeStyles(theme => ({
    dialogWrapper: {
        position: 'absolute',
        top: theme.spacing(5),
        height: theme.spacing(60)
    },
    DialogTitle: {
        paddingRight: '0px',
        margin: theme.spacing(1),
        flexGrow: 1
    },
    button: {
        margin: theme.spacing(1)
    },
    deleteButton: {
        margin: theme.spacing(1),
        float: "right"
    },
    inputWrapper: {
        width: "300px",
        minHeight: "50px",
        border: "1px solid #d9d9d9",
        backgroundColor: "#fff",
        borderRadius: "4px",
        margin: "5px 0",
        padding: "1px",
        display: "flex",
        flexWrap: "wrap",

        '&:hover': {
            borderColor: "#40a9ff"
        },
        '&.focused': {
            borderColor: "#40a9ff",
            boxShadow: "0 0 0 2px rgba(24, 144, 255, 0.2)"
        },
        '& input': {
            fontSize: "14px",
            height: "50px",
            boxSizing: "borderBox",
            padding: "4px 6px",
            width: "0",
            minWidth: "30px",
            flexGrow: "1",
            border: "0",
            margin: "0",
            outline: "0"
        }
    },
    tag: {
        display: "flex",
        alignItems: "center",
        height: "45px",
        margin: theme.spacing(1),
        lineHeight: "50px",
        backgroundColor: "#fafafa",
        border: "1px solid #e8e8e8",
        borderRadius: "2px",
        boxSizing: "content-box",
        padding: "0 4px 0 10px",
        outline: "0",
        overflow: "hidden",

        '&:focus': {
            borderColor: "#40a9ff",
            backgroundColor: "#e6f7ff"
        },
        '& span': {
            overflow: "hidden",
            whiteSpace: "nowrap",
            textOverflow: "ellipsis"
        },
        '& svg': {
            fontSize: "20px",
            cursor: "pointer",
            padding: "4px"
        }
    }
}))


const Tag = styled(({ label, onDelete, ...props }) => (
    <div {...props}>
        <span>{label}</span>
        <CloseIcon onClick={onDelete} />
    </div>
))`
    display: flex;
    align-items: center;
    height: 45px;
    margin: 2px;
    line-height: 50px;
    background-color: #fafafa;
    border: 1px solid #e8e8e8;
    border-radius: 2px;
    box-sizing: content-box;
    padding: 0 4px 0 10px;
    outline: 0;
    overflow: hidden;
    
    &:focus {
        border-color: #40a9ff;
        background-color: #e6f7ff;
    }
    
    & span {
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
    }
    
    & svg {
        font-size: 20px;
        cursor: pointer;
        padding: 4px;
    }
`;


const Listbox = styled('ul')`
    width: 300px;
    margin: 2px 0 0;
    padding: 0;
    position: absolute;
    list-style: none;
    background-color: #fff;
    overflow: auto;
    max-height: 250px;
    border-radius: 4px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
    z-index: 1;
    
    & li {
        padding: 5px 12px;
        display: flex;
        
        & span {
          flex-grow: 1;
        }
        
        & svg {
          color: transparent;
        }
    }
    
    & li[aria-selected='true'] {
        background-color: #fafafa;
        font-weight: 600;
        
        & svg {
          color: #1890ff;
        }
    }
    
    & li[data-focus='true'] {
        background-color: #e6f7ff;
        cursor: pointer;
        
        & svg {
          color: #000;
        }
    }
`;


export default function InviteCollaboratorModal(props) {

    const classes = useStyles();
    const { show, setShow, etlUsers, invite, remove } = props;

    const [allUsers, setAllUsers] = useState([]);           // all users
    const [users, setUsers] = useState([]);                 // users that are not collaborators

    const ref = useRef(true);

    let {
        getRootProps, getInputLabelProps, getInputProps, getTagProps, getListboxProps,
        getOptionProps, groupedOptions, value, focused, setAnchorEl,
    } = useAutocomplete({
        id: 'customized-hook-demo',
        defaultValue: [],
        multiple: true,
        options: users,
        getOptionLabel: (option) => option.username,
    });


    /**
     * Retrieves list with all user except the ones that already have access to ETL procedure
     */

    const getListOfUsers = () => {
        // reset previous selected
        value = []

        AuthService
            .getListOfUsers()
            .then(response => {
                let tempUsers = [];
                response.data.forEach(user => {
                    let isCollaborator = etlUsers.some(item => item.username === user.username);
                    if (!isCollaborator) tempUsers.push(user);
                });
                setAllUsers(response.data);
                setUsers(tempUsers);
            });
    }


    const updateUsers = (removed) => {
        let newNoCollaborators = [];
        allUsers.forEach(user => {
            let isRemoved = user.username === removed.username;
            let isCollaborator = etlUsers.some(item => item.username === user.username);
            if (!isCollaborator || isRemoved) newNoCollaborators.push(user);
        });

        setUsers(newNoCollaborators);
    }






    return (
        <Dialog open={show} onEnter={() => getListOfUsers()} fullWidth classes={{ paper: classes.dialogWrapper }}>
            <DialogTitle >
                <div style={{ display: 'flex' }}>
                    <Typography variant="h6" component="div" className={classes.DialogTitle}>
                        Manage collaborators
                    </Typography>

                    <Controls.ActionButton color="secondary" onClick={() => {setShow(false)}}>
                        <CloseIcon />
                    </Controls.ActionButton>
                </div>
            </DialogTitle>

            <DialogContent dividers>
                {/* Users with access */}
                <div>
                    <h2>Collaborators</h2>
                    <div className={classes.inputWrapper} ref={ref}>
                        {etlUsers.map((option, index) => (
                            <div key={index} className={classes.tag}>
                                <span>{option.username}</span>
                                <CloseIcon onClick={() => { remove(option); updateUsers(option) }} />
                            </div>
                        ))}
                    </div>
                </div>

                <br />
                {/* Invite other collaborators */}
                <Divider />
                <br/>
                <h2>Invite collaborators</h2>

                <NoSsr>
                    <div>
                        <div {...getRootProps()}>
                            <div ref={setAnchorEl} className={classes.inputWrapper}>
                                {value.map((option, index) => (
                                    <Tag label={option.username} {...getTagProps({ index })}/>
                                ))}

                                <input {...getInputProps()} />
                            </div>
                        </div>
                        {groupedOptions.length > 0 ? (
                            <Listbox {...getListboxProps()}>
                                {groupedOptions.map(((option, index) => (
                                    <li {...getOptionProps({option, index})}>
                                        <span>{option.username}</span>
                                        <CheckIcon fontSize="small"/>
                                    </li>
                                )))}
                            </Listbox>
                        ) : null}
                    </div>
                </NoSsr>

                <Controls.Button
                    text="Invite"
                    size="medium"
                    onClick={() => invite(value)}
                />
            </DialogContent>
        </Dialog>
    )
}
