# glpat-sscjjb-3huYDsbhugatb
import requests
import base64
import json
import time, datetime
import gitlab

# 개발 진행 중...
class GitLabManager:
    
    def __init__(self):
        self.git_lab_api_url = "https://gitlab.com/api/v4/"
        self.git_lab_api_token = "glpat-sscjjb-3huYDsbhugatb"
        print('init')
    
    def get_project_list(self, params):
        url = f"{self.git_lab_api_url}?private_token={self.git_lab_api_token}"
        res = requests.get(url)
        return res.json() 
        
    def get_dump(self):
        gl = gitlab.Gitlab('https://gitlab.com', private_token=self.git_lab_api_token, api_version=4)
        gl.auth()
        project = gl.projects.get('path/to/project')
        items = project.repository_tree()
        print(items)
    
        
    def create_repository(self, params):
        print('create_repository')
        
