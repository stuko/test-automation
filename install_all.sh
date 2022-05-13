sudo docker container prune
cd ./kanboard/
sudo docker-compose -f docker-compose-with-mariadb.yml down
sudo docker-compose -f docker-compose-with-mariadb.yml up -d
cd ../

sudo docker rm -f test-controller-python
export MY_LOCAL_IP=`hostname -I | cut -d' ' -f1`
cd ./apache-jmeter-5.4.1/bin
chmod a+x *.sh jmeter*
pkill -9 -ef jmeter
./jmeter-server -DTEST_AUTO=true -Djava.rmi.server.hostname=${MY_LOCAL_IP} > jmeter-server.out &
cd ../../

pkill -9 -ef com.auto.test.jmeter.plugin.common.server.ShellServer
java -cp .:./apache-jmeter-5.4.1/lib/ext/test-automation-jmeter-plugin.jar com.auto.test.jmeter.plugin.common.server.ShellServer &
sudo docker container stop mongo-test-automation
sudo docker container rm mongo-test-automation
# sudo docker run -d --name mongo-test-automation -e "TZ=Asia/Seoul" -p 27017:27017 --mount type=bind,source=${PWD}/mongodb/init,target=/docker-entrypoint-initdb.d --mount type=bind,source=${PWD}/volume/mongo/data,target=/data/db -d  --restart always mongo:4.4.10
sudo docker run -d --name mongo-test-automation -e "TZ=Asia/Seoul" -p 27017:27017 --mount type=bind,source=${PWD}/volume/mongo/data,target=/data/db -d  --restart always mongo:4.4.10
# cd ./mongodb/init/
# chmod a+x *.sh
# ./init.sh
# cd ../..
cd ./test-controller/
chmod a+x *.sh
./docker-build.sh
./docker-run.sh
cd ../



