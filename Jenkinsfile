#!groovy
pipeline {
    agent none
    stages {
        stage ('Compile') {
            agent {
                docker {
                    image 'maven:3-alpine'
                    args '-v /root/.m2:/root/.m2'
                }
            }
            steps {
                sh 'mvn clean compile'
            }
        }
        stage ('Unit Tests') {
            agent {
                docker {
                    image 'maven:3-alpine'
                    args '-v /root/.m2:/root/.m2'
                }
            }
            steps {
                sh 'mvn test'
            }
        }
        stage ('Package Artifact Jar') {
            agent {
                docker {
                    image 'maven:3-alpine'
                    args '-v /root/.m2:/root/.m2'
                }
            }
            steps {
                sh ('mvn clean install')
            }
        }
        stage ('Build Docker Image') {
            agent any
            environment {
                VERSION = readMavenPom().getVersion()
            }
            steps {
                sh ('docker build -t microservice-java:${VERSION} .')
            }
        }
        stage ('Publish Docker image') {
            agent any
            steps {
                withCredentials([[
                    $class: 'AmazonWebServicesCredentialsBinding',
                    credentialsId: 'AWS_CREDENTIALS',
                    accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                    secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
                ]]) {
                    sh "aws ecr get-login --no-include-email | awk '{printf ${6}}' | docker login -u AWS https://066203203749.dkr.ecr.eu-west-2.amazonaws.com --password-stdin"
                    sh ('docker push 066203203749.dkr.ecr.eu-west-2.amazonaws.com/microservice-java:${VERSION}')
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
