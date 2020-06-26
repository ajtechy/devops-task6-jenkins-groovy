job("devops-task6-pull-code"){
    description("This job will pull repo from Github as developer push ")
    scm{
        github('ajtechy/devops-task6-git-kubernetes-jenkins-groovy', 'master')
    }
    triggers{
        githubPush()
    }
    wrappers {
    preBuildCleanup()
  }
}

job("devops-task6-check-code"){
    description("This job will check code and deploy environment accordingly")
  
    triggers {
        upstream("devops-task6-pull-code")
    }

    steps {
        shell(readFileFromWorkspace("check-code.sh"))
    
}

job("devops-task6-test-code") {
    description("This job will test whether our webserver is working or not and also tigger to notify job")
  
    triggers {
         upstream("devops-task6-check-code")
    }

    steps {
        shell(readFileFromWorkspace("test-webserver.sh"))
    }
}

job("devops-task6-notify-mail") {
    description("This job will  Send Mail to Developer if webserver is not working properly ")
  
    triggers {
        upstream("devops-task6-test-code")
    }
    
    publishers {
        extendedEmail {
        contentType('text/html')
        triggers {
            success{
                attachBuildLog(true)
                subject('Build successfull')
                content('The build was successful and deployment was done.')
                recipientList('anshujhalani98@gmail.com')
            }
            failure{
                attachBuildLog(true)
                subject('Failed build')
                content('The build was failed')
                recipientList('anshujhalani98@gmail.com')
            }
        }
        }
    }
}

