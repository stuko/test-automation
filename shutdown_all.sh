cd ./apache-jmeter-5.4.1/bin
pkill -9 -ef jmeter
cd ../../
cd ./kanboard/
sudo docker-compose -f docker-compose-with-mariadb.yml down
cd ../
sudo docker container stop mongo-test-automation
sudo docker container rm mongo-test-automation
cd ./test-controller/
sudo docker container stop test-controller-python
sudo docker container rm test-controller-python
cd ../
sudo docker container stop test-controller-influxdb
sudo docker container rm test-controller-influxdb



