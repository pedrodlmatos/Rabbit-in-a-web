pipeline {
    agent any

    triggers {
        pollSCM '* * * * *'
    }

    stages {
        stage('Build') {
            steps {
                sh '''
                    cd backend/riah
                    mvn clean package -DskipTests
                '''
            }
        }
    }
}