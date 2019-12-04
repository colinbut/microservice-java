pipeline {
    agent {
        docker {
            image 'maven:3-alpine'
        }
    }
    environment {
        VERSION = readMavenPom().getVersion()
    }
    stages {
        stage ('Maven: Compile source code') {
            steps {
                sh 'mvn clean compile'
            }
        }
        stage ('Maven: Run Unit Tests') {
            steps {
                sh 'mvn test'
            }
        }
        stage ('Maven: Package Artifact Jar') {
            steps {
                sh ('mvn clean install')
            }
        }
        stage ('Docker: Build Docker Image') {
            steps {
                sh ('docker build -t microservice-java:${VERSION} .')
            }
        }
    }
    post {
        success {
            echo "Build Success"
            echo "Successfully built ${env.JOB_BASE_NAME} - ${env.BUILD_ID} on ${env.BUILD_URL}"
        }
        failure {
            echo "Build Failed - ${env.JOB_BASE_NAME} - ${env.BUILD_ID} on ${env.BUILD_URL}"
        }
        aborted {
            echo " ${env.JOB_BASE_NAME} Build - ${env.BUILD_ID} Aborted!"
        }
    }
}
