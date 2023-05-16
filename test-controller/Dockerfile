FROM python:3.9
LABEL maintainer="tu.ko@koreacb.com"
COPY . /app/server 
WORKDIR /app/server
RUN pip3 install -r requirements.txt
ENTRYPOINT ["python", "test-controller.py"]
