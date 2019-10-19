pipeline {
    agent any

    stages {
        stage ('Maven: Package Artifact Jar') {
            steps {
                sh ('mvn clean install')
            }
        }

        stage ('Docker: Build Docker Image') {
            steps {
                sh ('docker build -t microservice-java:1.0.0 .')
            }
        }
    }

    post {
        success {
            echo "Build Success"
        }
    }
}