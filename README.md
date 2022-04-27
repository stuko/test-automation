# test-automation

## 테스트 프로세스
### 배경
 #### 체계적인 테스트 프로세스와 시스템이 왜 필요한가?
  * 솔루션의 개발 이후 검증은 최소 이 기간과 비슷하거나 더 많은 시간과 리소스가 투입되어야 한다. 
  * 하나의 문제를 해결하기 위해, 하나의 기능을 만들었다면, 이 기능이 문제를 일으킬 수 있는 경우의 수는 하나 이상이다.
  * 솔루션의 기능 개발과 솔루션의 오류 가능성에 대한 신뢰도는 서로 상반된 기대치를 가지고 있다.
  * 더 많은 기능을 개발을 한다면, 솔루션은 더 믿을 수 없는 품질을 기대하게 된다.(솔루션의 신뢰도 문제점)
  * 믿을 수 있는 체계적인 솔루션의 테스트 프로세스는 솔루션의 오류 가능성을 낮추는 품질 관리 중 하나이다.
 #### 최소한의 품질 보증이 왜 필요한가? 
  * 솔루션의 생명주기에는 외부 고객(사용자)에게 인도 되는 과정을 거쳐야 한다.
  * 솔루션의 인도(Delivery)는 솔루션이 제공하는 Service의 Level을 준수해야 하는 계약이 포함되어 있다.
  * 테스트를 통해, 솔루션의 Service Level을 보증하는 품질 보증 절차는 솔루션의 브랜드 품질과 고객만족도를 좌우 한다
 #### 테스트 실행에 시간과 비용이 왜 많이 발생 하는가?
  * 솔루션의 변경 범위에 해당하지 않는 기능을 테스트 해야 하는 과정이 필요함.(Regression Test)
  * 솔루션을 지속적으로 유지보수하기 위해서는 지속적인 테스트가 필요하고, 테스트 과정에서 발생하는 테스트 비용을 줄이기 위한 노력이 필요함.
  * 테스트 비용을 일정 수준으로 유지 될 수 있도록 관리 될 필요가 있음.
## 시스템 개요
 > 테스트 자동화 시스템에 참여하는 오픈소스와 개발 시스템의 개요입니다.
![Test Automation Project System Context](https://user-images.githubusercontent.com/1683771/162646776-23a210b5-a5b8-45fa-aebd-8cf3a5fb2915.png)


## 설치 방법
>
```
1. kanboard : run kanboard with mariadb (check the volume in docker-compose-with-mariadb.yml)
  docker-compose -f docker-compose-with-mariadb.yml up
```
```
2. test-controller(python) : run test-controler.py
  python ./test-controller.py
```
```
3. Jmeter(Server) : run jmeter-server.bat or jmeter-server in ./jmeter-XXX/bin/    
  You can download directly from https://jmeter.apache.org/download_jmeter.cgi
  ./jmeter-server

* Jmeter에는 빌드된 plugin이 포함되어 있습니다.
```
```
4. Mongo DB : run docker 
  docker run --name mongo-test-automation -e "TZ=Asia/Seoul" -p ${MONGO_PORT}:${MONGO_PORT} --mount type=bind,source=${BASEDIR}/volume/mongo/data,target=/data/db 
-d  --restart always mongo:4.4.10
```

That's all :)


## JMeter 테스트 플러그인 데모

https://user-images.githubusercontent.com/1683771/162646281-827b8de7-b1f9-487e-b6fa-bcdf8fd75246.mp4


https://user-images.githubusercontent.com/1683771/162646260-fe71ea1e-1fc4-4e53-9f7f-f3af942f94e8.mp4


https://user-images.githubusercontent.com/1683771/162646669-ad08a926-0593-4d1e-9a73-199fb1cf06b4.mp4

# JMeter 테스트 데이터 관리 프로그램 #

 > 솔루션의 내부에서 테스트 하기 위해, 필요한 것들은 **'테스트 시나리오' , '테스트 데이터', '테스트 프로그램', '테스트 결과서'** 가 필요합니다. 

 > 여기에서 테스트 시나리오는 솔루션 패키징 과정을 통해서, 정리한 룰 정의서의 내용을 바탕으로 꼭 필요한 룰 변수들을 추출 하였으며, 나머지 룰 변수들은 소스를 분석하여, 최대한 모든 변수들을 취합하여 테스트 시나리오에 적용되도록 하였습니다.

 > 테스트 데이터는 정확하지 않은 테스트 시나리오를 보완하기 위해, 발생할 수 있는 모든 케이스의 테스트 데이터를 만들 수 있도록, 룰 변수들의 예상되는 범위와 그 범위 안에 포함된 값들 간의 발생할 수 있는 모든 조합을 생성하도록 하였습니다.

 > 테스트 데이터의 범위를 정의 하는 작업을 일반화(누구나 가능하도록)하기 위해, 엑셀에 필요한 변수명과 유형 그리고 범위를 정의 하여 해당 엑셀의 표를 로드하여 테스트 데이터를 만들 수 있도록 지원하는 기능을 추가 개발 하였습니다.

 > 테스트 프로그램은 별도의 개발 작업을 줄이기 위해, JMeter의 테스트 기능(테스트 절차, 테스트 부하 발생, 테스트 관리)을 사용하였으며, FDS에서 필요한 테스트 데이터를 만들어 내기 위해, JMeter 플러그인을 개발하여 적용하였습니다.

 > 테스트 결과서는 현재 추가 개발 중에 있으며, 테스트의 실제 결과와 예상 결과를 비교하고, 테스트 결과 예외를 확인하고 찾아 낼 수 있는 기능을 추가 개발하고 있습니다.

## JMeter Plugin 테스트 프로그램

1. 엑셀로 테스트 데이터의 규칙과 범위를 작성하고, JMeter를 사용하여, 시스템에 테스트 데이터를 보낸 후, 데이터베이스에 시스템의 룰 조건에 맞는 정보가 들어오는지 확인

2. 테스트 데이터 정의 문서 설명
    > 엑셀에 작성하는 테스트 데이터의 규칙을 정의 하는 방법을 설명한 문서입니다.
![image](https://user-images.githubusercontent.com/1683771/163735668-6b039d54-0eb7-4e62-86cc-45e08b7dac9f.png)

3. 테스트 데이터 범위와 규칙 문자열
    > 테스트 데이터에서 문자열로 표현되는 부분을 만들어 내는 규칙을 설명한 문서입니다. 
![image](https://user-images.githubusercontent.com/1683771/163735686-d088e819-3f8b-4707-b60c-95234d9fa899.png)

4. 테스트 데이터 범위와 규칙 숫자형
    > 테스트 데이터에서 숫자형으로 표현되는 부분을 만들어 내는 규칙을 설명한 문서입니다. 주로 특정 숫자의 범위 안에서 만들어 내는 방법입니다.
![image](https://user-images.githubusercontent.com/1683771/163735698-52f670df-d524-4ebc-9a8b-e0a9441583cc.png)

5. 테스트 데이터 범위와 규칙 날짜형
    > 테스트 데이터에서 날짜형식을 만들어 내는 규칙을 설명한 문서입니다.
![image](https://user-images.githubusercontent.com/1683771/163735706-79296f53-a32e-461d-a3a2-2c213bb25205.png)

6. 테스트 데이터 범위와 규칙 랜덤키
    > 테스트 데이터에서 랜덤하게 유일한 키 값을 만들어 내는 규칙을 설명한 문서입니다.
![image](https://user-images.githubusercontent.com/1683771/163735719-c19859a3-9ada-4e64-a4e1-ddc2d57ab2e0.png)

7. 테스트 데이터 범위와 규칙 참조형
    > 이미 사용되고 있는 테스트 데이터 변수를 참조해서 같은 값을 만들어 내는 규칙을 설명한 문서입니다.
![image](https://user-images.githubusercontent.com/1683771/163735727-a27f197b-3d80-417e-90c2-516ce8fa2610.png)

8. JMeter에 Plugin 추가 방법
> JMeter 5.4.1 버전 사용함.
  1. JMeter 설치폴더/lib/ 아래에 3개 jar 파일 복사 (gson-2.8.6.jar,groovy-all-2.3.11.jar,kafka-clients-3.0.0.jar)
  2. JMeter 설치폴더/lib/ext/ 아래에 1개 jar 파일 복사 (safe-test-jmeter-plugin-1.0-SNAPSHOT.jar)
  3. JMeter 설치폴더/bin/ 아래에 fds/data 폴더 생성 (위 3가지를 준비 하시고, JMeter를 실행 하시면 됩니다.)



![image](https://user-images.githubusercontent.com/1683771/165467651-f8d658f1-c8b1-45cd-ae66-abe4d585ccbc.png)

