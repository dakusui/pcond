pipeline {
    agent any
    def jdkTool = tool name: 'My JDK', type: 'hudson.model.JDK'
    env.JAVA_HOME="${jdkTool}/bin/java"
    stages {
        stage('Which Java') {
            steps {
                sh "java --version"
            }
        }
        stage('Build') {
            steps {
                echo 'Building..'
                sh "mvn -B clean"
                sh "mvn -B compile"
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }
    }
}
