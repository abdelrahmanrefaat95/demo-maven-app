pipeline {
  agent { label 'app-staging' }

  options {
    timestamps()
    disableConcurrentBuilds()
    buildDiscarder(logRotator(numToKeepStr: '10'))
    timeout(time: 20, unit: 'MINUTES')
  }

  triggers {
    pollSCM('H/5 * * * *')
  }

  stages {
    stage('Test') {
      steps {
        sh './mvnw -B -ntp verify'
      }
    }

    stage('Archive') {
      steps {
        archiveArtifacts artifacts: 'target/devops-demo-*.jar', fingerprint: true
      }
    }

    stage('Deploy') {
      steps {
        sh 'sudo /usr/local/bin/deploy-devops-demo.sh "$WORKSPACE/target/devops-demo-1.0.0.jar"'
        sh 'curl -fsS --retry 12 --retry-delay 5 --retry-all-errors http://localhost:8080/actuator/health/readiness'
      }
    }
  }

  post {
    always {
      junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml, target/failsafe-reports/*.xml'
    }
    success {
      echo 'Tested, archived and deployed to app-staging.'
    }
    failure {
      echo 'The pipeline failed. Open the stage that turned red and read its log.'
    }
  }
}
