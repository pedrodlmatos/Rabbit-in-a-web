pipeline {
    agent any

    stages {

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

    parameters {
        booleanParam(name: 'Build', defaultValue: true, description: 'Build and Dockerize applications')
    }
}