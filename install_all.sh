cd ./apache-jmeter-5.4.1/bin
./jmeter-server -Djava.rmi.server.hostname=192.168.57.224 &
cd ../../
cd ./kanboard/
docker-compose -d -f docker-compose-with-mariadb up
docker run -d --name mongo-test-automation -e "TZ=Asia/Seoul" -p 27001:27001 --mount type=bind,source=${PWD}/volume/mongo/data,target=/data/db -d  --restart always mongo:4.4.10
