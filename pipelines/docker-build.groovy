pipeline {
    agent any
    stages {
        stage ('Compile') {
            steps {
                sh './mvnw clean compile'
            }
        }
        stage ('Unit Tests') {
            steps {
                sh './mvnw test'
            }
        }
        stage ('Package Artifact Jar') {
            steps {
                sh ('./mvnw clean package -Dmaven.test.skip=true')
            }
        }
        stage ('Build Docker Image') {
            environment {
                COMMIT_ID = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
            }
            steps {
                echo "Building a Docker image..."
                sh ('docker build -t microservice-java:${COMMIT_ID} .')
            }
        }
        stage ('Publish Docker image') {
            steps {
                script {
                    docker.withRegistry('https://066203203749.dkr.ecr.eu-west-2.amazonaws.com', 'ecr:eu-west-2:AWS_CREDENTIALS') {
                        def COMMIT_ID = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
                        docker.image("microservice-java:${COMMIT_ID}").push()
                    }
                }
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
