pipeline {
    agent any
    environment {
        NEXUS_VERSION = "nexus3"
        NEXUS_PROTOCOL = "http"
        NEXUS_URL = "35.179.15.243:8081"
        NEXUS_REPOSITORY = "repository-example"
        NEXUS_CREDENTIALS_ID = "nexus-credentials"
    }
    parameters {
        booleanParam(name: 'SemVerTagRelease', defaultValue: false, description: 'Should we Tag the release with a SemVer version? i.e. execute a maven-release?')
    }
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
        stage ('Tag Release') {
            steps {
                script {
                    if ("${params.SemVerTagRelease}" == "true") {
                        sh './mvnw --batch-mode release:clean release:prepare release:perform'
                    } else {
                        echo "SemVerTagRelease is false: Skipping tagging a release"
                    }
                }
            }
        }
        stage('Publish Artifact to Nexus') {
            steps {
                script {
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
