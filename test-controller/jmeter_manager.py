import json
from pymongo import MongoClient
from pymongo.cursor import CursorType
import subprocess
import socket

class JmeterManager:
    
    def __init__(self, ip, port, jmeter_ip, jmeter_port, jmeter_path):
        self.jmeter_ip = jmeter_ip
        self.jmeter_port = jmeter_port
        self.client = MongoClient(ip,port)
        self.db = self.client['auto']
        self.collection = self.db['test']
        self.path = jmeter_path
        
    def save_run_config(self, params):
        find = self.collection.find({'jmx_file_name' : params['jmx_file_name']})
        if find != None :
            self.collection.update_many({'jmx_file_name' : params['jmx_file_name'] },{'$set' : {'run' : ''}})
            self.collection.update_many({'jmx_file_name' : params['jmx_file_name'] },{'$set' : {'run' : params['run']}})
        result = {}
        if find == None:
            result['error'] = 'can not find jmx_file_name'
        else:
            result['success'] = 'run configs are updated'
        return result
        
    def save_factors(self, params):
        find = self.collection.find({'jmx_file_name' : params['jmx_file_name']})
        if find != None :
            self.collection.update_many({'jmx_file_name' : params['jmx_file_name'] },{'$set' : {'factors' : ''}})
            self.collection.update_many({'jmx_file_name' : params['jmx_file_name'] },{'$set' : {'factors' : params['factors']}})
        
        result = {}
        if find == None:
            result['error'] = 'can not find jmx_file_name'
        else:
            result['success'] = 'factors are updated'
        return result
    
    def save_project_info(self, params):
        find = self.collection.find_one({'project_id': params['project_id']})
        result = {}
        if find == None:
            result['success'] = 'project info is created'
            self.collection.insert_one(params)
        else:
            result['success'] = 'project info is updated'
            find.update(params)
            self.collection.update_many({'project_id': params['project_id']}
                                       ,{'$set' : {'jmx_file_name': params['jmx_file_name']
                                                  ,'jenkins_server_url': params['jenkins_server_url']
                                                  ,'jenkins_project_name': params['jenkins_project_name']
                                                  ,'jenkins_token': params['jenkins_token']
                                                  ,'mattermost_webhook_id': params['mattermost_webhook_id']
                                                  ,'before_test_exec_shell': params['before_test_exec_shell']
                                                  ,'test_exec_shell': params['test_exec_shell']}})
        return result
    
    def get_project_detail(self, project_id):
        project_detail = self.collection.find_one({'project_id' : project_id})
        if project_detail != None :
            del project_detail['_id']
        return project_detail

    def get_project_detail_by_jmx(self, jmx_file_name):
        project_detail = self.collection.find({'jmx_file_name' : jmx_file_name})
        if project_detail != None :
            l = list()
            for p in project_detail: 
                del p['_id']
                l.append(p)
            return l
        else: 
            return None
        
    def get_jmx_file_name(self, project_id):
        find = self.collection.find_one({'project_id' : project_id})
        if find == None :
            return None
        return find
    #################################################
    # JMeter self.path는 ShellServer에서 실행 하기 위한 Path 이므로 ./ 로 설정 됨.
    #################################################
    def get_shell_command(self, jmx_file_name , result_file_name ):
        # REMOTE
        cmd = self.path + 'jmeter -Dcmd=EXEC_JMETER_TEST -Ljmeter.engine=DEBUG -DTEST_AUTO=true -Djava.rmi.server.hostname='+ self.jmeter_ip +' -n -t ' + jmx_file_name + ' -r -l ' + result_file_name
        # LOCAL
        # cmd = self.path + 'jmeter -DTEST_AUTO=true -Djava.rmi.server.hostname='+ self.jmeter_ip +' -n -t ' + jmx_file_name 
        return cmd

    def stop_test(self):
        # self.execute_shell_command(self, self.path + 'shutdown.sh')
        # self.execute_shell_command(self, self.path + 'stoptest.sh')
        self.execute_shell_command(self, 'pkill -9 -ef EXEC_JMETER_TEST')

    def execute_shell_command(self, shell , is_root=True):
        if is_root : 
            shell = 'sudo ' + shell
        print(f'execute jemter shell : {shell}')
        client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        client_socket.connect((self.jmeter_ip, self.jmeter_port))
        client_socket.sendall(shell.encode())
        client_socket.close()
        
        # proc = subprocess.Popen(shell.split()
        #                        ,shell=True
        #                        ,stdout=subprocess.PIPE
        #                        ,stderr=subprocess.PIPE)       

