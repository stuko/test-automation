chmod a+x ./apache-jmeter-5.4.1/bin/*.sh ./apache-jmeter-5.4.1/bin/jmeter*
sudo docker image rm test-controller-python
sudo docker build -t test-controller-python .
#sudo docker build -t self/test-controller-python:lates .
# sudo docker run -d -p 5000:5000 --name test-controller-python test-controller-python
