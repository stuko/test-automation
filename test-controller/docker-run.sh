sudo docker container stop test-controller-python
sudo docker container rm test-controller-python
sudo docker run -d -p 5000:5000 -e MONGO_IP=${MONGO_IP} -e MONGO_PORT={MONGO_PORT} --name test-controller-python test-controller-python