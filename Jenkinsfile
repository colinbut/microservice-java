#!groovy
pipeline {
    // agent {
    //     docker {
    //         image 'maven:3-alpine'
    //     }
    // }
    agent none
    environment {
        VERSION = readMavenPom().getVersion()
    }
    stages {
        stage ('Compile') {
            agent {
                docker {
                    image 'maven:3-alpine'
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
                }
            }
            steps {
                sh ('mvn clean install')
            }
        }
        stage ('Build Docker Image') {
            agent any
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
