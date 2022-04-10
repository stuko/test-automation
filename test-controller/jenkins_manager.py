import requests
import base64
import json
import time, datetime
from jmeter_manager import JmeterManager

class JenkinsManager:
    
    def __init__(self):
        self.building = False
        # self.url = 'http://192.168.57.254:8088'
        # self.token = '118ee24a6adf47050567e1a326d4096051' # '118ee24a6adf47050567e1a326d4096051' # '11de52d0d8f0e10f725d0e47b1d44e152d'
        self.jm = JmeterManager()
        
    def build(self, project_id):
        job = self.jm.get_project_detail(project_id)
        if job == None:
            return 2
        job_name = job["jenkins_project_name"]
        job_url = job["jenkins_server_url"]
        job_token = job["jenkins_token"]
        res = requests.get(f'{job_url}/job/{job_name}/build?token={job_token}')
        print(res.text)
        return 1
    
    def complete(self, project_id):
        self.building = False
        
    def check(self, project_id):
        current_time = datetime.datetime.now()
        job = self.jm.get_project_detail(project_id)
        if job == None:
            print (f'job does not exist')
            return 2
        job_name = job["jenkins_project_name"]
        job_url = job["jenkins_server_url"]
        job_token = job["jenkins_token"]
        res = requests.get(f'{job_url}/job/{job_name}/lastBuild/api/json?depth=1')
        print(f'{job_url}/job/{job_name}/lastBuild/api/json?depth=1')
        j = res.json() 
        if j.get('timestamp') == None :
            print(f'timestamp does not exist')
            return 0
        if j.get('building') != None and j.get('building') == True : 
            print(f'building progress is [{j.get("building")}]')
            print(f'result does not exist [{j.get("result")}]')
            return 0
        build_time = datetime.datetime.fromtimestamp(int(j['timestamp'])/1000)
        time_gap = current_time - build_time
        
        print (f'elapsed time is {time_gap.total_seconds()}')
        print (f'jenkins building is {j.get("building")}')
        print (f'controller building is {self.building}')
        print (f'result is {j.get("result")}')
        
        if self.building == False and time_gap.total_seconds() > 30 :
            print(f'building elapsed time exceeds over 30 seconds [{time_gap.total_seconds()}]')
            self.building = True
            return 0

        msg = f'---> [{job_name}] build status: {j.get("result")}'
        print(msg)
        
        if self.building == True or (j.get('building') != None and j.get('building') == False) :
            if j.get('result') == 'SUCCESS' :
                return 1
            elif j.get('result') == 'FAILURE' :
                return 2
            else:
                return 0
        else:
            return 0
