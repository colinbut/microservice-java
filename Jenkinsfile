pipeline {
    agent none
    tools {
        maven "Maven 3.6.0"
    }
    environment {
        NEXUS_VERSION = "nexus3"
        NEXUS_PROTOCOL = "http"
        NEXUS_URL = "35.179.15.243:8081"
        NEXUS_REPOSITORY = "repository-example"
        NEXUS_CREDENTIALS_ID = "nexus-credentials"
    }
    parameters {
        string(name: 'Builder', defaultValue: '', description: 'The person who is trigerring this build job.')
        booleanParam(name: 'NonDockerBuild', defaultValue: false, description: 'Should we do a non-docker build job? i.e. build jar artifact instead and publish to Nexus?')
        booleanParam(name: 'DockerBuild', defaultValue: false, description: 'Should we do a Docker Build Job? i.e. build docker image and publish to ECR?')
        booleanParam(name: 'SemVerTagRelease', defaultValue: false, description: 'Should we Tag the release with a SemVer version? i.e. execute a maven-release?')
    }
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
        stage ('Tag Release') {
            agent {
                docker {
                    image 'maven:3-alpine'
                    args '-v /root/.m2:/root/.m2'
                }
            }
            steps {
                script {
                    if ("${params.SemVerTagRelease}" == true) {
                        sh 'mvn --batch-mode release:clean release:prepare release:perform'
                    } else {
                        echo "SemVerTagRelease is false: Skipping tagging a release"
                    }
                }
            }
        }
        // This stage is specifically for non-containerized applications
        stage('Publish Artifact') {
            steps {
                script {
                    if ("${params.NonDockerBuild}" == true) {
                        echo "Publishing to Nexus..."
                        pom = readMavenPom file: "pom.xml";
                        filesByGlob = findFiles(glob: "target/*.${pom.packaging}");
                        echo "${filesByGlob[0].name} ${filesByGlob[0].path} ${filesByGlob[0].directory}"
                        artifactPath = filesByGlob[0].path

                        artifactExists = fileExists artifactPath

                        if (artifactExists) {
                            nexusArtifactUploader(
                                nexusVersion: NEXUS_VERSION,
                                protocol: NEXUS_PROTOCOL,
                                nexusUrl: NEXUS_URL,
                                groupId: pom.groupId,
                                version: pom.version,
                                repository: NEXUS_REPOSITORY,
                                credentialsId: NEXUS_CREDENTIALS_ID,
                                artifacts: [
                                    [artifactId: pom.artifactId, classifier: '', file: artifactPath, type: pom.packaging],
                                    [artifactId: pom.artifactId, classifier: '', file: "pom.xml", type: "pom"]
                                ]
                            );
                        } else {
                            echo "Error: File: ${artifactPath} could not be found."
                            error("Error: File: ${artifactPath} could not be found.")
                        }

                    } else {
                        echo "NonDockerBuild is false: Skipping publishing Artifact to a Repoistory Manager (Nexus)"
                    }
                }
            }
        }
        stage ('Build Docker Image') {
            agent any
            environment {
                VERSION = readMavenPom().getVersion()
            }
            steps {
                script {
                    if ("${params.DockerBuild}" == true) {
                        echo "Building a Docker image..."
                        sh ('docker build -t microservice-java:${VERSION} .')
                    } else {
                        echo "DockerBuild is false: Skipping building a Docker image"
                    }
                }
            }
        }
        stage ('Publish Docker image') {
            agent any
            steps {
                script {
                    if ("${params.DockerBuild}" == true) {
                        docker.withRegistry('https://066203203749.dkr.ecr.eu-west-2.amazonaws.com', 'ecr:eu-west-2:AWS_CREDENTIALS') {
                            def version = readMavenPom().getVersion()
                            docker.image('microservice-java:1.0.0-SNAPSHOT').push()
                        }
                    } else {
                        echo "DockerBuild is false: Skipping pushing Docker image to ECR"
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
