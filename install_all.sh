cd ./apache-jmeter-5.4.1/bin
chmod a+x *.sh jmeter*
pkill -9 -ef jmeter
./jmeter-server -Djava.rmi.server.hostname=192.168.57.224 > jmeter-server.out &
cd ../../
cd ./kanboard/
sudo docker-compose -f docker-compose-with-mariadb.yml down
sudo docker-compose -f docker-compose-with-mariadb.yml up -d
cd ../
sudo docker container stop --name mongo-test-automation
sudo docker run -d --name mongo-test-automation -e "TZ=Asia/Seoul" -p 27001:27001 --mount type=bind,source=${PWD}/volume/mongo/data,target=/data/db -d  --restart always mongo:4.4.10
cd ./test-controller/
chmod a+x *.sh
./docker-run.sh
cd ../



