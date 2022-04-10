# Kabboard API
'''
1. Task API : 테스트 자동화에서 테스트 상태를 변경해 주는 기능 필요
2. 
'''

# 젠킨스 API
'''
API Token 설정을 할 수 있는 메뉴
- Jenkins 메뉴 > 사람 > token 발행할 user 명 > 설정
API 사용법
- http://[jenkins url]/api

Job 생성 [POST]
http://[jenkins url]/createItem?name=[job name]
Job 조회 [GET]
http://[jenkins url]/job/[job name]/api/json or xml
Job 빌드 수행 [POST]
http://[jenkins url]/job/[job name]/build
Job 빌드 결과 조회 [GET]
http://[jenkins url]/job/[job name]/[build number]/api/json or xml
Job 빌드 결과 조회 - 마지막 성공 빌드 [GET]
http://[jenkins url]/job/[job name]/lastStableBuild/api/json or xml
'''

import requests
import base64
import kanboard

url = "http://localhost:8080/jsonrpc.php"
token = base64.b64encode('stuko:kcb1234!'.encode('ascii')).decode('ascii')
kb = kanboard.Client(url, 'jsonrpc', '7f888ec470709090eb52f11de8f731f7c937c9b7d84d662575c42c494baa')
project_id = kb.create_project(name='My project')
print(project_id)
kb = kanboard.Client(url, 'stuko', 'kcb1234!')
projects = kb.get_my_projects()

# user_agent = 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.47 Safari/537.36'
# user_agent_name = 'User-Agent'
# header = {user_agent_name : user_agent, 'X-API-Auth': '{token}'}
# param = {"jsonrpc": "2.0", "method": "getAllProjects", "id": 1}
# response = requests.post(url,headers=header,data=param)
# print(response.text)