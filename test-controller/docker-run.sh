sudo docker container stop test-controller-python
sudo docker container rm test-controller-python
sudo docker run -d -p 5000:5000 --name test-controller-python test-controller-python