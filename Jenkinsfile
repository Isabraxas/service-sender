node {
    def repoName = "service-statement-sender"
    def artifactName = "${repoName}-0.1.${BUILD_NUMBER}.jar"
    def artifactPomName = "${repoName}-0.1.${BUILD_NUMBER}.pom"

    stage('scm') {
        dir(repoName) {
            git branch: 'master',
            credentialsId: 'mycredentials',
            url: 'http://feronti@bitbucket.viridian.cc/scm/stat/' + repoName + '.git'
        }
        sh('du -hcs *')
    }
    stage('Build') {
        echo "building " + artifactName
        dir(repoName) {
            sh 'sed -i "s/.9999/.${BUILD_NUMBER}/g" pom.xml'
            sh 'sed -i "s/\\\${artifact.version}/0.1.${BUILD_NUMBER}/g" pom.xml'
            sh "mvn -Dbuild.number=${BUILD_NUMBER} -DskipTests clean package"
        }
    }
    stage('Checkstyle') {
        dir(repoName) {
            sh "mvn checkstyle:checkstyle -Dcheckstyle.config.location=viridian_checks.xml"
            publishHTML ( [
                allowMissing: false,
                alwaysLinkToLastBuild: true,
                keepAll: false,
                reportDir: 'target/site',
                reportFiles: 'checkstyle.html',
                reportName: 'HTML Report',
                reportTitles: ''
            ])

            step([
                $class: 'CheckStylePublisher',
                pattern: '**/checkstyle-result.xml',
                unstableTotalHigh: '0',
                unstableTotalNormal: '0',
                unstableNewHigh: '0',
                unstableNewNormal: '0',

                usePreviousBuildAsReference: true
            ])
        }
    }
    stage('test') {
        dir(repoName) {
            sh "mvn -Dbuild.number=${BUILD_NUMBER} -Dmaven.test.failure.ignore package"
            archiveArtifacts "target/" + artifactName
        }
         //junit '**/target/surefire-reports/TEST-*.xml'
    }
    stage("deploy") {

        dir(repoName) {
            def committerEmail = sh (
                 script: 'git log -1 --pretty=format:\'%an\' ',
                 returnStdout: true
             ).trim()

            def summary = sh (
                 script: 'git log -1 --pretty=format:\'%s\' ',
                 returnStdout: true
            ).trim()

            def slackColor = 'good';
            if (currentBuild.result == "UNSTABLE") {
                slackColor = 'danger';
            }
            def slackFooter = '';
            if (currentBuild.result != "SUCCESS") {
                slackFooter = "\n`${currentBuild.result}`";
            }
            slackSend color: slackColor,
                message: "*" + artifactName + "*\n" + summary + "\n_" + committerEmail + "_" + slackFooter

            sh '/var/lib/jenkins/viridian/deploy-' + repoName + '.sh'
        }
    }
}