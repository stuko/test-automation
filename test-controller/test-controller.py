from flask import Flask, request, jsonify
from mattermost_manager import MatterMostManager
from jmeter_manager import JmeterManager
from kanboard_manager import KanboardManager
from jenkins_manager import JenkinsManager
from os.path import exists
import time
import json
import datetime
import os, sys, socket

from pymongo import MongoClient
from pymongo.cursor import CursorType

app = Flask(__name__) 
config_file = "./volume/config/config.json"
upload_folder = "./volume/upload/"
result_folder = "./volume/result/"

@app.route('/controller', methods=['POST','GET']) 
def controller():
    # print('------ start parameter !!! ---------')
    # print(request.data) 
    # print('------ end parameter !!! -----')
    params = request.get_json()
    
    # ----------------------------------------------
    # 개발 진행 중....
    # ----------------------------------------------
    # print(params)
    # 모든 요청에 대해서, Column 이 
    # 프러덕트 백로그 -> 스프린트 백로그 -> 개발 -> 테스트 자동화 -> 테스트 리뷰 -> 테스트 완료 -> 릴리즈
    # 로 되어 있는지 확인 후, 안되어 있으면, 생성 해줌.
    # km.check_project_is_scrum(params)
    # km.make_project_to_scrum(params)
    
    print(params)
    ##############################################
    # 프로젝트 정보 가져오기
    ##############################################
    km.check_project_is_scrum(params["event_data"]["task"]["project_id"])
    
    if(params["event_name"] == "task.create"):
        print('Check Project Creation')
    
    ##############################################
    # Kanboard 이벤트 중 Move 이벤트 추출 하기
    ##############################################
    if(params["event_name"] == "task.move.column"):
        task_id = params["event_data"]["task_id"]
        task = params["event_data"]["task"]
        project_id = task["project_id"]
        owner_id = task["owner_id"]
        position = task["position"]
        category_id = task["category_id"]
        creator_id = task["creator_id"]
        swimlane_id = task["swimlane_id"]
        src_column_id = params["event_data"]["changes"]["src_column_id"]
        dst_column_id = params["event_data"]["changes"]["dst_column_id"]
        project_name = task["project_name"]
        column_title = task["column_title"]
        task_title = task["title"]
        description = task["description"]
        
        if(column_title.find(km.get_column(0)) >= 0):
            print("you moved to {}", km.get_column(0))
            mm.send_create_requirement(project_id,project_name, task_title,description)
        if(column_title.find(km.get_column(1)) >= 0): 
            print("you moved to {}", km.get_column(1))           
            mm.send_create_sprint_backlog(project_id,project_name, task_title,description)      
            
        if(column_title.find(km.get_column(2)) >= 0):            
            print("you moved to {}", km.get_column(2))
            mm.send_start_dev(project_id,project_name, task_title,description)      
            
        ##############################################
        # 테스트 자동화 단계로 이동한 경우 처리 하기
        ##############################################
        if(column_title.find(km.get_column(3)) >= 0):            
            print(f"let's start test of {km.get_column(3)}")
            #------------------------------------------
            # 메타 모스트로 테스트 자동화 시작 메시지 전송하기
            #------------------------------------------
            mm.send_execute_test_automation(project_id,project_name, task_title,description)      
            forward = km.get_next_position(project_id, task_id , 1)
            backward = km.get_next_position(project_id, task_id , -1)

            #------------------------------------------
            # JMeter 정보 가져오기
            #------------------------------------------
            find_map = jm.get_jmx_file_name(project_id)
            jmx_file_name = find_map['jmx_file_name']
            before_test_exec_shell = ''
            if 'before_test_exec_shell' in find_map.keys():
                before_test_exec_shell = find_map['before_test_exec_shell'] 
            
            test_exec_shell = find_map['test_exec_shell'] 
            
            if before_test_exec_shell != None:
                print("before test exec shell : " + before_test_exec_shell)
                jm.execute_shell_command(before_test_exec_shell , False)
            else:
                print("not exist before test exec shell")

            #------------------------------------------
            # 젠킨스 빌드하기
            #------------------------------------------
            if jkm.build(project_id) != 1:
                mm.send_execute_jenkins_build_fail(project_id,project_name, task_title,description)  
                return 'error'
            
            complete = -1
            alerady_send = False
            #------------------------------------------
            # 젠킨스 빌드 완료 시점까지 대기 하기
            #------------------------------------------
            while True:
                time.sleep(3)
                complete = jkm.check(project_id)
                print(f'jenkins building status is {complete}')
                if complete == 1 : 
                    mm.send_execute_jenkins_build_success(project_id, project_name,task_title,description)      
                    break
                elif complete == 2 : 
                    mm.send_execute_jenkins_build_fail(project_id,project_name, task_title,description)      
                    break
                else:
                    time.sleep(10)
                    if alerady_send != True:
                        mm.send_execute_jenkins_build_ing(project_id,project_name, task_title,description)      
                        alerady_send = True
            #------------------------------------------
            # 젠킨스 빌드 완료 됨 
            #------------------------------------------
            jkm.complete(project_id)
            
            #------------------------------------------
            # 젠킨스 빌드 실패 한 경우 처리하기
            #------------------------------------------
            if complete != 1:
                contents = ""
                mm.send_execute_jenkins_build_error(project_id,project_name, task_title,contents)  
                km.move(project_id, task_id, swimlane_id, backward , position)
                return 'error of building'    

            #------------------------------------------
            # 젠킨스 빌드 실패시 메타 모스트로 메시지 보내기
            #------------------------------------------
            mm.send_execute_jenkins_build_complete(project_id,project_name, task_title,description)  
            
            if test_exec_shell != None:
                print("test exec shell : " + test_exec_shell)
                jm.execute_shell_command(test_exec_shell , False)
            else:
                print("not exist test exec shell")

            #------------------------------------------
            # JMeter 실행 할 파일이 없는 경우 에러 메시지 보내기
            # Kanboard 이전 단계로 이동 시키키
            #------------------------------------------
            if jmx_file_name == None:
                mm.send_jmeter_jenkins_relation_error(project_id,project_name, task_title,description)  
                km.move(project_id, task_id, swimlane_id, backward, position)
                return "error"
            #------------------------------------------
            # JMeter 실행 하기 위해, 결과 파일 미리 생성하기
            #------------------------------------------
            nowDatetime = datetime.datetime.now()
            os.makedirs(result_folder, exist_ok=True)
            result_name = "result-" + project_id + "-" + nowDatetime.strftime("%Y%m%d%H%M%S")
            result_file_name = result_folder + result_name

            #------------------------------------------
            # JMeter를 Non GUI 모드로 실행 하기
            #  (1) 테스트 대상 서버가 실행 중이어 야 함. (O) - ShellServer에서 startup.sh 실행 해줌.
            #  (2) 테스트 대상 서버의 테스트 데이터(환경)가 준비 되어야 함. (O) - 테스트 데이터를 카피 하도록 함.
            #  (3) 테스트 결과 정보를 메타 모스트 메시지로 전송해야 함.
            #  (4) 테스트 결과 정보를 DB에 저장 해야 함. / 테스트가 종료 되면 테스트 데이터 수집 프로세스도 중지 되어야 함.
            #  (5) 테스트 결과 중 결함이 발견되면, 결함을 -> 요구사항으로 등록 해야 함.
            #------------------------------------------
            jm.execute_shell_command(jm.get_shell_command(upload_folder + jmx_file_name , result_file_name), False)
            # result_string = open("/app/server/volume/result/" + result_name, 'r').read()
            #------------------------------------------
            # 테스트 자동화 완료 메시지 보내기
            #------------------------------------------
            mm.send_test_automation_complete(project_id,project_name, task_title,result_file_name)              
            
            #------------------------------------------
            # 테스트 요청 프로세스 삭제
            #------------------------------------------
            # jm.stop_test()
            
            #------------------------------------------
            # Kanboard를 다음 단계로 이동 시키키
            #------------------------------------------
            km.move(project_id, task_id, swimlane_id, forward, position)

        if(column_title.find(km.get_column(4)) >= 0):            
            print("you moved to {}", km.get_column(4))
            mm.send_execute_test_review(project_id,project_name, task_title,description)  

        if(column_title.find(km.get_column(5)) >= 0):            
            print("you moved to {}", km.get_column(5))
            mm.send_test_complete(project_id,project_name, task_title,description)  

        if(column_title.find(km.get_column(6)) >= 0):            
            print("you moved to {}", km.get_column(6))
            mm.send_release(project_id,project_name, task_title,description)  

    return 'controller!' 

@app.route('/backlog', methods=['POST','GET']) 
def backlog(): 
    return 'backlog!' 

@app.route('/sprintlog', methods=['POST','GET']) 
def sprintlog(): 
    return 'spring log!!!!!' 

@app.route('/develop', methods=['POST','GET']) 
def develop(): 
    return 'develop!!!!' 

@app.route('/testing', methods=['POST','GET']) 
def testing(): 
    return 'testing..!' 

@app.route('/review', methods=['POST','GET']) 
def review(): 
    params = request.get_json()
    return km.move_to_forward(params)

@app.route('/release', methods=['POST','GET']) 
def release(): 
    return 'release.....!' 

@app.route('/get_project_list', methods=['POST','GET']) 
def get_project_list(): 
    return json.dumps(km.get_project_lists())

@app.route('/get_project_detail', methods=['POST','GET']) 
def get_project_detail():
    params = request.get_json() 
    return json.dumps(jm.get_project_detail(params['project_id']))

@app.route('/get_project_detail_by_jmx', methods=['POST','GET']) 
def get_project_detail_by_jmx():
    params = request.get_json() 
    result = json.dumps(jm.get_project_detail_by_jmx(params['jmx_file_name']))
    return result


@app.route('/save_project_info', methods=['POST','GET']) 
def save_project_info(): 
    params = request.get_json()
    return json.dumps(jm.save_project_info(params))


@app.route('/save_factors', methods=['POST','GET']) 
def save_factors(): 
    params = request.get_json()
    return json.dumps(jm.save_factors(params))

@app.route('/save_run_config', methods=['POST','GET']) 
def save_run_config(): 
    params = request.get_json()
    return json.dumps(jm.save_run_config(params))

    
@app.route("/upload", methods=["POST"])
def upload():
    file = request.files['file']
    os.makedirs(upload_folder, exist_ok=True)
    file.save(os.path.join(upload_folder, file.filename))
    return "upload"

    
if __name__ == '__main__': 
    
    mongo_ip = socket.gethostbyname(socket.gethostname())
    mongo_port = 27017
    
    file_exists = exists(config_file)
    
    if file_exists : 
        
        f = open(config_file,'r')
        config_json = json.load(f)
        
        if config_json['mongo_ip'] != None and config_json['mongo_port'] != None :
            # 몽고 DB에 접속한다.
            # 입력받은 IP와 PORT로 접속한다.
            # Config 정보를 읽어 온다.
            # 없으면, Default를 생성한다.
            '''
            config :
                flask { port : },
                kanboard { ip :  , port : , token : , id : , pw : , db : },
                mattermost { url :  },
                jmeter { path : }
            '''
            # X : Flask Port
            # Mongodb IP, Port 
            # Upload Path
            # kanboard (ip, port, token , id, pw, db)
            # mattermost (url)
            # JMeter (Mongo ip, Mongo port, jmeter_path)
            mongo_ip = config_json['mongo_ip']
            mongo_port = config_json['mongo_port']
        
    print(f'Mongo ip is {mongo_ip} , port is {str(mongo_port)}')
        
    mongo = MongoClient(mongo_ip, mongo_port)
    db = mongo['auto']
    if db == None:
        print("##### Mongo DB does not have auto DataBase")
        exit() 
    
    config = db['config']
    if config == None:
        print("##### auto DB does not have config collection")
        exit() 

    config_collection = config.find()
    l = list(config_collection)
    
    if l == None or len(l) == 0:
        print("##### You have to insert config collection")
        config.insert_one(
            {
                'flask' : { 'port' : config_json['flask_port']},
                'kanboard' : { 'ip' : config_json['kanboard_ip'] , 
                              'port' : config_json['kanboard_port'], 
                              'token' : config_json['kanboard_token'] , 
                              'id' : config_json['kanboard_id'], 
                              'pw' : config_json['kanboard_pw'], 
                              'db' : config_json['kanboard_db']},
                'mattermost' : { 'url' : config_json['mattermost_url'] },
                'jmeter' : { 
                    'jmeter_ip' : config_json['jmeter_ip'],
                    'jmeter_port' : config_json['jmeter_port'],
                    'path' : config_json['jmeter_path']}
            }
        )
    collection = config.find()[0]
    print(collection)
    
    if collection != None :
        
        flask_port = collection['flask']['port']
        kanboard_ip = collection['kanboard']['ip']
        kanboard_port = collection['kanboard']['port']
        kanboard_token = collection['kanboard']['token']
        kanboard_id = collection['kanboard']['id']
        kanboard_pw = collection['kanboard']['pw']
        kanboard_db = collection['kanboard']['db']
        mattermost_url = collection['mattermost']['url']
        jmeter_ip = collection['jmeter']['jmeter_ip']
        jmeter_port = collection['jmeter']['jmeter_port']
        jmeter_path = collection['jmeter']['path']
        
        global km , mm, jm, jkm
        km = KanboardManager(kanboard_ip,str(kanboard_port),kanboard_token,kanboard_id,kanboard_pw,kanboard_db)
        jm = JmeterManager(mongo_ip,mongo_port,jmeter_ip,jmeter_port,jmeter_path)
        mm = MatterMostManager(mattermost_url, jm)
        jkm = JenkinsManager(jm)
    
        app.run(host="0.0.0.0",debug=True, port=flask_port)
    else :
        print("Please input config info to MongoDB")
