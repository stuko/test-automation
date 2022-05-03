sudo docker container stop test-controller-python
sudo docker container rm test-controller-python
sudo docker run -d -p 5000:5000 --mount type=bind,target=/app/server/volume/upload,source="$(pwd)"/../volume/upload --mount type=bind,target=/app/server/volume/config,source="$(pwd)"/../volume/config -e MONGO_IP=${MONGO_IP} -e MONGO_PORT={MONGO_PORT} -e "TZ=Asia/Seoul" --name test-controller-python test-controller-python