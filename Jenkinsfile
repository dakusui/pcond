pipeline {
    agent any

    stages {
        stage('Which Java') {
            sh "java --version"
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
