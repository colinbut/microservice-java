pipeline {
    agent any
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
        stage ('Static Code Analysis: SonarQube') {
            steps {
                withSonarQubeEnv(credentialsId: 'sonarqube-credentials', installationName: 'MySonarQubeServer') {
                    sh './mvnw sonar:sonar'
                }
            }
        }
        stage ('Tag Release') {
            steps {
                script {
                    if ("${params.SemVerTagRelease}" == "true") {
                        sh 'mvn --batch-mode release:clean release:prepare release:perform'
                    } else {
                        echo "SemVerTagRelease is false: Skipping tagging a release"
                    }
                }
            }
        }
        stage('Publish Artifact to Artifactory') {
            steps {
                rtBuildInfo(captureEnv: true)

                rtMavenResolver (
                    id: 'resolver-unique-id',
                    serverId: 'ART',
                    releaseRepo: 'libs-release',
                    snapshotRepo: 'libs-snapshot'
                )
                rtMavenDeployer(
                    id: 'deployer-unique-id',
                    serverId: 'ART',
                    releaseRepo: 'libs-release',
                    snapshotRepo: 'libs-snapshot'
                )

                rtMavenRun(
                    tool: "Maven 3.6.0",
                    pom: "pom.xml",
                    goals: "clean install -DskipTests=true",
                    resolverId: 'resolver-unique-id',
                    deployerId: 'deployer-unique-id'
                )
                
                rtPublishBuildInfo(serverId: 'ART')

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
