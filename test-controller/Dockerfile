# python:3.9의 이미지로 부터
FROM python:3.9
# FROM rappdw/docker-java-python:openjdk1.8.0_171-python3.6.6

RUN apt-get update && \
apt-get install -y openjdk-11-jdk && \
apt-get install -y ant && \
apt-get clean;

# Fix certificate issues
RUN apt-get update && \
apt-get install ca-certificates-java && \
apt-get clean && \
update-ca-certificates -f;
# Setup JAVA_HOME -- useful for docker commandline
# ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64/
# RUN export JAVA_HOME

ARG MONGO_IP
ARG MONGO_PORT

ENV MONGO_IP=$MONGO_IP
ENV MONGO_PORT=$MONGO_PORT

# 제작자 및 author 기입
LABEL maintainer="tu.ko@koreacb.com"

# 해당 디렉토리에 있는 모든 하위항목들을 '/app/server`로 복사한다
# COPY . /app/server
COPY . /app/server 

# image의 directory로 이동하고
WORKDIR /app/server

# RUN cd ./apache-jmeter-5.4.1/bin && chmod a+x *.sh jmeter* && ./jmeter-server -Djava.rmi.server.hostname=$MY_LOCAL_IP > jmeter-server.out &
RUN chmod a+x ./apache-jmeter-5.4.1/bin/*.sh ./apache-jmeter-5.4.1/bin/jmeter* 
RUN cd ./apache-jmeter-5.4.1/bin && ./jmeter-server -Djava.rmi.server.hostname=$MY_LOCAL_IP > jmeter-server.out &
# RUN cd ../../
# 필요한 의존성 file들 설치
RUN pip3 install -r requirements.txt

# 환경 설정 세팅
# RUN python setup.py install

# container가 구동되면 실행
# ENTRYPOINT ["python", "test-controller.py" , "${MONGO_IP}" , "${MONGO_PORT}" ]
ENTRYPOINT ["python", "test-controller.py"]
