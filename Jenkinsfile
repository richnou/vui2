// VUI
node {
 
  properties([pipelineTriggers([[$class: 'GitHubPushTrigger']])])
  def mvnHome = tool 'maven3'

  env.JAVA_HOME="${tool 'oracle-jdk8'}"
  env.PATH="${env.JAVA_HOME}/bin:${env.PATH}"

  stage('Clean') {
    checkout scm
    sh "${mvnHome}/bin/mvn -B clean"
  }

  stage('Build') {
    sh "${mvnHome}/bin/mvn -B  compile test-compile"
  }

  stage('Test') {
    sh "${mvnHome}/bin/mvn -B -Dmaven.test.failure.ignore test"
    junit '**/target/surefire-reports/TEST-*.xml'
  }


  if (env.BRANCH_NAME == 'dev' || env.BRANCH_NAME == 'master') {
 	 stage('Deploy') {
      		sh "${mvnHome}/bin/mvn -B -Dmaven.test.failure.ignore deploy"
      		step([$class: 'ArtifactArchiver', artifacts: '**/target/*.jar', fingerprint: true])
  	}
  }


}
