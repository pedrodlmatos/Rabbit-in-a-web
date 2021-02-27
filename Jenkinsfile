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
                        withMaven(mavenSettingsConfig: 'b7c8e3fd-55f0-461e-a31b-f67b6965e319') {
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