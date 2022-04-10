from flask import Flask, request, jsonify
from mattermost_manager import MatterMostManager
from jmeter_manager import JmeterManager
from kanboard_manager import KanboardManager
from jenkins_manager import JenkinsManager
import time
import json
import datetime
import os

km = KanboardManager()
mm = MatterMostManager()
jm = JmeterManager()
jkm = JenkinsManager()

app = Flask(__name__) 
upload_folder = "D:\\install\\apache-jmeter-5.4.1\\bin\\upload\\"

@app.route('/controller', methods=['POST']) 
def controller(): 
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
    
    km.check_project_is_scrum(params["event_data"]["task"]["project_id"])
    
    if(params["event_name"] == "task.create"):
        print('Check Project Creation')
    
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
            mm.send_create_requirement(project_id,project_name, task_title,description)
        if(column_title.find(km.get_column(1)) >= 0):            
            contents = f"""
### 😁😁😁 스프린트 백로그에 요구사항({task_title})이 생성 되었습니다.
|title|contents|
|---|---|
|{task_title}|{description}|
                  """
            mm.send_create_sprint_backlog(project_id,project_name, task_title,contents)      
            
        if(column_title.find(km.get_column(2)) >= 0):            
            mm.send_start_dev(project_id,project_name, task_title,description)      
        if(column_title.find(km.get_column(3)) >= 0):            
            mm.send_execute_test_automation(project_id,project_name, task_title,description)      
            forward = km.get_next_position(project_id, task_id , 1)
            backward = km.get_next_position(project_id, task_id , -1)
            
            if jkm.build(project_id) != 1:
                mm.send_execute_jenkins_build_fail(project_id,project_name, task_title,description)  
                return 'error'
            
            complete = -1
            alerady_send = False
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
            
            jkm.complete(project_id)
            
            if complete != 1:
                contents = f"""
### 😨😨😨 젠킨스 빌드가 완료되었으나, 테스트가 가능한 상태가 아닙니다.
|title|contents|
|---|---|
|{task_title}|{description}|
                  """
                mm.send_execute_jenkins_build_error(project_id,project_name, task_title,contents)  
                km.move(project_id, task_id, swimlane_id, backward , position)
                return 'error of building'    
            
            mm.send_execute_jenkins_build_complete(project_id,project_name, task_title,description)  
            jmx_file_name = jm.get_jmx_file_name(project_id)
            if jmx_file_name == None:
                mm.send_jmeter_jenkins_relation_error(project_id,project_name, task_title,description)  
                km.move(project_id, task_id, swimlane_id, backward, position)
                return "error"
            jm.execute_shell_command(jm.get_shell_command(upload_folder + jmx_file_name))

            mm.send_test_automation_complete(project_id,project_name, task_title,description)              
            km.move(project_id, task_id, swimlane_id, forward, position)

        if(column_title.find(km.get_column(4)) >= 0):            
            mm.send_execute_test_review(project_id,project_name, task_title,description)  

        if(column_title.find(km.get_column(5)) >= 0):            
            mm.send_test_complete(project_id,project_name, task_title,description)  

        if(column_title.find(km.get_column(6)) >= 0):            
            mm.send_release(project_id,project_name, task_title,description)  

    return 'controller!' 

@app.route('/backlog') 
def backlog(): 
    return 'backlog!' 

@app.route('/sprintlog') 
def sprintlog(): 
    return 'spring log!!!!!' 

@app.route('/develop') 
def develop(): 
    return 'develop!!!!' 

@app.route('/testing') 
def testing(): 
    return 'testing..!' 

@app.route('/review') 
def review(): 
    params = request.get_json()
    return km.move_to_forward(params)

@app.route('/release') 
def release(): 
    return 'release.....!' 

@app.route('/get_project_list', methods=['POST','GET']) 
def get_project_list(): 
    return km.get_project_lists()

@app.route('/get_project_detail', methods=['POST','GET']) 
def get_project_detail():
    params = request.get_json() 
    return jm.get_project_detail(params['project_id'])

@app.route('/get_project_detail_by_jmx', methods=['POST','GET']) 
def get_project_detail_by_jmx():
    params = request.get_json() 
    result = json.dumps(jm.get_project_detail_by_jmx(params['jmx_file_name']))
    return result


@app.route('/save_project_info', methods=['POST','GET']) 
def save_project_info(): 
    params = request.get_json()
    return jm.save_project_info(params)


@app.route('/save_factors', methods=['POST','GET']) 
def save_factors(): 
    params = request.get_json()
    return jm.save_factors(params)

@app.route('/save_run_config', methods=['POST','GET']) 
def save_run_config(): 
    params = request.get_json()
    return jm.save_run_config(params)

    
@app.route("/upload", methods=["POST"])
def upload():
    file = request.files['file']
    os.makedirs(upload_folder, exist_ok=True)
    file.save(os.path.join(upload_folder, file.filename))
    return "upload"

    
if __name__ == '__main__': app.run(host="0.0.0.0",debug=True, port="5000")
