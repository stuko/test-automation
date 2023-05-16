docker container prune
cd ./kanboard/
docker-compose -f docker-compose-with-mariadb.yml down
docker-compose -f docker-compose-with-mariadb.yml up -d
cd ../

docker rm -f test-controller-python
export MY_LOCAL_IP=`hostname -I | cut -d' ' -f1`

chmod -R a+w ./apache-jmeter-5.4.1
cd ./apache-jmeter-5.4.1/bin
chmod a+x *.sh
./run-jmeter-test-server.sh
cd ../../
pkill -9 -ef com.auto.test.jmeter.plugin.common.server.ShellServer
sleep 1
nohup java -cp .:./apache-jmeter-5.4.1/lib/ext/test-automation-jmeter-plugin.jar com.auto.test.jmeter.plugin.common.server.ShellServer &

docker container stop mongo-test-automation
docker container rm mongo-test-automation
docker run -d --name mongo-test-automation -e "TZ=Asia/Seoul" -p 27017:27017 --mount type=bind,source=${PWD}/volume/mongo/data,target=/data/db -d  --restart always mongo:4.4.10
cd ./test-controller/
chmod -R a+x *.sh
./docker-build.sh
./docker-run.sh
cd ../

chmod -R 777 volume

docker network create influxdb

docker container stop test-controller-influxdb
docker container rm test-controller-influxdb
docker run -d --name=test-controller-influxdb -p 8083:8083 -p 8086:8086  --net=influxdb -v $PWD/volume/influxdb-config/influxdb.conf:/etc/influxdb/influxdb.conf -v $(pwd)/volume/influxdb:/var/lib/influxdb influxdb:1.8

docker container stop test-controller-chronograf
docker container rm test-controller-chronograf
docker run -d -p 8888:8888 --add-host=influxdb:192.168.57.224 --name=test-controller-chronograf --net=influxdb chronograf --influxdb-url=http://influxdb:8086

docker container stop test-controller-grafana
docker container rm test-controller-grafana
docker run -d --name=test-controller-grafana -p 3000:3000 -v $(pwd)/volume/grafana:/var/lib/grafana grafana/grafana
 
cd ./wiki.js/
docker-compose -f docker-compose.yml down
docker-compose -f docker-compose.yml up -d
cd ../
