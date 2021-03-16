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
                                cd backend/hiah
                                mvn clean package -DskipTest
                            '''
                        }
                    }
                }
            }   
        }

        /*
         * Push jar artifacts to artifactory
         */
        stage('Push jar artifacts to artifactory') {
            when {
                expression { params.Publish }
            }
            parallel {
                stage('Backend project') {
                    steps {
                        withMaven(maven: 'maven', mavenSettingsConfig: '36ce45b9-8eb6-4068-b806-04f48c5a0a4f') {
                            sh '''
                                cd backend/hiah
                                mvn deploy -DskipTest
                            '''
                        }
                    }
                }
            }
        }

        /*
         * Build docker images locally
         */
        stage('Docker build') {
            when {
                expression { params.Build }
            }
            parallel {
                stage('Backend project') {
                    steps {
                        sh '''
                            cd backend/hiah
                            docker build -t hiah-backend .
                        '''
                    }
                }

                state('Frontend project') {
                    steps {
                        sh '''
                            cd frontend
                            docker build -t hiah-frontend .
                        '''
                    }
                }
            }
        }

        /*
         * Push new images to docker registry
         */
        stage('Docker registry push') {
            when {
                expression { params.Publish }
            }
            parallel {
                stage('Backend project') {
                    steps {
                        sh '''
                            docker tag hiah-backend 35.233.0.56:5000/hiah/hiah:runtime
                            docker push 35.233.0.56:5000/hiah/hiah:runtime
                        '''
                    }
                }

                stage('Backend project') {
                    steps {
                        sh '''
                            docker tag hiah-frontend 35.233.0.56:5000/hiah/hiah:runtime
                            docker push 35.233.0.56:5000/hiah/hiah:runtime
                        '''
                    }
                }
            }
        }
    }

    parameters {
        booleanParam(name: 'Build', defaultValue: true, description: 'Build and Dockerize applications')
        booleanParam(name: 'Publish', defaultValue: true, description: 'Publish artifacts to artifactory and docker registry')
    }
}