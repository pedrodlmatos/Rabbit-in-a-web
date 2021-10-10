import React from 'react'
import './home.css'
import Carousel from 'react-material-ui-carousel'
import { Card, CardMedia, Grid, makeStyles, Typography } from '@material-ui/core'

const useStyles = makeStyles((theme) => ({
    homeContent: {
        margin: theme.spacing(1, 2)
    },
    banner: {
        position: 'relative',
        height: "600px"
    },
    bannerGrid: {
        height: '100%',
        position: 'relative'
    },
    media: {
        backgroundColor: 'white',
        objectFit: 'cover',
        width: '100%',
        height: '100%',
        overflow: 'hidden',
        position: 'relative',
        //opacity: '50%'
    },
    mediaCaption: {
        textAlign: 'center',
        textOverflow: 'ellipsis',
        position: 'absolute',
        bottom: 0,
        padding: theme.spacing(3),
        backgroundColor: 'black',
        color: 'white',
        opacity: 0.6,
        width: '100%',
        height: '20%',
        fontSize: '20px',
        fontWeight: '100',
        transition: '300ms',
        cursor: 'pointer',
        '&:hover': {
            opacity: 0.8
        }
    }
}))



const items = [
    {
        name: "Rabbit-in-a-web",
        description: 'Map concepts of EHR databases to the OMOP Common Data Model',
        image: "/carousel/rabbitinaweb_logo.jpg"
    },
    {
        name: "Other things",
        description: "DDDD",
        image: "https://source.unsplash.com/featured/?iphone"
    }
]

export default function Home() {

    const classes = useStyles();

    function Banner(props) {
        return (
            <Card raised className={classes.banner}>
                <Grid container spacing={0} className={classes.bannerGrid}>
                    <Grid item xs={12}>
                        <CardMedia className={classes.media} image={props.item.image}>
                            <Typography className={classes.mediaCaption}>
                                <h2>{props.item.name}</h2>
                                <p>{props.item.description}</p>
                            </Typography>
                        </CardMedia>
                    </Grid>
                </Grid>
            </Card>
        )
    }

    return (
        <div className={classes.homeContent}>
            <Carousel
                autoPlay={true}
                animation="slide"
                indicators={true}
                timeout={1000}
                navButtonsAlwaysVisible={false}
                navButtonsAlwaysInvisible={false}
                cycleNavigation={true}
                //navButtonsProps={{style: {backgroundColor: 'cornflowerblue', borderRadius: 0}}}
            >
                {items.map((item, i) => {
                    return <Banner item={item} key={i}/>
                })}
            </Carousel>

            {/*
            <img className="logo" src="/rabbitinahatlogo.png" alt="Logo" />

            <p>Rabbit in a Hat is a project developed by Observational Health Data Sciences and Informatics (OHDSI) and allows to:</p>

            <ul className="text">
                <li>Read a scan created with White Rabbit</li>
                <li>Read data from OMOP Common Data Models (CDM)</li>
                <li>Map a source table of EHR database to a target table of CDM</li>
                <li>Map field between two tables</li>
            </ul>

            <p>To check documentation, click <a href="instructions">here</a></p>
            */}
        </div>
    )
}