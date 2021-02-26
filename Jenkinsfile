pipeline {
    agent any

    stages {
        docker {
            image 'maven:3-alpine' 
            args '-v /root/.m2:/root/.m2' 
        }

        /*
         * Maven build backend projetct
         */
        stage('Maven Build') {
            when {
                expression { params.Build }
            }
            parallel {
                stage('Backend project') {
                    steps {
                        withMaven(maven: 'maven-latest', globalMavenSettingsConfig: 'default-global-settings') {
                            sh '''
                                cd backend/riah
                                mvn clean package -DskipTest
                            '''
                        }
                    }
                }
            }   
        }
    }

    parameters {}
}