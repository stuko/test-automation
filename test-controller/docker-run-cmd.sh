docker container stop test-controller-python
docker container rm test-controller-python
# docker run -d -p 5000:5000  -e PYTHONUNBUFFERED=1 --mount type=bind,target=/app/server,source=/c/workspace/work/test-automation/test-controller  --mount type=bind,target=/app/server/volume/upload,source=/c/workspace/work/test-automation/volume/upload --mount type=bind,target=/app/server/volume/config,source=/c/workspace/work/test-automation/volume/config  --mount type=bind,target=/app/server/volume/result,source=/c/workspace/work/test-automation/volume/result -e MONGO_IP=${MONGO_IP} -e MONGO_PORT={MONGO_PORT} -e "TZ=Asia/Seoul" --name test-controller-python test-controller-python
docker run -d -p 5000:5000  -e PYTHONUNBUFFERED=1 -v /c/workspace/work/test-automation/test-controller:/app/server  -v /c/workspace/work/test-automation/volume/upload:/app/server/volume/upload -v /c/workspace/work/test-automation/volume/config:/app/server/volume/config  -v /c/workspace/work/test-automation/volume/result:/app/server/volume/result -e MONGO_IP=${MONGO_IP} -e MONGO_PORT={MONGO_PORT} -e "TZ=Asia/Seoul" --name test-controller-python test-controller-python