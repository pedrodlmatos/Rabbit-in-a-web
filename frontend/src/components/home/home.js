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
        height: "400px"
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
    },
    mediaCaption: {
        textAlign: 'center',
        textOverflow: 'ellipsis',
        position: 'absolute',
        bottom: 0,
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


/* Items in the carousel */
const items = [
    {
        name: "Rabbit-in-a-web",
        description: 'Map concepts of EHR databases to the OMOP Common Data Model',
        image: "/carousel/rabbitinaweb_logo.jpg"
    },
    {
        name: "Dissertation project",
        description: "Application developed under the Dissertation for Master Degree",
        image: "/carousel/web_app.jpeg"
    }
]

export default function Home() {

    const classes = useStyles();

    function Banner(props) {
        return (
            <Card raised className={classes.banner}>
                <Grid container spacing={0} className={classes.bannerGrid}>
                    <Grid item xs={12}>
                        <CardMedia className={classes.media} image={props.item.image} />
                        <Typography className={classes.mediaCaption}>
                            <h2>{props.item.name}</h2>
                            <p>{props.item.description}</p>
                        </Typography>
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
            >
                {items.map((item, i) => {
                    return <Banner item={item} key={i}/>
                })}
            </Carousel>
        </div>
    )
}