sudo docker image rm test-controller-python
sudo docker build -t test-controller-python .
# sudo docker run -d -p 5000:5000 --name test-controller-python test-controller-python