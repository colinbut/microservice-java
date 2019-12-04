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
            echo "Successfully built ${env.JOB_BASE_NAME} - ${env.BUILD_ID} on ${env.BUILD_URL}
        }
        failure {
            echo "Build Failed - ${env.JOB_BASE_NAME} - ${env.BUILD_ID} on ${env.BUILD_URL}"
        }
        aborted {
            echo " ${env.JOB_BASE_NAME} Build - ${env.BUILD_ID} Aborted!"
        }
    }
}
