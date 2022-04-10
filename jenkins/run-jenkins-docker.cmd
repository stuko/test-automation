docker stop jenkins
docker rm jenkins
docker run -itd --name jenkins -p 8085:8080 -v "D:\workspace\kcb-test-framework\jenkins\jenkins_home":/var/jenkins_home -v ./docker.sock:/var/run/docker.sock jenkins/jenkins:lts