pipeline {
    agent any

    triggers {
        pollSCM '* * * * *'
    }

    stages {
        stage('Maven build') {
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
}