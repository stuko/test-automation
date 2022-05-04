import mattermost
import requests
import json

from jenkins_manager import JenkinsManager
from jmeter_manager import JmeterManager

class MatterMostManager:
    
    def __init__(self , url , jm):
        self.message = {
            'username': 'automation-bot',
            'text'    : ''
        }
        self.url = url
        self.jm = jm # JmeterManager()
    
    def send(self, mattermost_webhook_id, subject, detail):
        self.message['text'] = '## ' + subject  + '\n----\n' + '> ' + detail
        r = requests.post(
            self.url + mattermost_webhook_id ,
            data=json.dumps(self.message)
        )
        r.raise_for_status() 
    
    def send_no_markdown(self, mattermost_webhook_id, subject, detail):
        self.message['text'] = '' + subject  + detail
        r = requests.post(
            self.url + mattermost_webhook_id,
            data=json.dumps(self.message)
        )
        r.raise_for_status() 
        
    def notify(self, project_id, project_name, task_title, message, detail) :
        project = self.jm.get_project_detail(project_id)
        print(f'project id is {project_id} and project info is {project}')
        if(project != None) :
            mattermost_webhook_id = project['mattermost_webhook_id']
            self.send(mattermost_webhook_id,message,detail)
            
    def notify_no_markdown(self, project_id,project_name, task_title, message, detail) :
        project = self.jm.get_project_detail(project_id)
        if(project != None) :
            mattermost_webhook_id = project['mattermost_webhook_id']
            self.send_no_markdown(mattermost_webhook_id,message,detail)
        
    def send_arrange_project(self,project_id, project_name, task_title, task_desc):
        self.notify( project_id,project_name, task_title, f'😁😁😁 {project_name}을 스크럼 프로젝트에 맞게 정리 하였습니다.','')
        
    def send_create_requirement(self,project_id, project_name, task_title, task_desc):
        self.notify( project_id,project_name,task_title, f'😁😁😁 {project_name}에 요구사항[{task_title}]이 생성 되었습니다.',task_desc)
        
    def send_create_sprint_backlog(self,project_id, project_name, task_title, task_desc):
        self.notify( project_id,project_name, task_title, f'😁😁😁 {project_name}의 스프린트 백로그에 [{task_title}]이 생성 되었습니다.',task_desc)
        
    def send_start_dev(self,project_id, project_name, task_title, task_desc):
        self.notify( project_id,project_name, task_title, f'😁😁😁 {project_name}의 개발 작업이 진행 되고 있습니다.',task_desc)
    
    def send_execute_test_automation(self,project_id, project_name, task_title, task_desc):
        self.notify( project_id,project_name, task_title, f'😁😁😁 {project_name}의 테스트 자동화가 실행 되었습니다.',task_desc)

    def send_execute_jenkins_build(self,project_id, project_name, task_title, task_desc):
        self.notify( project_id,project_name, task_title, f'😁😁😁 {project_name}의 젠킨스 빌드가 실행 되었습니다.',task_desc)

    def send_execute_jenkins_build_success(self,project_id, project_name, task_title, task_desc):
        self.notify( project_id,project_name,  f'😁😁😁 {project_name}의 젠킨스 빌드를 성공하였습니다.',task_desc)

    def send_execute_jenkins_build_fail(self,project_id, project_name, task_title, task_desc):
        self.notify( project_id,project_name, task_title, f'😁😁😁 {project_name}의 젠킨스 빌드가 실패 하였습니다.',task_desc)

    def send_execute_jenkins_build_ing(self,project_id, project_name, task_title, task_desc):
        self.notify( project_id,project_name,task_title,  f'😁😁😁 {project_name}의 젠킨스 빌드가 진행중입니다.',task_desc)
        
    def send_execute_jenkins_build_complete(self,project_id, project_name, task_title, task_desc):
        self.notify( project_id,project_name, task_title, f'😁😁😁 {project_name}의 젠킨스 작업이 완료 되었습니다.',task_desc)        

    def send_execute_jenkins_build_error(self,project_id, project_name, task_title, task_desc):
        self.notify( project_id,project_name,task_title,  f'😁😁😁 {project_name}의 젠킨스 작업에 오류가 발생하였습니다.',task_desc)        
        
    def send_jmeter_jenkins_relation_error(self,project_id, project_name, task_title, task_desc):
        self.notify( project_id,project_name, task_title, f'😁😁😁 {project_name}의 젠킨스와 Jmeter의 연결이 되어 있지 않습니다.',task_desc)        

    def send_test_automation_complete(self,project_id, project_name, task_title, task_desc):
        self.notify( project_id,project_name,task_title,  f'😁😁😁 {project_name}의 테스트 자동화가 완료되었습니다.',task_desc)        
        
    def send_execute_test_review(self,project_id, project_name, task_title, task_desc):
        self.notify( project_id,project_name, task_title, f'😁😁😁 {project_name}의 테스트 리뷰가 실행 되었습니다.',task_desc)

    def send_test_complete(self,project_id, project_name, task_title, task_desc):
        self.notify( project_id,project_name,task_title,  f'😁😁😁 {project_name}의 테스트가 완료 되었습니다.',task_desc)
        
    def send_release(self,project_id, project_name, task_title, task_desc):
        self.notify( project_id,project_name,task_title,  f'😁😁😁 {project_name}의 릴리즈 실행 되었습니다.',task_desc)
        