sudo docker image rm test/test-controller-python
sudo docker build -t test/test-controller-python:latest .
# sudo docker run -d -p 5000:5000 --name test-controller-python test-controller-python