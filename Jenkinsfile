pipeline {
    agent any

    stages {
        /* Maven build backend projetct */
        stage('Maven Build') {
            when {
                expression { params.Build }
            }
            parallel {
                stage('Backend project') {
                    steps {
                        withMaven(maven: 'maven', mavenSettingsConfig: '7bb4ab77-7de9-46b4-8b70-ff9f823c4c9e') {
                            sh '''
                                cd backend/riaw
                                mvn clean package -DskipTest
                            '''
                        }
                    }
                }
            }   
        }

        
        /* Push jar artifacts to repository */
        stage('Push jar artifacts to repository') {
            when {
                expression { params.Publish }
            }
            parallel {
                stage('Backend project') {
                    steps {
                        withMaven(maven: 'maven', mavenSettingsConfig: '7bb4ab77-7de9-46b4-8b70-ff9f823c4c9e') {
                            sh '''
                                cd backend/riaw
                                mvn deploy -DskipTest
                            '''
                        }
                    }
                }
            }
        }


        /* Build docker images locally */
        stage('Docker build') {
            when {
                expression { params.Build }
            }
            parallel {
                stage('Backend project') {
                    steps {
                        sh '''
                            cd backend/riaw
                            docker build -t riaw-backend .
                        '''
                    }
                }

                stage('Frontend project') {
                    steps {
                        sh '''
                            cd frontend
                            docker build -t riaw-frontend .
                        '''
                    }
                }
            }
        }


        /* Push new images to docker registry */
        stage('Docker registry push') {
            when {
                expression { params.Publish }
            }
            parallel {
                stage('Backend project') {
                    steps {
                        sh '''
                            docker tag riaw-backend 35.205.223.175:5000/v2/riaw-backend:runtime
                            docker push 35.205.223.175:5000/v2/riaw-backend:runtime
                        '''
                    }
                }

                stage('Frontend project') {
                    steps {
                        sh '''
                            docker tag riaw-frontend 35.205.223.175:5000/v2/riaw-frontend:runtime
                            docker push 35.205.223.175:5000/v2/riaw-frontend:runtime
                        '''
                    }
                }
            }
        }

        /* Deploy latest images in runtime VM */
        stage('Deploy in runtime') {
            when {
                expression { params.Deploy }
            }
            steps {
                sshagent(credentials: ['runtime-key']) {
                    sh 'ssh -o StrictHostKeyChecking=no -l pedrodlmatos98 34.76.46.230 "bash -s" < deploy.sh'
                }
            }
        }
    }

    parameters {
        booleanParam(name: 'Build', defaultValue: true, description: 'Build and Dockerize applications')
        booleanParam(name: 'Publish', defaultValue: true, description: 'Publish artifacts to artifactory and docker registry')
        booleanParam(name: 'Deploy', defaultValue: true, description: 'Deploy latest images to runtime VM')
    }
}