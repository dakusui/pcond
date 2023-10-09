pipeline {
    agent any
    tools {
        jdk 'OPENJDK-8'
    }
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
