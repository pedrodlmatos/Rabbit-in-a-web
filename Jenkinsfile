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
                        withMaven(maven: 'maven', mavenSettingsConfig: '36ce45b9-8eb6-4068-b806-04f48c5a0a4f') {
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